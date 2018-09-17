package com.cheche365.cheche.picc.config

import com.cheche365.cheche.core.service.IThirdPartyAutoTypeService
import com.cheche365.cheche.picc.service.PiccAutoTypeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class PiccAutoTypeServiceConfig {
    @Bean
    IThirdPartyAutoTypeService autoTypeService(Environment env){
        new PiccAutoTypeService(env)
    }
}
