package com.cheche365.cheche.parser.ms.app.config

import com.cheche365.cheche.core.service.FileBasedConfigService
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.cpicuk.service.CpicUKService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * cpicuk微服务的配置
 */
@Configuration
@ComponentScan('com.cheche365.cheche.cpicuk.app.config')
class CpicukMicroServiceConfig {

    @Bean
    IConfigService configService(Environment env) {
        new FileBasedConfigService(env)
    }

    @Bean
    IThirdPartyHandlerService  cpicUKService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker,
                                             IThirdPartyDecaptchaService decaptchaService,
                                             IConfigService configService) {
        new CpicUKService(env, insuranceCompanyChecker, decaptchaService, configService)
    }

}
