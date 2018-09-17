package com.cheche365.cheche.parser.ms.app.config

import com.cheche365.cheche.core.service.ResourceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource

/**
 * cpic2b-ms的配置
 */
@Configuration
@Profile('itg')
@PropertySource('classpath:/properties/core-itg.properties')
@EnableConfigurationProperties(ResourceProperties)
class ParserMicroServiceItgConfig {

}
