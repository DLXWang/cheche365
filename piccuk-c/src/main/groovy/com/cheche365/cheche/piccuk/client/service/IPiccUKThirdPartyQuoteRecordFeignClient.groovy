package com.cheche365.cheche.piccuk.client.service

import com.cheche365.cheche.parser.client.service.IThirdPartyQuoteRecordFeignClient
import com.cheche365.cheche.parser.dto.RequestObjectForList
import com.cheche365.cheche.parser.dto.ResponseObjectForMap
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE



@FeignClient(value = 'gateway', qualifier = 'piccUKThirdPartyQuoteRecordFeignClient')
interface IPiccUKThirdPartyQuoteRecordFeignClient extends IThirdPartyQuoteRecordFeignClient {

    @PostMapping(value = 'piccuk/insurancePlatform/quoteRecordState', consumes = APPLICATION_JSON_VALUE)
    ResponseObjectForMap getQuoteRecordState(@RequestBody RequestObjectForList body)

}
