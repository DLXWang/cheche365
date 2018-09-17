package com.cheche365.cheche.piccuk.client.service

import com.cheche365.cheche.parser.client.service.IThirdPartyCheckAccountFeignClient
import com.cheche365.cheche.parser.dto.ResponseObjectForList
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.PostMapping

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@FeignClient(value = 'gateway', qualifier = 'piccUKThirdPartyCheckAccountFeignClient')
interface IPiccUKThirdPartyCheckAccountFeignClient extends IThirdPartyCheckAccountFeignClient {

    @PostMapping(value = 'piccuk/insurancePlatform/getFailedAccounts', consumes = APPLICATION_JSON_VALUE)
    ResponseObjectForList getFailedAccounts()

}
