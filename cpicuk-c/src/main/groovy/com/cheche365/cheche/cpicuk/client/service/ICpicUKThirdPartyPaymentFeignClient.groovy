package com.cheche365.cheche.cpicuk.client.service

import com.cheche365.cheche.parser.client.service.IThirdPartyPaymentFeignClient
import com.cheche365.cheche.parser.dto.RequestObjectForList
import com.cheche365.cheche.parser.dto.RequestObjectForMap
import com.cheche365.cheche.parser.dto.ResponseObjectForList
import com.cheche365.cheche.parser.dto.ResponseObjectForMap
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE



@FeignClient(value = 'gateway', qualifier = 'cpicUKThirdPartyPaymentFeignClient')
interface ICpicUKThirdPartyPaymentFeignClient extends IThirdPartyPaymentFeignClient {

    @PostMapping(value = 'cpicuk/insurancePlatform/paymentChannels', consumes = APPLICATION_JSON_VALUE)
    ResponseObjectForMap getPaymentChannels(@RequestBody RequestObjectForMap body)

    @PostMapping(value = 'cpicuk/insurancePlatform/paymentInfo', consumes = APPLICATION_JSON_VALUE)
    ResponseObjectForMap getPaymentInfo(@RequestBody RequestObjectForMap body)

    @PostMapping(value = 'cpicuk/insurancePlatform/paymentState', consumes = APPLICATION_JSON_VALUE)
    ResponseObjectForList checkPaymentState(@RequestBody RequestObjectForList body)

    @PostMapping(value = 'cpicuk/insurancePlatform/cancelPayment', consumes = APPLICATION_JSON_VALUE)
    ResponseObjectForMap cancelPayment(@RequestBody RequestObjectForMap body)


}
