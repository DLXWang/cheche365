package com.cheche365.cheche.piccuk.client.app.config

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

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9



/**
 * piccuk客户端的生产配置
 */
@ComponentScan([
    'com.cheche365.cheche.core.service',
    'com.cheche365.cheche.parser.app.config'
])
@EnableFeignClients('com.cheche365.cheche.piccuk.client.service')
abstract class APiccUKClientConfig {

    @Bean
    IThirdPartyHandlerService piccUKThirdPartyHandlerService(
//        ApplicationContextHolder _appContextHolder, // <-- 绝对不许删
        @Qualifier('piccUKThirdPartyHandlerFeignClient') IThirdPartyHandlerFeignClient client
    ) {
        new ThirdPartyHandlerService(client, {conditions ->
        PICC_10000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)}
        )
    }

    @Bean
    IThirdPartyPaymentService piccUKPaymentService(
        @Qualifier('piccUKThirdPartyPaymentFeignClient') IThirdPartyPaymentFeignClient client
    ) {
        new ThirdPartyPaymentService(client, {conditions ->
            PICC_10000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)}
        )
    }

    @Bean
    IThirdPartyQuoteRecordService piccUKQuoteRecordService(
        @Qualifier('piccUKThirdPartyQuoteRecordFeignClient') IThirdPartyQuoteRecordFeignClient client
    ) {
        new ThirdPartyQuoteRecordService(client, { conditions ->
            PICC_10000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)}
        )
    }

    @Bean
    IThirdPartyCheckAccountService piccUKCheckAccountService(
        @Qualifier('piccUKThirdPartyCheckAccountFeignClient') IThirdPartyCheckAccountFeignClient client
    ) {
        new ThirdPartyCheckAccountService(client, { conditions ->
            PICC_10000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)}
        )
    }

}
