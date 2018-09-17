package com.cheche365.cheche.zhongan.app.config

import com.cheche365.cheche.core.service.ISignService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.zhongan.service.ZhonganQuerySignService
import com.cheche365.cheche.zhongan.service.ZhonganService
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * 众安spring配置文件
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.tp.app.config',
    'com.cheche365.cheche.zhongan.service',
    'com.cheche365.cheche.decaptcha.app.config',
    'com.cheche365.cheche.core.app.config'
])
@EnableAutoConfiguration
class ZhonganConfig {

    @Bean
    IThirdPartyHandlerService zhonganService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker, IThirdPartyDecaptchaService decaptchaService) {
        new ZhonganService(env, insuranceCompanyChecker, decaptchaService)
    }

    @Bean
    ISignService zhonganQuerySignService(Environment env) {
        new ZhonganQuerySignService(env)
    }

//    @Bean
//    IThirdPartyHandlerService zhonganService(Environment env) {
//        new ZhonganService(env)
//    }


}
