package com.cheche365.abao.core.app.config

import org.kie.api.runtime.KieContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

import static org.kie.api.KieServices.Factory.get as getKieServices


@Configuration
@ComponentScan([
    'com.cheche365.abao.core.highmedical.service',
    'com.cheche365.abao.core.highmedical.model'
])
class AbaoCoreConfig {

    @Bean
    KieContainer kieContainer() {
        getKieServices().kieClasspathContainer
    }

}
