package com.cheche365.cheche.pingan.app.config

import com.cheche365.cheche.pingan.service.PinganInsuranceInfoService
import com.cheche365.cheche.pingan.service.PinganService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * PICC的Spring配置注释类
 */
@Configuration
class PinganMinTestConfig extends APinganConfig {

    @Bean
    pinganService(Environment env) {
        new PinganService(env)
    }

    @Bean
    pinganInsuranceInfoService() {
        new PinganInsuranceInfoService()
    }

}
