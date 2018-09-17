package com.cheche365.cheche.cpic.app.config

import com.cheche365.cheche.core.service.IInsuranceInfoService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.cpic.service.CpicInsuranceInfoService
import com.cheche365.cheche.cpic.service.CpicService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



@Configuration
class CpicConfig extends ACpicConfig {

    @Bean
    IThirdPartyHandlerService cpicService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker, IThirdPartyDecaptchaService decaptchaService) {
        new CpicService(env, insuranceCompanyChecker, decaptchaService)
    }

    @Bean
    IInsuranceInfoService cpicInsuranceInfoService(IThirdPartyDecaptchaService decaptchaService) {
        new CpicInsuranceInfoService(decaptchaService)
    }
}
