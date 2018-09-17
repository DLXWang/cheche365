package com.cheche365.cheche.misc.app

import groovy.util.logging.Slf4j
import org.springframework.boot.autoconfigure.SpringBootApplication

import static org.springframework.boot.SpringApplication.run

@SpringBootApplication
@Slf4j
class ApplicationLauncher {

    public static void main(String[] args) {
        run ApplicationLauncher, args
    }

}
