package com.cheche365.cheche.parser.app.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import feign.Request
import feign.codec.Encoder
import org.springframework.beans.BeansException
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.web.HttpMessageConverters
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.feign.support.SpringEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

import java.text.SimpleDateFormat


@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
class ParserClientConfig {

    @Bean
    Encoder feignEncoder() {
        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper())
        ObjectFactory<HttpMessageConverters> objectFactory = new ObjectFactory<HttpMessageConverters>() {

            @Override
            HttpMessageConverters getObject() throws BeansException {
                return new HttpMessageConverters(jacksonConverter)
            }
        }
        new SpringEncoder(objectFactory)
    }

    ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        objectMapper.setDateFormat(new SimpleDateFormat('yyyy-MM-dd HH:mm:ss'))
        objectMapper
    }

    @Bean
    Request.Options feignRequestOptions(Environment env) {
        new Request.Options(
            env.getProperty('parser.connectTimeoutMillis', '600000') as int,
            env.getProperty('parser.readTimeoutMillis', '600000') as int
        )
    }

}
