package com.cheche365.cheche.pinganuk.ms.app.config

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource

/**
 * 平安UK配置文件类
 * **/
@Configuration
@EnableAutoConfiguration
@ComponentScan([
    'com.cheche365.cheche.core.app.config',
    'com.cheche365.cheche.pinganuk.app.config',
    'com.cheche365.cheche.pinganuk.ms.controller'
])
@ImportResource([
    'classpath:META-INF/spring/datasource-context-new-spring.xml',
])
@EnableDiscoveryClient
@EnableCircuitBreaker
class PinganUKMicroServiceConfig {
}
