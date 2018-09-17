package com.cheche365.cheche.developer.app.config;

import com.cheche365.cheche.developer.jsonfilter.FilteringJackson2HttpMessageConverter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @Author shanxf
 * @Date 2018/4/11  21:23
 */

@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
@ComponentScan({
    "com.cheche365.cheche.developer",
    "com.cheche365.cheche.web.app.config",
    "com.cheche365.cheche.core.app.config",
    "com.cheche365.cheche.email.app.config",

    // 合作方
    "com.cheche365.cheche.partner.config.app",
    "com.cheche365.cheche.partner.config",
})
@ImportResource({
    "classpath:META-INF/spring/developer-context.xml"
})
public class DeveloperConfig {

    @Configuration
    @AutoConfigureAfter(DeveloperConfig.class)
    public static class WebMvcConfigurerAdapterAutoConfig extends WebMvcConfigurerAdapter {

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
            converters.add(new FilteringJackson2HttpMessageConverter());

            super.configureMessageConverters(converters);
        }

    }

}
