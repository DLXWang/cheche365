package com.cheche365.cheche.cpicuk.client.app.config

import com.cheche365.cheche.core.service.IThirdPartyCheckAccountService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.core.service.IThirdPartyQuoteRecordService
import com.cheche365.cheche.parser.client.service.IThirdPartyCheckAccountFeignClient
import com.cheche365.cheche.parser.client.service.IThirdPartyHandlerFeignClient
import com.cheche365.cheche.parser.client.service.IThirdPartyPaymentFeignClient
import com.cheche365.cheche.parser.client.service.IThirdPartyQuoteRecordFeignClient
import com.cheche365.cheche.parser.client.service.ThirdPartyCheckAccountService
import com.cheche365.cheche.parser.client.service.ThirdPartyHandlerService
import com.cheche365.cheche.parser.client.service.ThirdPartyPaymentService
import com.cheche365.cheche.parser.client.service.ThirdPartyQuoteRecordService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CPIC_25000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9



/**
 * cpicuk客户端的生产配置
 */
@ComponentScan([
    'com.cheche365.cheche.core.service',
    'com.cheche365.cheche.parser.app.config'
])
@EnableFeignClients('com.cheche365.cheche.cpicuk.client.service')
abstract class ACpicUKClientConfig {

    @Bean
    IThirdPartyHandlerService cpicUKThirdPartyHandlerService(
//        ApplicationContextHolder _appContextHolder, // <-- 绝对不许删
        @Qualifier('cpicUKThirdPartyHandlerFeignClient') IThirdPartyHandlerFeignClient client
    ) {
        new ThirdPartyHandlerService(client, {conditions ->
        CPIC_25000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)}
        )
    }

    @Bean
    IThirdPartyPaymentService cpicUKPaymentService(
        @Qualifier('cpicUKThirdPartyPaymentFeignClient') IThirdPartyPaymentFeignClient client
    ) {
        new ThirdPartyPaymentService(client, {conditions ->
            CPIC_25000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)}
        )
    }

    @Bean
    IThirdPartyQuoteRecordService cpicUKQuoteRecordService(
        @Qualifier('cpicUKThirdPartyQuoteRecordFeignClient') IThirdPartyQuoteRecordFeignClient client
    ) {
        new ThirdPartyQuoteRecordService(client, { conditions ->
            CPIC_25000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)}
        )
    }

    @Bean
    IThirdPartyCheckAccountService cpicUKCheckAccountService(
        @Qualifier('cpicUKThirdPartyCheckAccountFeignClient') IThirdPartyCheckAccountFeignClient client
    ) {
        new ThirdPartyCheckAccountService(client, { conditions ->
            CPIC_25000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)}
        )
    }

}
