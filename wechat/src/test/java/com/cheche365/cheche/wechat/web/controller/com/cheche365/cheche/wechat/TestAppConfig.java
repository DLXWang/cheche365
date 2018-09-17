package com.cheche365.cheche.wechat.web.controller.com.cheche365.cheche.wechat;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by liqiang on 3/21/15.
 */

@Configuration

@ComponentScan({
    "com.cheche365.cheche.wechat.web.controller"
})
public class TestAppConfig {
}
