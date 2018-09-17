package com.cheche365.cheche.misc.app.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan([
    'com.cheche365.cheche.core.app.config',
    'com.cheche365.cheche.misc.controller'
])
class MiscConfig {

}
