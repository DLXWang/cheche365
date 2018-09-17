package com.cheche365.cheche.parserapp.app

import org.springframework.boot.autoconfigure.SpringBootApplication

import static org.springframework.boot.SpringApplication.run

/**
 * parser命令行应用
 * Created by Huabin on 2016/11/07.
 */
@SpringBootApplication
class ParserApplication {

    public static void main(String[] args) {
        run ParserApplication.class, args
    }

}
