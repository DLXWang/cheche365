package com.cheche365.cheche.partner.web.controller

import com.cheche365.cheche.common.util.StringUtil
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PartnerOrderRepository
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository
import com.cheche365.cheche.web.service.ChannelAgentService
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.web.service.UserCallbackService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.partner.handler.index.PartnerIndexDirector
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.service.system.PartnerOrderDetailURL
import com.cheche365.cheche.web.service.system.PartnerPaymentURL
import com.cheche365.cheche.web.util.ClientTypeUtil
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.ws.rs.core.UriBuilder

import static com.cheche365.cheche.web.service.system.SystemURL.PAYMENT_URL_COMPANY_KEY

/**
 * 第三方首页跳转api
 */
@Controller
@RequestMapping("/partner/{code}/")
class DispatcherResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(DispatcherResource.class)

    @Autowired
    private PartnerIndexDirector partnerIndexDirector
    @Autowired
    private PurchaseOrderService orderService
    @Autowired(required = false)
    public HttpSession session
    @Autowired
    private PartnerOrderRepository partnerOrderRepository
    @Autowired
    private PurchaseOrderAmendRepository orderAmendRepository
    @Autowired
    private PartnerPaymentURL partnerPaymentPage
    @Autowired
    private PartnerOrderDetailURL partnerOrderDetailPage
    @Autowired
    private ChannelAgentService channelAgentService
    @Autowired
    UserCallbackService userCallbackService

    @RequestMapping(value = "orders", method = RequestMethod.GET)
    public String orderPage(@PathVariable("code") String partnerCode, HttpServletRequest req) {
        ApiPartner partner = ApiPartner.findByCode(partnerCode)
        String orderListUrl = partnerIndexDirector.toTargetUrl(partner, req, false, "mine/orders/0")
        return "redirect:" + orderListUrl
    }

    @RequestMapping(value = "quote/photo", method = RequestMethod.GET)
    String photoQuote(@PathVariable("code") String partnerCode, HttpServletRequest req) {
        ApiPartner partner = ApiPartner.findByCode(partnerCode)
        logger.debug("第三方合作 [{}] 进入拍照报价页面, url:{}, queryString:{}", partnerCode, req.getRequestURI().toString(), req.getQueryString())
        return "redirect:" + partnerIndexDirector.toTargetUrl(partner, req, true, "photo")
    }

    @RequestMapping(value = "index", method = RequestMethod.GET)
    String index(@PathVariable("code") String partnerCode, HttpServletRequest req) {
        ApiPartner partner = ApiPartner.findByCode(partnerCode)
        if (null == partner) {
            throw new BusinessException(BusinessException.Code.UNAUTHORIZED_ACCESS, partnerCode + " URL无效。")
        }
        logger.debug("第三方合作 [{}] 进入首页, url:{}, queryString={}", partnerCode, req.getRequestURI().toString(), req.getQueryString())

        try {
            String path = partnerIndexDirector.toIndexPageUrl(partner, req)
            logger.debug("第三方合作 [{}] 进入首页, 重定向到url:{}", partnerCode, path)
            return "redirect:" + path
        } catch (DataIntegrityViolationException e) {
            logger.error("第三方合作 [{}] 绑定手机号出错，重定向到错误提示页面，url:{}, queryString={}", partnerCode, req.getRequestURI().toString(), req.getQueryString())
            return "redirect:/m/error.html?code=" + BusinessException.Code.MULTIPLE_BOUNDED_MOBILE.getCodeValue()
        } catch (Exception e) {
            logger.error("第三方合作 [{}] 首页调整错误，重定向到错误提示页面，url:{}, queryString={}, exception = {}", partnerCode, req.getRequestURI().toString(), req.getQueryString(), e)
            String errorCode = (e instanceof BusinessException) ? ((BusinessException) e).getCode().getCodeValue().toString() : ""
            return "redirect:/m/error.html?code=" + errorCode
        }
    }

    /**
     * 第三方合作[订单详情回调链接]重定向:
     * from        : /partner/code/callback/order/T20160120000002
     * redirect to : /m/index.html?src=code#mine/order/T20160120000002
     */
    @RequestMapping(value = "/callback/order/{orderNo}", method = RequestMethod.GET)
    String orderDetailCallback(@PathVariable("code") String partnerCode,
                               @PathVariable("orderNo") String orderNo,
                               @RequestParam(value = "param", required = false) String param,
                               HttpServletRequest request, HttpServletResponse response) {
        ApiPartner partner = ApiPartner.findByCode(partnerCode)

        PurchaseOrder order = preCheckPartnerOrder(partnerCode, orderNo, partner)

        simulateLogin(param, request, partner, order)

        UriBuilder builder = UriBuilder.fromUri(getRootPath(order.getSourceChannel()))
            .queryParam("src", partnerCode)
            .queryParam("param", URLEncoder.encode(param, "UTF-8"))
            .fragment("#mine/order/" + orderNo)

        return "redirect:" + builder.build().toString()
    }


    @RequestMapping(value = "/callback/order/detail/{orderNo}", method = RequestMethod.GET)
    String orderDetailCallback(@PathVariable(value = "code") String partnerCode,
                               @PathVariable(value = "orderNo") String orderNo,
                               @RequestParam(value = "uuid") String uuid,
                               @RequestParam(value = "param", required = false) String param,
                               HttpServletRequest request){

        logger.info('第三方渠道短信链接查询订单详情，partnerCode:{}, orderNo:{}，uuid:{}', partnerCode, orderNo, uuid)

        if (!partnerOrderDetailPage.isCachedValue(orderNo, uuid)){
            logger.debug("短信查看订单详情uuid{}和orderNo{}不匹配", uuid, orderNo)
            throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "订单详情链接无效")
        }

        ApiPartner partner = ApiPartner.findByCode(partnerCode)
        PurchaseOrder order = preCheckPartnerOrder(partnerCode, orderNo, partner)

        ClientTypeUtil.cacheChannel(request, Channel.findByApiPartner(partner))
        userCallbackService.cacheUserCallbackInfo(request, uuid, orderNo, order.getApplicant(), order.getSourceChannel())
        logger.debug("第三方合作[订单详情回调链接]，向当前session添加user: {}", order.getApplicant().getId())

        return "redirect:" + partnerOrderDetailPage.toClientPage(partner, order, uuid, param)
    }



    /**
     * 第三方合作[订单支付回调链接]重定向:
     * from        : /partner/code/callback/order/payment/T20160219000001
     * redirect to : /m/index.html?src=code#pay&T20160219000001
     */
    @RequestMapping(value = "/callback/order/payment/{orderNo}", method = RequestMethod.GET)
    String orderPayCallback(@PathVariable("code") String partnerCode,
                            @PathVariable("orderNo") String orderNo,
                            @RequestParam(value = "uuid") String uuid,
                            @RequestParam(value = "param", required = false) String param,
                            HttpServletRequest request) {

        logger.debug(" third partner order no is {}, uuid is {}", orderNo, uuid)
        ApiPartner partner = ApiPartner.findByCode(partnerCode)

        PurchaseOrder order = preCheckPartnerOrder(partnerCode, orderNo, partner)

        if (!partnerPaymentPage.isCachedValue(orderNo, uuid)) {
            logger.debug("短信支付uuid{}和orderNo{}不匹配", uuid, orderNo)
            throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "短信支付链接错误")
        }

        ClientTypeUtil.cacheChannel(request, Channel.findByApiPartner(partner))
        userCallbackService.cacheUserCallbackInfo(request, uuid, orderNo, order.getApplicant(), order.getSourceChannel())

        logger.debug("第三方合作[订单支付回调链接]，向当前session添加user: {}", order.getApplicant().getId())

        String url = partnerPaymentPage.toClientPage(partner, order, uuid, param)
        String companyId = (String) request.getSession().getAttribute(PAYMENT_URL_COMPANY_KEY)

        UriBuilder builder = UriBuilder.fromUri(url)

        if (!StringUtil.isNull(companyId)) {
            builder.queryParam("companyId", companyId)
        }

        return "redirect:" + builder.build().toString()
    }

    /**
     * 第三方合作[按天买车险停复驶页面回调链接]重定向:
     * from        : /partner/tuhu/callback/daily/T20160120000002
     * redirect to : /m/index.html?daily=true&src=tuhu#daily/T20170112004496
     */
    @RequestMapping(value = "/callback/daily/{orderNo}", method = RequestMethod.GET)
    String dailyInsuranceDetailCallback(@PathVariable("code") String partnerCode,
                                        @PathVariable("orderNo") String orderNo,
                                        @RequestParam(value = "param", required = false) String param,
                                        HttpServletRequest request) {
        ApiPartner partner = ApiPartner.findByCode(partnerCode)

        PurchaseOrder order = preCheckPartnerOrder(partnerCode, orderNo, partner)

        simulateLogin(param, request, partner, order)

        UriBuilder builder = UriBuilder.fromUri(getRootPath(order.getSourceChannel()))
            .queryParam("daily", "true")
            .queryParam("src", partnerCode)
            .fragment("#daily/" + orderNo)

        return "redirect:" + builder.build().toString()
    }

    private void simulateLogin(String param, HttpServletRequest request, ApiPartner partner, PurchaseOrder order) {
        Boolean simulateLogin = validateThirdPartnerLogin(param, order)
        if (!simulateLogin) {
            throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "订单详情链接有误")
        }
        CacheUtil.cacheUser(request.getSession(), order.getApplicant())
        ClientTypeUtil.cacheChannel(request, Channel.findByApiPartner(partner))
        logger.debug("第三方合作[订单详情回调链接]，向当前session添加user: {}", order.getApplicant().getId())
    }

    private PurchaseOrder preCheckPartnerOrder(String partnerCode, String orderNo, ApiPartner partner) {
        if (null == partner) {
            throw new BusinessException(BusinessException.Code.UNAUTHORIZED_ACCESS, partnerCode + " URL无效。")
        }

        PurchaseOrder order = this.orderService.findFirstByOrderNo(orderNo)
        if (null == order) {
            logger.debug("订单号不存在，停止处理: {}", orderNo)
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, orderNo + "订单号不存在。")
        }
        return order
    }

    private Boolean validateThirdPartnerLogin(String param, PurchaseOrder order) {
        if (StringUtils.isBlank(param)) {
            return false
        }

        if (!order.getSourceChannel().isThirdPartnerChannel()) {
            return false
        }

        PartnerOrder partnerOrder = partnerOrderRepository.findFirstByPurchaseOrderId(order.getId())
        if (partnerOrder == null) {
            logger.error("数据库中未找到第三方与车车订单关联信息，订单号为 {}", order.getOrderNo())
            return false
        }
        return true
    }

}
