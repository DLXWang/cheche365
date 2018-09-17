package com.cheche365.cheche.partner.config.app;

import com.cheche365.cheche.web.filter.BodyReadFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Arrays;

/**
 * Created by chenxiaozhe on 15-12-10.
 */
@Configuration
@ComponentScan({
    "com.cheche365.cheche.partner.config",
    "com.cheche365.cheche.partner.api",
    "com.cheche365.cheche.partner.web",
    "com.cheche365.cheche.partner.service",
    "com.cheche365.cheche.partner.handler",
    "com.cheche365.cheche.partner.serializer"
})
@ImportResource({
    "classpath:META-INF/spring/partner-security-context.xml"
})
@EnableJpaAuditing
public class PartnerConfig {

    @Bean
    public FilterRegistrationBean bodyReadFilterBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/partner/*","/internal/developer/third/*"));
        filterRegistrationBean.setFilter(new BodyReadFilter());
        return filterRegistrationBean;
    }

}
