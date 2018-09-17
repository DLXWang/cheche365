package com.cheche365.cheche.piccuk.client.service

import com.cheche365.cheche.parser.client.service.IThirdPartyPaymentFeignClient
import com.cheche365.cheche.parser.dto.RequestObjectForList
import com.cheche365.cheche.parser.dto.RequestObjectForMap
import com.cheche365.cheche.parser.dto.ResponseObjectForList
import com.cheche365.cheche.parser.dto.ResponseObjectForMap
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE



@FeignClient(value = 'gateway', qualifier = 'piccUKThirdPartyPaymentFeignClient')
interface IPiccUKThirdPartyPaymentFeignClient extends IThirdPartyPaymentFeignClient {

    @PostMapping(value = 'piccuk/insurancePlatform/paymentChannels', consumes = APPLICATION_JSON_VALUE)
    ResponseObjectForMap getPaymentChannels(@RequestBody RequestObjectForMap body)

    @PostMapping(value = 'piccuk/insurancePlatform/paymentInfo', consumes = APPLICATION_JSON_VALUE)
    ResponseObjectForMap getPaymentInfo(@RequestBody RequestObjectForMap body)

    @PostMapping(value = 'piccuk/insurancePlatform/paymentState', consumes = APPLICATION_JSON_VALUE)
    ResponseObjectForList checkPaymentState(@RequestBody RequestObjectForList body)

    @PostMapping(value = 'piccuk/insurancePlatform/cancelPayment', consumes = APPLICATION_JSON_VALUE)
    ResponseObjectForMap cancelPayment(@RequestBody RequestObjectForMap body)


}
