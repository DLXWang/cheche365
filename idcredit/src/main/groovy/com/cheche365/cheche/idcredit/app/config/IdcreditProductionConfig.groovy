package com.cheche365.cheche.idcredit.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource

/**
 * 绿湾的Spring配置注释类
 */
@Configuration
@Profile('production')
@PropertySource('classpath:/properties/idcredit-production.properties')
class IdcreditProductionConfig {

}
