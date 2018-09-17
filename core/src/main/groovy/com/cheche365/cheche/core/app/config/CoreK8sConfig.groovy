package com.cheche365.cheche.core.app.config

import com.cheche365.cheche.core.service.ResourceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource

/**
 * Created by liqiang on 6/15/15.
 */
@Configuration
@Profile('k8s')
@PropertySource("classpath:/properties/core-k8s.properties")
@EnableConfigurationProperties(ResourceProperties)
public class CoreK8sConfig {
}
