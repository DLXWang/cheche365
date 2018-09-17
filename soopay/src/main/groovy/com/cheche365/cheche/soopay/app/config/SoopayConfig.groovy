package com.cheche365.cheche.soopay.app.config;

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.PropertySource

/**
 * Created by mjg on 2017/6/17.
 */
@Configuration
@ComponentScan([
    "com.cheche365.cheche.soopay.payment"
])
@ImportResource([
    "classpath:META-INF/spring/soopay-context.xml"
])
@PropertySource(
    "classpath:/properties/soopay.properties"
)
public class SoopayConfig {
}
