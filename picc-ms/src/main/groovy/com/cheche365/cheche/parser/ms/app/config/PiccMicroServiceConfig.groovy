package com.cheche365.cheche.parser.ms.app.config

import com.cheche365.cheche.core.service.IThirdPartyAutoTypeService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.picc.service.PiccAutoTypeService
import com.cheche365.cheche.picc.service.PiccService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * picc微服务的配置
 */
@Configuration
@ComponentScan('com.cheche365.cheche.picc.app.config')
class PiccMicroServiceConfig {


    @Bean
    IThirdPartyHandlerService piccService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker, IThirdPartyDecaptchaService decaptchaService) {
        new PiccService(env, insuranceCompanyChecker, decaptchaService)
    }

    @Bean
    IThirdPartyAutoTypeService piccAutoTypeService(Environment env) {
        new PiccAutoTypeService(env)
    }


}
