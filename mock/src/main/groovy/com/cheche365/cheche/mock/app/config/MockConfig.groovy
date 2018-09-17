package com.cheche365.cheche.mock.app.config

import com.cheche365.cheche.mock.service.DecaptchaMessageListener
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter

/**
 * @author zhengwei
 */
@Configuration
@ComponentScan(["com.cheche365.cheche.marketing.app.config",
                "com.cheche365.cheche.mock.service",
                "com.cheche365.cheche.mock.controller"
                ])
class MockConfig {


    MockConfig(RedisMessageListenerContainer redisContainer, DecaptchaMessageListener listener){
        redisContainer.addMessageListener(new MessageListenerAdapter(listener),
            new ChannelTopic('decaptcha-in-type07'));

        redisContainer.addMessageListener(new MessageListenerAdapter(listener),
            new ChannelTopic('decaptcha-in-type02'));
    }

}
