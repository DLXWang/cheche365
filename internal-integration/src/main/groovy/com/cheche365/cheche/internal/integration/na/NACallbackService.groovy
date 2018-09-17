package com.cheche365.cheche.internal.integration.na

import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.internal.integration.na.api.AsyncCallback
import com.cheche365.cheche.internal.integration.na.api.SyncCallback
import groovy.json.JsonOutput

/**
 * Created by zhengwei on 6/24/17.
 */
class NACallbackService {

    static String syncCallback(PaymentChannel pc, Map queryParams){
        SyncCallback.call(pc.id, queryParams)?.with{
            it.redirectUrl
        }
    }

    static asyncCallback(PaymentChannel pc, Map queryParams) {
        def body = JsonOutput.toJson(queryParams)
        AsyncCallback.call(pc.id, body)
    }
}
