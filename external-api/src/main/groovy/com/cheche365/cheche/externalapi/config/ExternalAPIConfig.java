package com.cheche365.cheche.externalapi.config;

import com.cheche365.cheche.core.service.FileBasedConfigService;
import com.cheche365.cheche.core.service.IConfigService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by zhengwei on 06/02/2018.
 */

@Configuration
@ComponentScan({
    "com.cheche365.cheche.externalapi"
})
class ExternalAPIConfig {

    @Bean
    IConfigService fileBasedConfigService(Environment env) {
        return new FileBasedConfigService(env);
    }
}
