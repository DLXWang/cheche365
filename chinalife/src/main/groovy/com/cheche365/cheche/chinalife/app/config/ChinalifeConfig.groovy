package com.cheche365.cheche.chinalife.app.config

import com.cheche365.cheche.chinalife.service.ChinalifeService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



@Configuration
class ChinalifeConfig extends AChinalifeConfig {

    @Bean
    IThirdPartyHandlerService chinalifeService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker, IThirdPartyDecaptchaService decaptchaService) {
        new ChinalifeService(env, insuranceCompanyChecker, decaptchaService)
    }
}
