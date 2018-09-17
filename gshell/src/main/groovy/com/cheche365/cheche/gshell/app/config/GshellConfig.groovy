package com.cheche365.cheche.gshell.app.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource



/**
 * 悟空配置文件类
 **/
@Configuration
@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.decaptcha.app.config',
    'com.cheche365.cheche.gshell.service'
])
@PropertySource('classpath:/properties/gshell.properties')
class GshellConfig {

}
