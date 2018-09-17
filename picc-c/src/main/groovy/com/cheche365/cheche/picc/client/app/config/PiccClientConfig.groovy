package com.cheche365.cheche.picc.client.app.config

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Configuration



@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
class PiccClientConfig extends APiccClientConfig {
}
