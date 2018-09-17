package com.cheche365.cheche.partner.api.common

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.partner.api.SyncAgentApi
import com.cheche365.cheche.partner.serializer.CommonChannelAgent
import com.sun.jersey.api.client.ClientResponse
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static java.lang.Boolean.TRUE

/**
 * 同步代理人信息
 * Created by liheng on 2018/7/11 0011.
 */
@Service
@Slf4j
class CommonAgent extends SyncAgentApi {

    @Override
    ApiPartner apiPartner() {
        null
    }

    @Override
    def prepareData(apiInput) {
        new CommonChannelAgent().generateChannelAgent apiInput
    }

    @Override
    def call(apiInput) {
        def afterConvertToJson = serializeBody prepareData(apiInput)
        ClientResponse clientResponse
        try {
            clientResponse = apiClient(apiInput).type(getMediaType()).post(ClientResponse.class, afterConvertToJson)
            handleResponse apiPartner(apiInput), clientResponse
            TRUE
        } catch (Exception e) {
            sendEmail e, apiInput
            log.error '同步代理人信息出错, 第三方code:{}, 代理人信息:{}, 错误信息:{}', apiPartner(apiInput).code, afterConvertToJson, e.message
        } finally {
            if (clientResponse) {
                clientResponse.close()
            }
        }
    }
}
