package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.message.RedisPublisher;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.service.QuoteRecordService;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import com.cheche365.cheche.core.util.URLUtils;
import com.cheche365.cheche.sms.client.service.ValidatingService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.service.CaptchaImageService;
import com.cheche365.cheche.core.service.WebPurchaseOrderService;
import com.cheche365.cheche.web.service.security.throttle.SmsValidationUtils;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.cheche365.cheche.core.constants.SmsConstants._SMS_PRODUCT_CHECHE;
import static com.cheche365.cheche.core.constants.SmsConstants._SMS_PRODUCT_KEY;
import static com.cheche365.cheche.core.message.IPFilterMessage.CODE_SMS;
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.CUSTOMER_QUOTE_ORDER_NORMAL_ONLINE;
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.CUSTOMER_QUOTE_ORDER_THIRDPARTNER;

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/sms")
@VersionedResource(from = "1.0")
public class SmsResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected ConditionTriggerHandler conditionTriggerHandler;

    @Autowired
    private WebPurchaseOrderService webOrderService;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    ValidatingService validationCodeService;

    @Autowired
    private CaptchaImageService captchaImageService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisPublisher redisPublisher;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> sendValidationCodeForNewUser(@RequestParam String mobile, @RequestParam String purpose, HttpServletRequest request) {
        SmsValidationUtils.validateRequest(CODE_SMS, request, redisTemplate, redisPublisher);
        if (!ValidatingService.PurposeEnum.containPurpose(purpose)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "参数异常");
        }
        Channel channel = ClientTypeUtil.getChannel(request);

        return this.getResponseEntity(ConditionTriggerUtil.sendValidateCodeMessage(conditionTriggerHandler, mobile, channel));
    }

    @RequestMapping(value = "/validation", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> sendValidation(@RequestParam String mobile, @RequestParam(value = "action", required = false, defaultValue = "action") String action, @RequestParam(required = false) String imageCode, HttpServletRequest request) {


        if (captchaImageService.needSupplyCaptchaImage(session.getId(), imageCode)) {
            captchaImageService.validate(session.getId(), imageCode);
        } else {
            SmsValidationUtils.validateRequest(CODE_SMS, request, redisTemplate, redisPublisher);
        }

        Channel channel = ClientTypeUtil.getChannel(request);
        if (null != action) {
            if ("newuser".equals(action)) {
                return this.getResponseEntity(ConditionTriggerUtil.sendValidateCodeMessage(conditionTriggerHandler, mobile, channel));
            } else if ("login".equals(action)) {
                cacheCaRegisterChannel(request);
                return this.getResponseEntity(ConditionTriggerUtil.sendValidateCodeMessage(conditionTriggerHandler, mobile, channel));
            } else if ("mobile".equals(action)) {
                return this.getResponseEntity(ConditionTriggerUtil.sendValidateCodeMessage(conditionTriggerHandler, mobile, channel));
            } else if ("password".equals(action)) {
                return this.getResponseEntity(ConditionTriggerUtil.sendValidateCodeMessage(conditionTriggerHandler, mobile, channel));
            } else if ("marketing".equals(action)) {
                return this.getResponseEntity(ConditionTriggerUtil.sendValidateCodeMessage(conditionTriggerHandler, mobile, 4, channel));
            } else {
                return this.getResponseEntity(ConditionTriggerUtil.sendValidateCodeMessage(conditionTriggerHandler, mobile, channel));
            }
        } else {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "非预期参数 action ");
        }
    }

    @RequestMapping(value = "/validation/valid", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> validValidationCode(@RequestParam String mobile, @RequestParam String verificationCode, HttpServletRequest request) {

        Map<String, String> additionalParam = new HashMap<>();
        additionalParam.put(_SMS_PRODUCT_KEY, _SMS_PRODUCT_CHECHE);
        return this.getResponseEntity(validationCodeService.validate(mobile, verificationCode, additionalParam));
    }

    @RequestMapping(value = "/common", consumes = "application/json", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<Object>> commonSmsAssist(@RequestBody Map body) {

        User user = this.currentUser();
        String mobile = body.get("mobile") == null ? user.getMobile() : String.valueOf(body.get("mobile"));

        if (body.get("orderNo") == null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "输入参数不合法");
        }

        PurchaseOrder purchaseOrder = this.webOrderService.findFirstByOrderNo(String.valueOf(body.get("orderNo")), user);

        sendOrderPaySms(mobile, purchaseOrder);

        return new ResponseEntity<>(new RestResponseEnvelope("success"), HttpStatus.OK);
    }

    private void sendOrderPaySms(String mobile, PurchaseOrder purchaseOrder) {
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put(SmsCodeConstant.MOBILE, mobile);
        parameterMap.put(SmsCodeConstant.ORDER_ORDER_NO, purchaseOrder.getOrderNo());
        parameterMap.put(SmsCodeConstant.TYPE, purchaseOrder.getSourceChannel().isThirdPartnerChannel() ? CUSTOMER_QUOTE_ORDER_THIRDPARTNER.getId().toString() : CUSTOMER_QUOTE_ORDER_NORMAL_ONLINE.getId().toString());
        ConditionTriggerUtil.getThirdPartnerParams(purchaseOrder.getSourceChannel().getId(), parameterMap);
        parameterMap.put(SmsCodeConstant.M_PAYMENT_LINK, purchaseOrder.getOrderNo());
        conditionTriggerHandler.process(parameterMap);
    }

    private void cacheCaRegisterChannel(HttpServletRequest request) {
        String refer = request.getHeader(HttpHeaders.REFERER);
        if (StringUtils.isNotBlank(refer) && refer.contains("activePage/index.html")) {

            try {
                logger.info("ca qr register info refer :{}", refer);
                URI uri = new URI(refer, false);
                String queryString = uri.getQuery();
                Map<String, String> qsMap = URLUtils.splitQuery(queryString);
                Long channelId = Long.valueOf(qsMap.get("channel"));
                logger.info("current channel id :{}, refer channel id :{}", getChannel().getId(), channelId);
                ClientTypeUtil.cacheChannel(request, Channel.toChannel(channelId));
            } catch (URIException e) {
                logger.info("parse refer to uri error message:{}", ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
