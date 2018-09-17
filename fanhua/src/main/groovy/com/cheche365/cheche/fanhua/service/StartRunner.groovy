package com.cheche365.cheche.fanhua.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.stereotype.Component

/**
 * 项目启动时执行内容
 * Created by zhangtc on 2017/12/29.
 */
@Component
class StartRunner implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    JedisConnectionFactory jedisConnectionFactory

    @Override
    void run(ApplicationArguments args) throws Exception {

        logger.info "泛华接口项目启动：redis配置->HOST:{}，:PORT{},CLIENT:{},DATEBASE:{}", jedisConnectionFactory.getHostName(),  jedisConnectionFactory.getPort(), jedisConnectionFactory.getClientName(), jedisConnectionFactory.getDatabase()
    }
}
