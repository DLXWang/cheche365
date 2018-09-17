package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.IResourceService;
import com.cheche365.cheche.core.service.OrderRelatedService;
import com.cheche365.cheche.core.service.ThirdPartyPaymentTemplate;
import com.cheche365.cheche.core.service.ThirdPartyPaymentTemplateFactory;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.externalpayment.service.PaymentStatusPollingService;
import com.cheche365.cheche.externalpayment.service.PollingServiceFactory;
import com.cheche365.cheche.partner.api.ApiLoader;
import com.cheche365.cheche.partner.api.PartnerApi;
import com.cheche365.cheche.unionpay.payment.UnionPaySignature;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.service.PaymentChannelService;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9;
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_BOTPY_11;
import static com.cheche365.cheche.web.service.PaymentChannelService.*;
import static com.cheche365.cheche.web.util.PaymentValidationUtil.checkBeforePay;
import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes;

/**
 * Created by zhengwei on 5/29/15.
 */

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/payments")
public class PaymentsResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(PaymentsResource.class);

    @Autowired
    private PaymentChannelService paymentChannelService;
    @Autowired
    private OrderRelatedService orService;

    @Autowired
    private ThirdPartyPaymentTemplateFactory externalPayFactory;

    @Autowired
    IResourceService resourceService;

    @Autowired
    private ApiLoader apiLoader;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private PollingServiceFactory pollingServiceFactory;

    @VersionedResource(from = "1.0")
    @RequestMapping(value = "/unionpay/signature", method = RequestMethod.POST)
    public HttpEntity checkUnionPaySignature(@RequestBody Map<String, String> reqMap) throws IOException {
        String orderNo = StringUtils.trimToEmpty(reqMap.get("orderNo"));
        String signature = StringUtils.trimToEmpty(reqMap.get("signature"));
        logger.debug("验证银联控件支付订单{}返回信息签名 {}", orderNo, signature);

        if (StringUtils.isBlank(orderNo)) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "请求参数缺少订单号");
        }

        if (StringUtils.isBlank(signature)) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "请求参数缺少签名串");
        }

        PurchaseOrder purchaseOrder = orService.initByOrderNo(orderNo).getPo();
        if (null == purchaseOrder) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "订单号不存在， " + orderNo);
        }

        Map<String, Boolean> booleanMap = new HashMap<>();

        boolean result = UnionPaySignature.validateAppResponse(signature, "UTF-8");
        booleanMap.put("result", result);
        logger.debug("验证银联控件支付订单{}验签结果: {}", orderNo, result);

        return getResponseEntity(booleanMap);
    }

    @RequestMapping(value = "/status/paid", method = RequestMethod.POST)
    public HttpEntity payConfirm(@RequestBody Map<String, String> params){
        Map<String, Boolean> result =  new HashMap<>();

        String orderNo = params.get("orderNo").trim();
        PurchaseOrder po = purchaseOrderRepository.findFirstByOrderNo(orderNo);
        if(po == null || !OrderStatus.Enum.PENDING_PAYMENT_1.equals(po.getStatus())){
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "确认支付订单不存在");
        }

        QuoteRecord qr = quoteRecordRepository.findOne(po.getObjId());
        if (AGENTPARSER_9.equals(qr.getType()) || PLATFORM_BOTPY_11.equals(qr.getType())) {
            logger.info("用户点击确认支付，开始调用支付状态轮询服务，订单号：{}", orderNo);
            PaymentStatusPollingService service = pollingServiceFactory.getService(qr.getType());
            service.poll(po, session.getId());
        } else {
            logger.info("不属于小鳄鱼或金斗云订单，或者订单已确认支付，不做处理");
        }

        result.put("isConfirmed", true);
        return getResponseEntity(result);
    }


    @VersionedResource(from = "1.0",to = "1.6")
    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    public HttpEntity getPaymentChannels(@RequestParam(value = "orderNo") String orderNo, HttpServletRequest request) {

        return getChannels(orderNo,request,false);
    }

    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    @VersionedResource(from = "1.7")
    public HttpEntity getPingPlusPaymentChannels(@RequestParam(value = "orderNo") String orderNo, HttpServletRequest request) {
        return getChannels(orderNo,request,true);
    }

    private HttpEntity getChannels(String orderNo, HttpServletRequest request , boolean newVersion){
        if(RuntimeUtil.isNonAuto(orderNo)){
            return getResponseEntity(paymentChannelService.nonAutoChannels(orderNo, request));
        }

        Channel channel = ClientTypeUtil.getChannel(request);
        logger.debug("{} get payment channels request channel : {}, header.referer : {}", orderNo, channel.getName(), request.getHeader("Referer"));

        OrderRelatedService.OrderRelated or = orService.initByOrderNo(orderNo);
        if (newVersion && (or.isPingPlusAdditional() || or.findDailyRestart() != null)) {
            newVersion = false;
        }

        checkBeforePay(or, channel, newVersion, request);

        ThirdPartyPaymentTemplate thirdPartyPayment = externalPayFactory.getTemplate(or.getQr());
        if(isThirdPartyPaymentChannel(or.getQr())){
            List<PaymentChannel> paymentChannels=(List<PaymentChannel>) thirdPartyPayment.prePay(or.getPo(), channel, or.getQr()) ;
            return getHttpEntity(paymentChannels);
        }
        if (null != thirdPartyPayment) {
            return getResponseEntity(thirdPartyPayment.prePay(or.getPo(), channel, or.getQr()));
        }
        if (newVersion && request.getHeader("Referer") == null && Channel.selfApp().contains(channel)) {
            return getResponseEntity(buildAppH5Form(or.getPo(), channel));
        }

        if (null != channel.getApiPartner()) {
            PartnerApi partnerApi = apiLoader.findApi(channel.getApiPartner());
            PurchaseOrder purchaseOrder = orService.initByOrderNo(orderNo).getPo();
            List<PaymentChannel> paymentChannels = partnerApi.getPaymentChannels(purchaseOrder);
            if (CollectionUtils.isNotEmpty(paymentChannels)) {
                return getHttpEntity(paymentChannels, newVersion, channel);
            }
        }

        List<PaymentChannel> paymentChannels = newVersion ? getPingPlusByChannel(channel) : getByChannel(channel);
        logger.debug("get payment channels paymentChannels : {}", CacheUtil.doJacksonSerialize(paymentChannels));

        Payment paid = or.findLastPaid();
        if (null != paid) {
            logger.debug("get payment channels paid payment channel : {}", CacheUtil.doJacksonSerialize(paid.getChannel()));
            paymentChannels = filterPaidChannel(paymentChannels, paid);
        }
        logger.debug("get payment channels result paymentChannels : {}", CacheUtil.doJacksonSerialize(paymentChannels));

        paymentChannelService.addOAuthUrl(paymentChannels, paid, channel, orderNo, newVersion);
        if(CollectionUtils.isEmpty(paymentChannels)){
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "无可用支付方式");
        }
        return getHttpEntity(paymentChannels,newVersion,channel);
    }

    Boolean isThirdPartyPaymentChannel(QuoteRecord qr){
        return qr.getInsuranceCompany().useChecheCashier() || QuoteSource.Enum.PLATFORM_SOURCES.contains(qr.getType());
    }

    private Object buildAppH5Form(PurchaseOrder po, Channel channel) {
        String form = "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title></title></head><body> <script type=\"text/javascript\">location.href=\"" + WebConstants.getIndexPath(channel) + "#pay&" + po.getOrderNo() + "\";</script></body></html>";
        Map<String, Object> result = new HashMap<>();
        result.put("form", form);
        result.put("pingPlusPay", true);
        return result;
    }

    private HttpEntity getHttpEntity(List<PaymentChannel> paymentChannels) {
        HttpEntity response;
        List<Map> pcMap = CacheUtil.doListJacksonDeserialize(CacheUtil.doJacksonSerialize(paymentChannels),Map.class);
        handleLogoUrl(pcMap);
        response = getResponseEntity(pcMap);
        return response;
    }

    private HttpEntity getHttpEntity(List<PaymentChannel> paymentChannels,boolean target,Channel channel) {
        HttpEntity response;
        List<Map> pcMap = CacheUtil.doListJacksonDeserialize(CacheUtil.doJacksonSerialize(paymentChannels),Map.class);
        handleLogoUrl(pcMap);
        handleTarget(pcMap,target);
        paymentChannelService.handleName(pcMap,channel);
        response = getResponseEntity(pcMap);
        return response;
    }

    private void handleLogoUrl(List<Map> paymentChannels) {
        Channel channel = ClientTypeUtil.getChannel(((ServletRequestAttributes) currentRequestAttributes()).getRequest());
        for (Map paymentChannel : paymentChannels) {
            String logoPath = resourceService.getResourceAbsolutePath(paymentChannel.get("logoUrl").toString());
            String channelLogoPath = logoPath.replaceAll("paymentChannel/", "paymentChannel/" + channel.getId() + "_");
            String aLogoPath = logoPath.replaceAll("paymentChannel/", "paymentChannel/a_");
            paymentChannel.put("logoUrl", resourceService.absoluteUrl(
                resourceService.getResourceUrl(exists(get(channelLogoPath)) ? channelLogoPath : channel.isLevelAgent() ? aLogoPath : logoPath)
            ));
        }
    }

    private void handleTarget(List<Map> paymentChannels,boolean target) {
        paymentChannels.forEach(paymentChannel ->
            paymentChannel.put("target",target)
        );
    }

}
