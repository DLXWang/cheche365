package com.cheche365.cheche.pinganuk.app.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration



/**
 * PINGANUK的Spring配置注释类
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.pinganuk.service',
    'com.cheche365.cheche.decaptcha.app.config',
    'com.cheche365.cheche.gshell.app.config'
])
abstract class APinganUKConfig {

}
