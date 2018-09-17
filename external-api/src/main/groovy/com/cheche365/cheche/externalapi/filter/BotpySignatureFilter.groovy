package com.cheche365.cheche.externalapi.filter

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.externalapi.signature.botpy.BotpySignature
import com.sun.jersey.api.client.ClientHandlerException
import com.sun.jersey.api.client.ClientRequest
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.filter.ClientFilter
import org.springframework.core.env.Environment
import static java.lang.System.currentTimeMillis
import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew


class BotpySignatureFilter extends ClientFilter {

    Environment env = ApplicationContextHolder.getApplicationContext().getBean(Environment.class)
    IConfigService configService = ApplicationContextHolder.getApplicationContext().getBean(IConfigService.class)

    @Override
    ClientResponse handle(ClientRequest request) throws ClientHandlerException {

        long timeStamp = (currentTimeMillis() / 1000L).toLong()
        def appKey = configValue('app_key')
        def accept = "application/vnd.botpy.${configValue('app_version')}+json"
        def appid = configValue('app_id')
        
        request.headers.add('Accept',accept)
        request.headers.add('Authorization',"appid "+appid)
        request.headers.add('X-Yobee-Timestamp',timeStamp)
        request.headers.add('X-Yobee-Signature',
            BotpySignature.sign([
                timeStamp:timeStamp,
                appKey:appKey,
                accept:accept,
                appid:appid
            ], request))

        return getNext().handle(request)
    }

    String configValue(String key) {

        def newEnv = [env: env, configService: configService, namespace: 'botpy']
        getEnvPropertyNew(newEnv, key, null, [])
    }
}
