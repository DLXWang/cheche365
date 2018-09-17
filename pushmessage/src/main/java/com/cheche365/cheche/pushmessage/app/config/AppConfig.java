package com.cheche365.cheche.pushmessage.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liqiang on 7/20/15.
 */
@Configuration
@ComponentScan({
    "com.cheche365.cheche.core.app.config",
    "com.cheche365.cheche.microservice.app.config",
    "com.cheche365.cheche.pushmessage",
    "com.cheche365.cheche.pushmessage.impl"
})
public class AppConfig {
}
