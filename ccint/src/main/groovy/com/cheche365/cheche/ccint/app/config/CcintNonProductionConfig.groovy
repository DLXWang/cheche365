package com.cheche365.cheche.ccint.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource

/**
 * 合合的Spring配置注释类
 */
@Configuration
@Profile('!production')
@PropertySource('classpath:/properties/ccint.properties')
class CcintNonProductionConfig {

}
