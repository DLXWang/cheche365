package com.cheche365.cheche.scheduletask.app.config;

import com.cheche365.cheche.core.message.AnswernNotifyEmailMessage;
import com.cheche365.cheche.core.message.BotpyNotifyEmailMessage;
import com.cheche365.cheche.core.message.CrocodileNotifyEmailMessage;
import com.cheche365.cheche.core.message.NotifyEmailMessage;
import com.cheche365.cheche.core.service.FileBasedConfigService;
import com.cheche365.cheche.core.service.IConfigService;
import com.cheche365.cheche.externalpayment.service.PingPlusRefundService;
import com.cheche365.cheche.externalpayment.service.PingPlusRoyaltyService;
import com.cheche365.cheche.externalpayment.service.PingPlusService;
import com.cheche365.cheche.externalpayment.service.ZaCallBackService;
import com.cheche365.cheche.externalpayment.service.ZaOrderQueryServices;
import com.cheche365.cheche.scheduletask.listener.AnswernRedisNotifyEmailListener;
import com.cheche365.cheche.scheduletask.listener.BotpyRedisNotifyEmailListener;
import com.cheche365.cheche.scheduletask.listener.CrocodileRedisNotifyEmailListener;
import com.cheche365.cheche.scheduletask.listener.RedisNotifyEmailListener;
import com.cheche365.cheche.zhongan.service.ZhonganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 引用SpringSecurity产生的web接口登录认证会导致问题
 * 为此单独注入external-payment的bean
 * Created by sunhuazhong on 2015/4/28.
 */

@Configuration
//@EnableJpaAuditing(auditorAwareRef="internalUserAuditorAware", dateTimeProviderRef = "auditingDataTimeProvider")
@ComponentScan({
    "com.cheche365.cheche.core.app.config",
    "com.cheche365.cheche.email.app.config",
    "com.cheche365.cheche.unionpay.app.config",
    "com.cheche365.cheche.alipay.app.config",
    "com.cheche365.cheche.wechat.app.config",
    "com.cheche365.cheche.scheduletask.auditing",
    "com.cheche365.cheche.scheduletask.model",
    "com.cheche365.cheche.scheduletask.service",
    "com.cheche365.cheche.scheduletask.task",
    "com.cheche365.cheche.scheduletask.listener",
    "com.cheche365.cheche.scheduletask.integration",
    "com.cheche365.cheche.baoxian.app.config",
    "com.cheche365.cheche.manage.common.app.config",
    "com.cheche365.cheche.soopay.app.config"
})
@ImportResource({
    "classpath:META-INF/spring/spring-task-common-config.xml",
    "classpath:META-INF/spring/spring-task-context.xml",
    "classpath:META-INF/spring/spring-task-report.xml",
    "classpath:META-INF/spring/spring-task-sms.xml"
})
public class ScheduleTaskConfig {

    @Autowired
    JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    RedisNotifyEmailListener redisNotifyEmailListener;

    @Autowired
    AnswernRedisNotifyEmailListener answernRedisNotifyEmailListener;

    @Autowired
    BotpyRedisNotifyEmailListener botpyRedisNotifyEmailListener;

    @Autowired
    CrocodileRedisNotifyEmailListener crocodileRedisNotifyEmailListener;


    @Bean
    public IConfigService fileBasedConfigService(Environment env) {
        return new FileBasedConfigService(env);
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        container.addMessageListener(new MessageListenerAdapter(this.redisNotifyEmailListener),
            new ChannelTopic(NotifyEmailMessage.QUEUE_NAME));
        container.addMessageListener(new MessageListenerAdapter(this.answernRedisNotifyEmailListener),
            new ChannelTopic(AnswernNotifyEmailMessage.QUEUE_NAME));
        container.addMessageListener(new MessageListenerAdapter(this.botpyRedisNotifyEmailListener),
            new ChannelTopic(BotpyNotifyEmailMessage.QUEUE_NAME));
        container.addMessageListener(new MessageListenerAdapter(this.crocodileRedisNotifyEmailListener),
            new ChannelTopic(CrocodileNotifyEmailMessage.QUEUE_NAME));
        return container;
    }

    @Configuration
    //@EnableWebSecurity(debug = true)
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.httpBasic().disable();
            http.csrf().disable();
            http.anonymous();
        }
    }

    @Bean
    ZhonganService zhonganService(Environment env) {
        return new ZhonganService(env);
    }

    @Bean
    ZaOrderQueryServices zaOrderQueryServices() {
        return new ZaOrderQueryServices();
    }

    @Bean
    ZaCallBackService zaCallBackService() {
        return new ZaCallBackService();
    }

    @Bean
    PingPlusRefundService pingPlusRefundService() {
        return new PingPlusRefundService();
    }

    @Bean
    PingPlusService pingPlusService() {
        return new PingPlusService();
    }

    @Bean
    PingPlusRoyaltyService pingPlusRoyaltyService() {
        return new PingPlusRoyaltyService();
    }
}
