package com.cheche365.cheche.baoxian.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource


@Configuration
@Profile('!production')
@PropertySource([
    'classpath:/properties/baoxian.properties',
    'classpath:/properties/baoxian2.properties'
])
class BaoXianNonProductionConfig {
}
