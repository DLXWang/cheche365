package com.cheche365.cheche.picc.config

import com.cheche365.cheche.picc.app.config.APiccConfig
import com.cheche365.cheche.picc.service.PiccInsuranceInfoService
import com.cheche365.cheche.picc.service.PiccService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * PICC的Spring配置注释类
 */
@Configuration
@ComponentScan('com.cheche365.cheche.core.app.config')
class PiccMinTestConfig extends APiccConfig {

    @Bean
    piccService(Environment env) {
        new PiccService(env)
    }

    @Bean
    piccInsuranceInfoService() {
        new PiccInsuranceInfoService()
    }

}
