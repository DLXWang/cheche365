package com.cheche365.cheche.picc.app.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration



/**
 * PICC的Spring配置注释类
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.decaptcha.app.config',
    'com.cheche365.cheche.picc.service'
])
abstract class APiccConfig {

}
