package com.cheche365.cheche.developer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


/**
 * @Author shanxf
 * @Date 2018/4/11  21:23
 */
@SpringBootApplication
public class DeveloperApplicationLauncher  {

    public static void main(String[] args) {
        SpringApplication.run(DeveloperApplicationLauncher.class, args);
    }


    @Bean
    public InternalResourceViewResolver internalResourceViewResolver(){
        InternalResourceViewResolver internalResourceViewResolver= new InternalResourceViewResolver();
        internalResourceViewResolver.setRedirectHttp10Compatible(false);
        return internalResourceViewResolver;
    }

}
