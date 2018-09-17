package com.cheche365.cheche.internal.integration.na.api

import groovy.json.JsonSlurper

import static com.cheche365.cheche.internal.integration.ApiClient.*

/**
 * Created by zhengwei on 6/23/17.
 */

class PayParams {

    static final String PATH = 'payments/order/{}/payment'

    static def call(String orderNo){
       naClient().path(formattedPath(PATH, orderNo)).get(String)?.with {
            new JsonSlurper().parseText(it).data
        }
    }


}
