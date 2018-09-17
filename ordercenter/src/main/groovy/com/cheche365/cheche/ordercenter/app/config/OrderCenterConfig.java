package com.cheche365.cheche.ordercenter.app.config;

import com.cheche365.cheche.answern.service.AnswernService;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.manage.common.jsonfilter.FilteringJackson2HttpMessageConverter;
import com.cheche365.cheche.ordercenter.session.ClearSessionStrategy;
import com.cheche365.cheche.ordercenter.session.OrderCenterRedisHttpSessionConfiguration;
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker;
import com.cheche365.cheche.zhongan.service.ZhonganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.http.HttpSessionEvent;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
@Configuration
//@EnableJpaAuditing(auditorAwareRef="internalUserAuditorAware", dateTimeProviderRef = "auditingDataTimeProvider")
@EnableWebMvc
@EnableSpringDataWebSupport
@Import(OrderCenterRedisHttpSessionConfiguration.class)
@ComponentScan({
        "com.cheche365.cheche.ordercenter",
        "com.cheche365.cheche.core.app.config",
        "com.cheche365.cheche.sms.client.app.config",
        "com.cheche365.cheche.email.app.config",
        "com.cheche365.cheche.partner.config.app",
        "com.cheche365.cheche.unionpay.app.config",
        "com.cheche365.cheche.alipay.app.config",
        "com.cheche365.cheche.wechat.app.config",
        "com.cheche365.cheche.manage.common.app.config",
        "com.cheche365.cheche.soopay.app.config",
        "com.cheche365.cheche.baoxian.app.config",
        "com.cheche365.cheche.externalpayment.config",
        "com.cheche365.cheche.web.app.config",
        "com.cheche365.cheche.zhongan.app.config"
})
@ImportResource({
        "classpath:META-INF/spring/ordercenter-context.xml",
        "classpath:META-INF/spring/ordercenter-security-context.xml"
})
public class OrderCenterConfig {

    private static final Integer SESSION_TIME_OUT = 60 * 60;

    @Autowired
    JedisConnectionFactory jedisConnectionFactory;

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    ZhonganService zhonganService(Environment env) {
        return new ZhonganService(env);
    }

    @Bean
    public IConfigService fileBasedConfigService(Environment env) {
        return new FileBasedConfigService(env);
    }

    @Bean
    AnswernService answernService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker, IThirdPartyDecaptchaService decaptchaService, IConfigService configService, IResourceService resourceService) {
        return new AnswernService(env, insuranceCompanyChecker, decaptchaService, configService, resourceService);
    }


    @Configuration
    @AutoConfigureAfter(OrderCenterConfig.class)
    public static class WebMvcConfigurerAdapterAutoConfig extends WebMvcConfigurerAdapter {

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
            converters.add(new FilteringJackson2HttpMessageConverter());

            super.configureMessageConverters(converters);
        }
    }


    @Configuration
    @EnableWebSecurity(debug = true)
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.httpBasic().disable();
            http.csrf().disable();
            http.anonymous();
        }
    }

    @Configuration
    @AutoConfigureAfter(OrderCenterConfig.class)
    @EnableWebSecurity(debug = true)
    public static class SessionTimeOutConfig extends HttpSessionEventPublisher {

        @Override
        public void sessionCreated(HttpSessionEvent se) {
            se.getSession().setMaxInactiveInterval(SESSION_TIME_OUT);
            super.sessionCreated(se);
        }
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        return container;
    }


    @Configuration
    @ConditionalOnClass({Servlet.class, StandardServletMultipartResolver.class,
            MultipartConfigElement.class})
    @ConditionalOnProperty(prefix = "spring.http.multipart", name = "enabled", matchIfMissing = true)
    @EnableConfigurationProperties(MultipartProperties.class)
    public static class MultipartAutoConfiguration {

        MultipartProperties multipartProperties;

        public MultipartAutoConfiguration(MultipartProperties multipartProperties) {
            this.multipartProperties = multipartProperties;
        }

        @Bean
        @ConditionalOnMissingBean
        public MultipartConfigElement multipartConfigElement() {
            this.multipartProperties.setMaxFileSize("10MB");
            this.multipartProperties.setMaxRequestSize("10MB");
            return this.multipartProperties.createMultipartConfig();
        }

        @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
        @ConditionalOnMissingBean(MultipartResolver.class)
        public StandardServletMultipartResolver multipartResolver() {
            StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
            multipartResolver.setResolveLazily(false);
            return multipartResolver;
        }
    }


    @Bean
    ClearSessionStrategy sessionStrategy() {
        return new ClearSessionStrategy();
    }
}
