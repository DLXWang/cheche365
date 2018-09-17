package com.cheche365.cheche.pinganuk.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource


/**
 * 平安UK配置文件类
 * **/
@Configuration
@Profile('!production')
@PropertySource('classpath:/properties/pinganuk.properties')
class PinganUKNonProductionConfig extends APinganUKConfig {

}
