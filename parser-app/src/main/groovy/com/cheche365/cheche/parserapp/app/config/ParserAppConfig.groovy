package com.cheche365.cheche.parserapp.app.config

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource


/**
 * parser命令行应用配置文件类
 * **/
@Configuration
@EnableAutoConfiguration
@ComponentScan([
    'com.cheche365.cheche.core.app.config',
    // 保险公司
    'com.cheche365.cheche.cpic.app.config',
    'com.cheche365.cheche.picc.app.config',
    'com.cheche365.cheche.piccuk.app.config',
    'com.cheche365.cheche.pinganuk.app.config',

    'com.cheche365.cheche.parserapp.controller'
])
class ParserAppConfig {
}
