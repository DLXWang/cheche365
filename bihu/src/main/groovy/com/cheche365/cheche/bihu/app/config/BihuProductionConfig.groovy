package com.cheche365.cheche.bihu.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource



/**
 * 壁虎的Spring Production配置注释类
 */
@Configuration
@Profile('production')
@PropertySource('classpath:/properties/bihu-production.properties')
class BihuProductionConfig {
}
