package com.cheche365.cheche.web.service

import com.cheche365.cheche.core.model.BusinessActivity
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.CPSChannelService
import com.cheche365.cheche.web.service.system.SystemUrlGenerator
import com.cheche365.cheche.web.util.ClientTypeUtil
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.UriBuilder

import static com.cheche365.cheche.core.constants.WebConstants.SESSION_KEY_USER_CALLBACK
import static com.cheche365.cheche.core.constants.WebConstants.getFrontCallbackPath
import static com.cheche365.cheche.core.constants.WebConstants.getIndexPath
import static com.cheche365.cheche.core.constants.WebConstants.getPath
import static com.cheche365.cheche.core.constants.WebConstants.getServerCallbackPath
import static com.cheche365.cheche.core.model.Channel.Enum.IOS_4
import static com.cheche365.cheche.core.model.Channel.Enum.IOS_CHEBAOYI_221
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.PING_PLUS_WX_23
import static com.cheche365.cheche.web.service.system.SystemURL.PAYMENT_URL_COMPANY_KEY
import static com.cheche365.cheche.web.service.system.SystemURL.PAYMENT_URL_READONLY_KEY
import static com.cheche365.cheche.web.service.system.SystemURL.PAYMENT_URL_SRC_VALUE
import static java.net.URLEncoder.encode

/**
 * Created by zhengwei on 1/24/16.
 * 处理不同支付方式对支付回调URL的需求，包括银联，支付宝，微信
 */

@Service
class PaymentCallbackURLHandler {

    private Logger logger = LoggerFactory.getLogger(PaymentCallbackURLHandler.class)

    static final String QUERY_KEY_CPS_CHANNEL = "channel"
    public static final String PAYMENT_URL_SRC_KEY = "src"

    @Autowired
    private CPSChannelService cpsChannelService
    @Autowired
    private SystemUrlGenerator systemUrlGenerator

    String toCallbackPage(PurchaseOrder order, HttpServletRequest request, boolean success) {
        String fragment = (success ? "success" : "fail") + "/" + order.getOrderNo()
        Channel channel = ClientTypeUtil.getChannel(request)
        return callbackUrlBuilder(order, channel, request, getIndexPath(channel), fragment).build().toString()
    }

    private UriBuilder callbackUrlBuilder(PurchaseOrder order, Channel channel, HttpServletRequest request, String basePath, String fragment) {
        UriBuilder builder = UriBuilder.fromUri(basePath)

        logger.debug("开始生成支付成功页面url，原始url: {}", builder.build().toString())

        //cps处理
        BusinessActivity cpsBusinessActivity = cpsChannelService.getCpsActivityByOrderNo(order.getOrderNo())
        if (null != cpsBusinessActivity) {
            builder.queryParam(QUERY_KEY_CPS_CHANNEL, StringUtils.trimToEmpty(cpsBusinessActivity.getCode()))
            logger.debug("处理cps后的支付成功url", builder.build().toString())
        }

        //支付短连接处理
        if (null != request.getSession() && null != request.getSession().getAttribute(SESSION_KEY_USER_CALLBACK)) {
            builder.queryParam(PAYMENT_URL_SRC_KEY, PAYMENT_URL_SRC_VALUE)
            request.getSession().setAttribute(SESSION_KEY_USER_CALLBACK, null)
            logger.debug("清空session中短信支付标记，支付成功跳转链接增加参数串, url: {}", builder.build().toString())
        } else {
            logger.debug("session中不存在短信支付标记，非短信链接支付。session值: {}", (null == request.getSession()) ? null : request.getSession().getId())
        }

        if (channel.isThirdPartnerChannel()) {
            builder.queryParam(PAYMENT_URL_SRC_KEY, channel.getApiPartner().getCode().toLowerCase())

            buildQueryParam(builder, request)
            logger.debug("处理{}支付成功, url: {}", channel.getApiPartner().getCode(), builder.build().toString())
        }

        Boolean readonly = request.getHeader("Referer") != null && request.getHeader("Referer").contains("readonly")
        if (readonly) {
            builder.queryParam(PAYMENT_URL_READONLY_KEY, readonly)
        }
        return builder.fragment(fragment)
    }

    static String toServerCallbackUrl(PurchaseOrder order, HttpServletRequest request) {
        UriBuilder builder = UriBuilder.fromUri(getServerCallbackPath())
            .path(String.valueOf(ClientTypeUtil.getChannel(request).getId()))
            .path(order.getOrderNo())
        Boolean readonly = request.getHeader("Referer") != null && request.getHeader("Referer").contains("readonly")
        if (readonly) {
            builder.queryParam(PAYMENT_URL_READONLY_KEY, readonly)
        }
        return builder.build().toString()
    }

    String toFrontCallBackPage(PurchaseOrder order, Channel channel, Boolean redirect, HttpServletRequest request) {

        UriBuilder builder = callbackUrlBuilder(order, channel, request, getFrontCallbackPath(), null)
        builder.queryParam("path", getPath(channel))
        builder.queryParam("type", "order")
        builder.queryParam("orderNo", order.getOrderNo())
        if (request.getQueryString() != null && request.getQueryString().contains(PAYMENT_URL_READONLY_KEY)) {
            builder.queryParam(PAYMENT_URL_READONLY_KEY, Boolean.TRUE)
        }

        if (redirect && PING_PLUS_WX_23 == order.getChannel()) {
            if (IOS_4 == channel) {
                builder.queryParam("redirectUrl", encode("wxPingForCheChe://orders?orderNo=", "UTF-8") + order.getOrderNo())
            }

            if (IOS_CHEBAOYI_221 == channel) {
                builder.queryParam("redirectUrl", encode("cheBaoYiScheme://orders?orderNo=", "UTF-8") + order.getOrderNo())
            }
        }

        return builder.build().toString()
    }

    String toFrontCallBackPage(PurchaseOrder order, HttpServletRequest request) {
        return this.toFrontCallBackPage(order, ClientTypeUtil.getChannel(request), false, request)
    }

    private void buildQueryParam(UriBuilder builder, HttpServletRequest request) {

        try {
            Object companyId = request.getSession().getAttribute(PAYMENT_URL_COMPANY_KEY)
            if (companyId != null) {
                logger.debug("从session中获取到的companyId为:{}，将会被拼接在路径中", companyId.toString())
                builder.queryParam(PAYMENT_URL_COMPANY_KEY, companyId.toString())
            }
        } catch (Exception e) {
            logger.error("支付回调query string 拼接出错, " + ExceptionUtils.getStackTrace(e))
        }

    }
}
