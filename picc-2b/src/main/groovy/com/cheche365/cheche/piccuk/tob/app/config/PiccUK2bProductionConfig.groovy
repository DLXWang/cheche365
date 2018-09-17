package com.cheche365.cheche.piccuk.tob.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource

/**
 * 人保UK配置文件类
 **/
@Configuration
@Profile('production')
@PropertySource('classpath:/properties/piccuk-production.properties')
class PiccUK2bProductionConfig {

}
