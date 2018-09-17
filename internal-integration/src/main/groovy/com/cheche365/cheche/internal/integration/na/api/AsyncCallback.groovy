package com.cheche365.cheche.internal.integration.na.api

import com.cheche365.cheche.internal.integration.ApiClient
import groovy.json.JsonSlurper

import javax.ws.rs.core.MediaType

/**
 * Created by zhengwei on 6/24/17.
 */
class AsyncCallback {

    static final String PATH = 'payments/callback/async'

    static def call(Long paymentChannelId, String body){
        ApiClient.naClient().path(PATH)
            .queryParam('paymentChannel', paymentChannelId.toString())
            .entity(body, MediaType.APPLICATION_JSON_TYPE)
            .post(String)?.with {
                new JsonSlurper().parseText(it).data
            }
    }
}
