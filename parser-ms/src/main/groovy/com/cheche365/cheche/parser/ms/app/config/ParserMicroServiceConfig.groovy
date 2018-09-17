package com.cheche365.cheche.parser.ms.app.config

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.service.ResourceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableMBeanExport
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.PropertySource

import static org.springframework.jmx.support.RegistrationPolicy.REPLACE_EXISTING



/**
 * 应用的配置
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.core.app.config',
    'com.cheche365.cheche.core.context',
    'com.cheche365.cheche.parser.ms.controller'
])
@EnableZuulProxy
@EnableMBeanExport(
    defaultDomain = 'parserMicroService',
    registration = REPLACE_EXISTING
)
@ImportResource(
    'classpath:spring/parser-context.xml'
)
@PropertySource('classpath:/properties/core.properties')
@EnableConfigurationProperties(ResourceProperties)
class ParserMicroServiceConfig {

    @Bean
    def waitForACH(
        ApplicationContextHolder _appContextHolder // <-- 绝对不许删
    ) {
        new Object()
    }

}
