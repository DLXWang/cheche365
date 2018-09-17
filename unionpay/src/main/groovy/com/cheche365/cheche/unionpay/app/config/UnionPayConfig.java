package com.cheche365.cheche.unionpay.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by wangfei on 2015/7/8.
 */
@Configuration
@ComponentScan({
    "com.cheche365.cheche.unionpay",
    "com.cheche365.cheche.unionpay.app.config"
})
@ImportResource({
    "classpath:META-INF/spring/unionpay-context.xml"
})
public class UnionPayConfig {
}
