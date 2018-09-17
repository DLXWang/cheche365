package com.cheche365.cheche.externalapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource

/**
 * Created by zhengwei on 11/02/2018.
 */

@Configuration

@Profile('!production')
@PropertySource('classpath:/properties/externalapi.properties')
class ExternalAPINonProductionConfig {
}
