package com.cheche365.cheche.manage.common.app.config
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource


@Configuration
@Profile('!production')
@PropertySource([
    'classpath:/properties/baidusend.properties',
])
class BaiduSenderNonProductionConfig {
}
