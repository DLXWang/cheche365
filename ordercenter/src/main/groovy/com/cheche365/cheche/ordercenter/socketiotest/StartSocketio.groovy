package com.cheche365.cheche.ordercenter.socketiotest

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.stereotype.Component

@Component
class StartSocketio implements ApplicationRunner{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PushSocketioService pushSocketioService;

    @Override
    void run(ApplicationArguments args) throws Exception {
        pushSocketioService.startServer();
    }
}
