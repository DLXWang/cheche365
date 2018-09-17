package com.cheche365.cheche.pinganuk.client.app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * 平安UK配置文件类
 * **/
@Configuration
@ComponentScan([
    'com.cheche365.cheche.pinganuk.client.service'
])
class PinganUKClientConfig {

    @Bean
    RestTemplate restTemplate() {
        new RestTemplate()
    }

}
