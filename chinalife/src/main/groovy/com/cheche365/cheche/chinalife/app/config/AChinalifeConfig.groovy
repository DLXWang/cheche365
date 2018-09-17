package com.cheche365.cheche.chinalife.app.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource



@Configuration
@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.decaptcha.app.config',
    'com.cheche365.cheche.chinalife.service'])
@PropertySource('classpath:/properties/chinalife.properties')
abstract class AChinalifeConfig {
}
