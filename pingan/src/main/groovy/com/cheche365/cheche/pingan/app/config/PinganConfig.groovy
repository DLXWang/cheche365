package com.cheche365.cheche.pingan.app.config

import com.cheche365.cheche.core.service.IInsuranceInfoService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.pingan.service.PinganInsuranceInfoService
import com.cheche365.cheche.pingan.service.PinganService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



@Configuration
class PinganConfig extends APinganConfig {

    @Bean
    IThirdPartyHandlerService pinganService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker, IThirdPartyDecaptchaService decaptchaService) {
        new PinganService(env, insuranceCompanyChecker, decaptchaService)
    }

    @Bean
    IInsuranceInfoService pinganInsuranceInfoService(IThirdPartyDecaptchaService decaptchaService) {
        new PinganInsuranceInfoService(decaptchaService)
    }

}
