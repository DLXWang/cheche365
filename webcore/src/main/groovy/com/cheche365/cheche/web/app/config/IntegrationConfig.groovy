package com.cheche365.cheche.web.app.config

import com.cheche365.cheche.web.app.listener.IntegrationBuilderListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.config.EnableIntegrationManagement

/**
 * Created by liheng on 2018/4/13 0013.
 */
@Configuration
@EnableIntegration
@EnableIntegrationManagement()
class IntegrationConfig {

    @Bean
    IntegrationBuilderListener applicationStartListener() {
        new IntegrationBuilderListener()
    }
}
