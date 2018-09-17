package com.cheche365.cheche.zhongan.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource



/**
 * 众安spring配置文件
 */
@Configuration
@Profile('production')
@PropertySource('classpath:/properties/zhongan-production.properties')
class ZhonganProductionConfig {
}

