package com.cheche365.cheche.fanhua.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by wangfei on 2015/9/1.
 */
@SpringBootApplication
public class FanhuaApplicationLauncher extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(FanhuaApplicationLauncher.class, args);
    }
}
