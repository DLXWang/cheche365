package com.cheche365.cheche.botpy.client.app.config

import com.cheche365.cheche.core.service.ResourceProperties
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource


/**
 * 客户端的配置
 */
@Configuration
@PropertySource('classpath:/properties/core.properties')
@EnableConfigurationProperties(ResourceProperties)
@EnableAutoConfiguration
@EnableDiscoveryClient
class BotpyClientTestConfig extends ABotpyClientConfig {
}
