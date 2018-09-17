package com.cheche365.cheche.externalapi.api.soopay

import com.sun.jersey.api.client.ClientResponse
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 12/02/2018.
 */

@Service
class SoopayPrepayAPI extends SoopayAPI {

    String call(Map params){
        ClientResponse response = super.call(params)
        return response.location.toString()
    }


    @Override
    Class responseType() {
        ClientResponse
    }
}
