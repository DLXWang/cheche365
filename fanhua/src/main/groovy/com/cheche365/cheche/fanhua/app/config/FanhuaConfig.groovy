package com.cheche365.cheche.fanhua.app.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * Created by zhangtc on 2017/11/30.
 */
@Configuration
//@EnableWebMvc
@ComponentScan([
    'com.cheche365.cheche.fanhua',
    'com.cheche365.cheche.manage.common.app.config',
    'com.cheche365.cheche.core.app.config'
])
@EnableJpaRepositories('com.cheche365.cheche.fanhua.repository')
@EnableScheduling
@PropertySource('classpath:/properties/fanhua.properties')
class FanhuaConfig {
}
