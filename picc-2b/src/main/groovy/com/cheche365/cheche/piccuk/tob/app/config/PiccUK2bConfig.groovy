package com.cheche365.cheche.piccuk.tob.app.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

/**
 * 平安UK配置文件类
 * **/
@Configuration
@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.piccuk.tob.service'
])
@PropertySource('classpath:/properties/piccuk2b.properties')
class PiccUK2bConfig {

}
