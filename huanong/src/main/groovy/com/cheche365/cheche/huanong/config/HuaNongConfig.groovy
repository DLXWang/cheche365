package com.cheche365.cheche.huanong.config

import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import com.cheche365.cheche.huanong.service.HuaNongService


/**
 * 华农的Spring配置注释类
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.parserapi.app.config',
    'com.cheche365.springcmp.app.config',
    'com.cheche365.cheche.core.app.config',
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.huanong.service',
    'com.cheche365.cheche.decaptcha.app.config',
])
@EnableAutoConfiguration
class HuaNongConfig {
    @Bean
    IThirdPartyHandlerService huaNongService(Environment env,
                                             IInsuranceCompanyChecker insuranceCompanyChecker, MoApplicationLogRepository logRepo, IThirdPartyDecaptchaService decaptchaService, IConfigService configService) {
        new HuaNongService(env, insuranceCompanyChecker,logRepo,decaptchaService,configService)
    }


}
