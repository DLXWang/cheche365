package com.cheche365.cheche.sinosafe.app.config

import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.IThirdPartyUploadingService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.sinosafe.service.SinosafeService
import com.cheche365.cheche.sinosafe.service.SinosafeUploadingService
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

/**
 * 华安spring配置文件
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.core.app.config',
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.sinosafe.service'
])
@EnableAutoConfiguration
class SinosafeConfig {

    @Bean
    IThirdPartyHandlerService sinosafeService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker) {
        new SinosafeService(env, insuranceCompanyChecker)
    }

    @Bean
    IThirdPartyUploadingService sinosafeUploadingService(Environment env) {
        new SinosafeUploadingService(env)
    }

}
