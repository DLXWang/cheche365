package com.cheche365.cheche.core.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories('com.cheche365.cheche.core.repository')
@ImportResource([
    'classpath:META-INF/spring/datasource-context-spring.xml',
    'classpath:META-INF/spring/datasource-context-common.xml'
])
@PropertySource('classpath:META-INF/spring/datasource.properties')
class JpaConfig {
}
