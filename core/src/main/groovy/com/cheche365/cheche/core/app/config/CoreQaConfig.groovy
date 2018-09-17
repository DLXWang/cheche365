package com.cheche365.cheche.core.app.config;

import com.cheche365.cheche.core.service.ResourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by liqiang on 6/15/15.
 */
@Configuration
@Profile('qa')
@PropertySource("classpath:/properties/core-qa.properties")
@EnableConfigurationProperties(ResourceProperties)
public class CoreQaConfig {
}
