package com.cheche365.cheche.wechat.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.Resource;

/**
 * Created by liqiang on 3/24/15.
 */

@Configuration
@ComponentScan({
    "com.cheche365.cheche.wechat",
    "com.cheche365.cheche.wechat.web.controller",
    "com.cheche365.cheche.wechat.web.test",
    "com.cheche365.cheche.marketing.app.config"
})
@ImportResource({
    "classpath:META-INF/spring/wechat-security-context.xml"
})
public class WechatConfig {
}
