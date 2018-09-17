package com.cheche365.cheche.botpy.client.app.config

import com.cheche365.cheche.core.service.IThirdPartyAutoTypeService
import com.cheche365.cheche.core.service.IThirdPartyCheckAccountService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.client.service.ThirdPartyAutoTypeService
import com.cheche365.cheche.parser.client.service.IAutoTypeFeignClient
import com.cheche365.cheche.parser.client.service.IThirdPartyCheckAccountFeignClient
import com.cheche365.cheche.parser.client.service.IThirdPartyHandlerFeignClient
import com.cheche365.cheche.parser.client.service.ThirdPartyCheckAccountService
import com.cheche365.cheche.parser.client.service.ThirdPartyHandlerService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

import static com.cheche365.cheche.core.model.QuoteSource.Enum.*


/**
 * botpy客户端的生产配置
 */
@ComponentScan([
    'com.cheche365.cheche.core.service',
    'com.cheche365.cheche.parser.app.config'
])
@EnableFeignClients('com.cheche365.cheche.botpy.client.service')
abstract class ABotpyClientConfig {

    @Bean
    IThirdPartyAutoTypeService botpyAutoTypeService(
        @Qualifier('botpyAutoTypeFeignClient') IAutoTypeFeignClient client
    ) {
        new ThirdPartyAutoTypeService(client)
    }

    @Bean
    IThirdPartyHandlerService botpyThirdPartyHandlerService(
        @Qualifier('botpyThirdPartyHandlerFeignClient') IThirdPartyHandlerFeignClient client
    ) {
        new ThirdPartyHandlerService(
            client,
            { conditions ->
                PLATFORM_BOTPY_11 == conditions.quoteSource
            }
        )
    }

    @Bean
    IThirdPartyCheckAccountService botpyCheckAccountService(
        @Qualifier('botpyThirdPartyAccountTowerFeignClient') IThirdPartyCheckAccountFeignClient client
    ) {
        new ThirdPartyCheckAccountService(
            client,
            { conditions ->
                PLATFORM_BOTPY_11 == conditions.quoteSource
            }
        )
    }


}
