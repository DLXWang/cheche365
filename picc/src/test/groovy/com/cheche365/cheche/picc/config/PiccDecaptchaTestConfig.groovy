package com.cheche365.cheche.picc.config

import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.picc.app.config.APiccConfig
import com.cheche365.cheche.picc.service.PiccInsuranceInfoService
import com.cheche365.cheche.picc.service.PiccService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * PICC的Spring配置注释类
 */
@Configuration
class PiccDecaptchaTestConfig extends APiccConfig {

    @Bean
    piccService(Environment env, IThirdPartyDecaptchaService decaptchaService) {
        new PiccService(env, null, decaptchaService)
    }

    @Bean
    piccInsuranceInfoService(IThirdPartyDecaptchaService decaptchaService) {
        new PiccInsuranceInfoService(decaptchaService)
    }

}
