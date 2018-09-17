package com.cheche365.cheche.botpy.client.service

import com.cheche365.cheche.parser.client.service.IThirdPartyCheckAccountFeignClient
import com.cheche365.cheche.parser.dto.ResponseObjectForList
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.PostMapping

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@FeignClient(value = 'gateway', qualifier = 'botpyThirdPartyAccountTowerFeignClient')
interface IBotpyThirdPartyAccountTowerFeignClient extends IThirdPartyCheckAccountFeignClient {

    @PostMapping(value = 'botpy/insurancePlatform/getFailedAccounts', consumes = APPLICATION_JSON_VALUE)
    ResponseObjectForList getFailedAccounts()

}
