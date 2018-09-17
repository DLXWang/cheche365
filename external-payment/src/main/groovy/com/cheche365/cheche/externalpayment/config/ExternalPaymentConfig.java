package com.cheche365.cheche.externalpayment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;


@Configuration
@ComponentScan({
    "com.cheche365.cheche.externalpayment.template",
    "com.cheche365.cheche.externalpayment.controller",
    "com.cheche365.cheche.externalpayment.service",
    "com.cheche365.cheche.externalpayment.handler",
    "com.cheche365.cheche.baoxian.app.config",
    "com.cheche365.cheche.wallet.core.app.config"
    })
public class ExternalPaymentConfig{

    @Bean
    public ExecutorService pollingExecutorService() {
        return newFixedThreadPool(3);
    }

}
