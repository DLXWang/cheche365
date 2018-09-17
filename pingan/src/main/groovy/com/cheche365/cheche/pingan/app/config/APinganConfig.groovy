package com.cheche365.cheche.pingan.app.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.PropertySource



@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.tp.app.config',
    'com.cheche365.cheche.pingan.service',
    'com.cheche365.cheche.decaptcha.app.config'
])
@PropertySource('classpath:/properties/pingan.properties')
abstract class APinganConfig {
}
