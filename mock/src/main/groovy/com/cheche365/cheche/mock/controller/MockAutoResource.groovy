package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.counter.annotation.NonProduction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1.6/mock/auto")
class MockAutoResource extends ContextResource {

    @Autowired
    private MoApplicationLogRepository moApplicationLogRepository

    @NonProduction
    @RequestMapping(value = "/bihu/info", method = RequestMethod.GET)
    public HttpEntity getBihuInsuranceInfo(@RequestParam(value = "licenseNo")String licenseNo) {
        List autoInfos = moApplicationLogRepository.findBihuInfoByLicenseNo(licenseNo);
        return getResponseEntity(autoInfos);
    }

}
