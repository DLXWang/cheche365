package com.cheche365.cheche.picc.client.service

import com.cheche365.cheche.parser.client.service.IThirdPartyHandlerFeignClient
import com.cheche365.cheche.parser.dto.InsuringRequestObject
import com.cheche365.cheche.parser.dto.InsuringResponseObject
import com.cheche365.cheche.parser.dto.QuotingRequestObject
import com.cheche365.cheche.parser.dto.QuotingResponseObject
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE



@FeignClient(value = 'gateway', qualifier = 'piccThirdPartyHandlerFeignClient')
interface IPiccThirdPartyHandlerFeignClient extends IThirdPartyHandlerFeignClient {

    @PostMapping(value = 'picc/insurancePlatform/quotes', consumes = APPLICATION_JSON_VALUE)
    QuotingResponseObject quote(@RequestBody QuotingRequestObject body)

    @PostMapping(value = 'picc/insurancePlatform/insurances', consumes = APPLICATION_JSON_VALUE)
    InsuringResponseObject insure(@RequestBody InsuringRequestObject body)

}
