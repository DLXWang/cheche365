package com.cheche365.cheche.chinalife.config

import com.cheche365.cheche.chinalife.app.config.AChinalifeConfig
import com.cheche365.cheche.chinalife.service.ChinalifeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * Chinalife的Spring配置注释类
 */
@Configuration
class ChinalifeMinTestConfig extends AChinalifeConfig {

    @Bean
    chinalifeService(Environment env) {
        new ChinalifeService(env)
    }

}
