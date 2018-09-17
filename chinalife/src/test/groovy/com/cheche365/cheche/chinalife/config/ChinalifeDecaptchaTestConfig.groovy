package com.cheche365.cheche.chinalife.config

import com.cheche365.cheche.chinalife.app.config.AChinalifeConfig
import com.cheche365.cheche.chinalife.service.ChinalifeService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * Chinalife的Spring配置注释类
 */
@Configuration
class ChinalifeDecaptchaTestConfig extends AChinalifeConfig {

    @Bean
    chinalifeService(Environment env, IThirdPartyDecaptchaService decaptchaService) {
        new ChinalifeService(env, null, decaptchaService)
    }

}
