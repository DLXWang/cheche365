package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.repository.ApiPartnerRepository
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.web.counter.annotation.NonProduction
import com.cheche365.cheche.web.service.system.SystemUrlGenerator
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by taichangwei on 2017/6/20.
 */
@RestController
@RequestMapping("/v1.6/mock/urls")
@Slf4j
class MockSystemUrlResource {

    @Autowired
    SystemUrlGenerator systemUrlGenerator

    @Autowired
    ChannelRepository channelRepository

    @NonProduction
    @RequestMapping(value = "qr")
    getQrUrl(@RequestParam(value = "qrId") Long qrId, @RequestParam(value = "channelId") Long channelId) {
        Channel channel = channelRepository.findById(channelId)
        String url = systemUrlGenerator.toQrUrl(qrId, channel)
        [url: url]
    }

    @NonProduction
    @RequestMapping(value = "image")
    getImageUrl(@RequestParam(value = "orderNo") String orderNo) {
        [url: systemUrlGenerator.toImageUrl(orderNo)]
    }

    @NonProduction
    @RequestMapping(value = "renewal")
    getRenewalUrl(@RequestParam(value = "orderNo") String orderNo) {
        [url: systemUrlGenerator.renewalOrder(orderNo)]
    }

    @NonProduction
    @RequestMapping(value = "payment")
    paymentUrl(@RequestParam(value = "orderNo") String orderNo) {
        [url: systemUrlGenerator.toPaymentUrlOriginal(orderNo)]
    }

    @NonProduction
    @RequestMapping(value = "suspendBill")
    suspendBill(@RequestParam(value = "orderNo") String orderNo) {
        [url: systemUrlGenerator.toSuspendBillUrlOriginal(orderNo)]
    }

    @NonProduction
    @RequestMapping(value = "orderDetail")
    orderDetail(@RequestParam(value = "orderNo") String orderNo) {
        [url: systemUrlGenerator.toOrderDetailUrl(orderNo)]
    }

    @NonProduction
    @RequestMapping(value = "shortUrl", method = RequestMethod.GET)
    getShortUrl(@RequestParam(value = 'longUrl')String longUrl){
        [shortUrl: systemUrlGenerator.toShortUrl(longUrl)]
    }

}

