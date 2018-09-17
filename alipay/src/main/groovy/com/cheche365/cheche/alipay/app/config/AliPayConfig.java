package com.cheche365.cheche.alipay.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by chenxiaozhe on 15-8-17.
 */
@Configuration
@ComponentScan({
    "com.cheche365.cheche.alipay",
    "com.cheche365.cheche.alipay.util",
    "com.cheche365.cheche.alipay.web",
    "com.cheche365.cheche.alipay.alipass"
})
public class AliPayConfig {


}
