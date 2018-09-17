package com.cheche365.cheche.picc.client.app.config

import com.cheche365.cheche.core.service.IThirdPartyAutoTypeService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.client.service.ThirdPartyAutoTypeService
import com.cheche365.cheche.parser.client.service.IAutoTypeFeignClient
import com.cheche365.cheche.parser.client.service.IThirdPartyHandlerFeignClient
import com.cheche365.cheche.parser.client.service.ThirdPartyHandlerService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.WEBPARSER_2


/**
 * picc客户端的生产配置
 */
@ComponentScan([
    'com.cheche365.cheche.core.service',
    'com.cheche365.cheche.parser.app.config'
])
@EnableFeignClients('com.cheche365.cheche.picc.client.service')
abstract class APiccClientConfig {

    @Bean
    IThirdPartyAutoTypeService piccAutoTypeService(
        @Qualifier('piccAutoTypeFeignClient') IAutoTypeFeignClient client
    ) {
        new ThirdPartyAutoTypeService(client)
    }

    @Bean
    IThirdPartyHandlerService piccThirdPartyHandlerService(
        @Qualifier('piccThirdPartyHandlerFeignClient') IThirdPartyHandlerFeignClient client
    ) {
        new ThirdPartyHandlerService(
            client,
            { conditions ->
                PICC_10000 == conditions.insuranceCompany && (WEBPARSER_2 == conditions.quoteSource)
            }
        )
    }


}
