package com.cheche365.cheche.picc.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource

/**
 * PICC的Spring配置注释类
 */
@Configuration
@Profile('!production')
@PropertySource('classpath:/properties/picc.properties')
class PiccNonProductionConfig {

}
