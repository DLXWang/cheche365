package com.cheche365.cheche.sinosig.app.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource


@Configuration
@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.sinosig.service'
])
@PropertySource('classpath:/properties/sinosig.properties')
public class SinosigConfig {

}
