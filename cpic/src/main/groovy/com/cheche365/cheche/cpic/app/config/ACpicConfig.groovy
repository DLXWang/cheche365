package com.cheche365.cheche.cpic.app.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource



@Configuration
@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.decaptcha.app.config',
    'com.cheche365.cheche.cpic.service'
])
@PropertySource('classpath:/properties/cpic.properties')
abstract class ACpicConfig {

}
