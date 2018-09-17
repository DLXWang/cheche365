package com.cheche365.cheche.ccint.app.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * 合合的Spring配置注释类
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.core.app.config',
    'com.cheche365.cheche.ccint.app.config',
    'com.cheche365.cheche.ccint.service'
])
class CcintConfig {

}
