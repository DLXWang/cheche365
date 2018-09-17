package com.cheche365.cheche.ordercenter.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * Created by sunhuazhong on 2015/4/28.
 */
@SpringBootApplication
public class OrderCenterApplicationLauncher extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(OrderCenterApplicationLauncher.class, args);
    }

}
