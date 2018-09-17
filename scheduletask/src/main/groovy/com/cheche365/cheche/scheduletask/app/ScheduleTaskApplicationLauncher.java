package com.cheche365.cheche.scheduletask.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


/**
 * Created by sunhuazhong on 2015/4/28.
 */
@SpringBootApplication
public class ScheduleTaskApplicationLauncher {

    public static void main(String[] args) {
//        SpringApplication.run(ScheduleTaskApplicationLauncher.class, args);
        new SpringApplicationBuilder(ScheduleTaskApplicationLauncher.class).web(true).run(args);
    }

}
