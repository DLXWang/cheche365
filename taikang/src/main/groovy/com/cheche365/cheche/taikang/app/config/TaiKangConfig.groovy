package com.cheche365.cheche.taikang.app.config

import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.taikang.service.TaiKangService
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * 泰康在线的配置
 *
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.parserapi.app.config',
    'com.cheche365.springcmp.app.config',
    'com.cheche365.cheche.core.app.config',
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.decaptcha.app.config',
    'com.cheche365.cheche.taikang.service',
])
@EnableAutoConfiguration
class TaiKangConfig {

    @Bean
    IThirdPartyHandlerService taiKangService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker, MoApplicationLogRepository logRepo,
                                             IThirdPartyDecaptchaService decaptchaService, IConfigService configService) {
        new TaiKangService(env, insuranceCompanyChecker, logRepo, decaptchaService, configService)
    }
}
