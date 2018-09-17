package com.cheche365.cheche.cpicuk.app.config

import com.cheche365.cheche.core.service.FileBasedConfigService
import com.cheche365.cheche.core.service.IConfigService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment



/**
 * cpicuk的Spring配置注释类
 */
@Configuration
@Import([CpicUKConfig])
class CpicUKMinTestConfig {

    @Bean
    IConfigService fileBasedConfigService(Environment env) {
        new FileBasedConfigService(env)
    }
}
