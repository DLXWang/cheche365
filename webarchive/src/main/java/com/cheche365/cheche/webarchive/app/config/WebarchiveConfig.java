package com.cheche365.cheche.webarchive.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dongruiren on 2015/11/30.
 */
@Configuration
@ComponentScan({
    "com.cheche365.cheche.rest.reconfigurable.web.controller",
    "com.cheche365.cheche.rest.v1_2.web.controller"})
public class WebarchiveConfig {
}
