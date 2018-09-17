package com.cheche365.cheche.core.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories('com.cheche365.cheche.core.mongodb.repository')
class MongodbConfig {
}
