package com.cheche365.cheche.marketing.app.config;

import com.cheche365.cheche.partner.config.app.PartnerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * @author zhengwei
 */
@Configuration
@ComponentScan({"com.cheche365.cheche.marketing.service",
                "com.cheche365.cheche.marketing.model",
                "com.cheche365.cheche.marketing.web.controller",
                "com.cheche365.cheche.marketing.message.listener",
                "com.cheche365.cheche.alipay.app.config"})
@Import({PartnerConfig.class})
@EnableJpaRepositories("com.cheche365.cheche.marketing.repository")
public class MarketingConfig {

    @Autowired
    JedisConnectionFactory jedisConnectionFactory;


    /** 以下代码本来为了2016年03月支付宝好车主活动同步报价的消息而添加，最终没有使用，留着代码为后续营销活动所使用的消息系统使用
    @Autowired
    TMQuoteListener tmQuoteListener;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    Object moreRedisMessageListener() {
        RedisMessageListenerContainer redisContainer = (RedisMessageListenerContainer)applicationContext.getBean("redisContainer");
        redisContainer.addMessageListener(new MessageListenerAdapter(this.tmQuoteListener),
            new ChannelTopic(TMQuoteMessage.QUEUE_NAME));
        return null;
    } */
}
