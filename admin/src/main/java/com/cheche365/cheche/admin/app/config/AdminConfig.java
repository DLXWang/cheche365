package com.cheche365.cheche.admin.app.config;

import com.cheche365.cheche.core.service.FileBasedConfigService;
import com.cheche365.cheche.core.service.IConfigService;
import com.cheche365.cheche.manage.common.jsonfilter.FilteringJackson2HttpMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpSessionEvent;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
@ComponentScan({
    "com.cheche365.cheche.admin",
    "com.cheche365.cheche.core.app.config",
    "com.cheche365.cheche.wechat.app.config",
    "com.cheche365.cheche.manage.common.app.config"
})
@ImportResource({
    "classpath:META-INF/spring/admin-context.xml",
    "classpath:META-INF/spring/admin-security-context.xml"
})
public class AdminConfig {
    private static Logger logger = LoggerFactory.getLogger(AdminConfig.class);
    private static final Integer SESSION_TIME_OUT = 60 * 60;

    @Bean
    public IConfigService fileBasedConfigService(Environment env) {
        return new FileBasedConfigService(env);
    }

    @Configuration
    @AutoConfigureAfter(AdminConfig.class)
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
    @AutoConfigureAfter(AdminConfig.class)
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
}
