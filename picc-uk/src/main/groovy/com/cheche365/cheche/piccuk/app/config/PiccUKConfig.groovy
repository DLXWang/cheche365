package com.cheche365.cheche.piccuk.app.config

import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.core.service.ResourceProperties
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.piccuk.service.PiccUKPaymentInfoService
import com.cheche365.cheche.piccuk.service.PiccUKService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * 人保UK配置文件类
 **/
@Configuration
@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.piccuk.service',
    'com.cheche365.cheche.core.service',
    'com.cheche365.cheche.decaptcha.app.config'
])
@EnableConfigurationProperties(ResourceProperties)
class PiccUKConfig {

    @Bean
    IThirdPartyHandlerService piccUKService(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService,
        IConfigService configService
    ) {
        new PiccUKService(env, insuranceCompanyChecker, decaptchaService, configService)
    }

    @Bean
    IThirdPartyPaymentService piccUKPaymentInfoService(IConfigService configService) {
        new PiccUKPaymentInfoService(configService)
    }
}
