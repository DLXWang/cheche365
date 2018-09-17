package com.cheche365.cheche.internal.integration.na.api

import com.cheche365.cheche.internal.integration.ApiClient
import groovy.json.JsonSlurper

/**
 * Created by zhengwei on 6/24/17.
 */
class SyncCallback {

    static final String PATH = 'payments/callback/sync'

    static def call(Long paymentChannelId, Map originalQuery){
        ApiClient.naClient().path(PATH)
        .queryParam('paymentChannel', paymentChannelId.toString())
        .with { resource ->
            originalQuery.each {
                resource = resource.queryParam(it.key, it.value)
            }
            resource
        }
        .get(String)?.with {
            new JsonSlurper().parseText(it).data
        }
    }
}
