package com.cheche365.cheche.core.app.config

import com.cheche365.cheche.core.service.ResourceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource

/**
 * @author Huabin
 */
@Configuration
@Profile('itg')
@PropertySource("classpath:/properties/core-itg.properties")
@EnableConfigurationProperties(ResourceProperties)
class CoreItgConfig {
}
