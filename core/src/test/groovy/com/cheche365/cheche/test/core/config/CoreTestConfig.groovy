package com.cheche365.cheche.test.core.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration

@EnableJpaRepositories('com.cheche365.cheche.test.core.repository')
@ImportResource([
    'classpath:META-INF/spring/datasource-context-spring.xml'
])
class CoreTestConfig {
}
