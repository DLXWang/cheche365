package com.cheche365.cheche.rest.web.controller

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.rest.service.pingpp.PingWebhooksAbstract
import com.cheche365.cheche.rest.service.pingpp.PingWebhooksServiceFactory
import com.cheche365.cheche.web.service.PaymentCallbackURLHandler
import com.cheche365.cheche.core.service.WebPurchaseOrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

/**
 * created by shanxf 17.12.27.
 * ping++ webhooks 接口
 */
@RestController
@RequestMapping("/api/callback")
public class PingCallBackResource {

    @Autowired
    private PingWebhooksServiceFactory pingWebhooksServiceFactory

    @Autowired
    private PaymentCallbackURLHandler urlHandler

    @Autowired
    private WebPurchaseOrderService orderService

    @RequestMapping(value = "/pingpp", method = RequestMethod.POST)
    void pingCallBack(HttpServletRequest httpServletRequest, @RequestBody Map event) {

        PingWebhooksAbstract pingWebhooksService = pingWebhooksServiceFactory.findService(event)
        if (pingWebhooksService != null) {
            pingWebhooksService.handle(event)
        }
    }

    @RequestMapping(value = "/pay/{channelId}/{orderNo}", method = RequestMethod.GET)
    ModelAndView redirectFinishPage(@PathVariable Long channelId,
                                    @PathVariable String orderNo,
                                    HttpServletRequest request) {

        Channel channel = Channel.toChannel(channelId)
        PurchaseOrder order = orderService.getFirstPurchaseOrderByNo(orderNo)
        String redirectUrl = urlHandler.toFrontCallBackPage(order, channel, true, request)
        return new ModelAndView("redirect:" + redirectUrl)
    }

}
