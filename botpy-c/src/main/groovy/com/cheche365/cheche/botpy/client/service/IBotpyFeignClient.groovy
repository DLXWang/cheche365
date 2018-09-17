package com.cheche365.cheche.botpy.client.service

import com.cheche365.cheche.parser.client.service.IAutoTypeFeignClient
import com.cheche365.cheche.parser.dto.AutoTypeRequestObject
import com.cheche365.cheche.parser.dto.AutoTypeResponseObject
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@FeignClient(value = 'gateway', qualifier = 'botpyAutoTypeFeignClient')
interface IBotpyFeignClient extends IAutoTypeFeignClient {

    @PostMapping(value = 'botpy/autoTypes', consumes = APPLICATION_JSON_VALUE)
    AutoTypeResponseObject getAutoTypes(@RequestBody AutoTypeRequestObject body)

}
