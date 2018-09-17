package com.cheche365.cheche.test.core

import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.message.PartnerOrderMessage
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.test.core.config.CoreTestConfig
import com.cheche365.cheche.test.core.config.RedisMessageTestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Created by zhengwei on 2/13/17.
 */
@EnableAutoConfiguration
@ContextConfiguration( classes = [CoreConfig, CoreTestConfig] )
class RedisBasicFT extends Specification {

    @Autowired
    RedisPublisher publisher

    @Autowired
    StringRedisTemplate template

    def "增补pub sub message"(){

        given:

        def message = new PartnerOrderMessage()
        message.message = '{}'

        def subscriber = Mock(MessageListener)
        ApplicationContextHolder.getApplicationContext().getBean(RedisMessageListenerContainer).addMessageListener(new MessageListenerAdapter(subscriber), new ChannelTopic(PartnerOrderMessage.QUEUE_NAME))

        when:
        publisher.publish(message)
        sleep(3 * 1000)
        def result = template.delete('non_exist_key')
        println result

        then:
        1 * subscriber.onMessage(_, _)
    }

    def "redis api 测试"(){

        when:
        template.opsForSet().isMember('foo', 'bar')

        then:
        true
    }

}
