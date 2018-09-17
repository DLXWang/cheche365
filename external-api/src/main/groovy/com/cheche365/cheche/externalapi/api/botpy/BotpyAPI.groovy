package com.cheche365.cheche.externalapi.api.botpy

import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.externalapi.ExternalAPI
import com.cheche365.cheche.externalapi.filter.BotpySignatureFilter
import com.sun.jersey.api.client.filter.ClientFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.util.MockUrlUtil.findBaseUrl
import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew


@Service
abstract class BotpyAPI extends ExternalAPI {

    @Autowired
    Environment env

    @Autowired
    IConfigService configService

    String clientIdentifier //为了测试支付状态轮询，跨线程

    @Override
    String host(){
        findBaseUrl([client_identifier:clientIdentifier]) ?: getEnvPropertyNew([env: env, configService: configService, namespace: 'botpy'], 'base_url', null, [])
    }

    @Override
    List<ClientFilter> filters(){
        return [new BotpySignatureFilter()]
    }

}
