package com.cheche365.cheche.cpic.config

import com.cheche365.cheche.cpic.app.config.ACpicConfig
import com.cheche365.cheche.cpic.service.CpicInsuranceInfoService
import com.cheche365.cheche.cpic.service.CpicService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * cpic的Spring配置注释类
 */
@Configuration
class CpicMinTestConfig extends ACpicConfig {

    @Bean
    cpicService(Environment env) {
        new CpicService(env)
    }

    @Bean
    cpicInsuranceInfoService() {
        new CpicInsuranceInfoService()
    }

}
