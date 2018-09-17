package com.cheche365.cheche.externalapi.api.lifeinsurace

import com.cheche365.cheche.core.util.MD5
import com.cheche365.cheche.externalapi.ExternalAPI
import com.sun.jersey.api.client.ClientResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service


/**
 * Created by zhengwei on 11/02/2018.
 * 意外险投保API
 */

@Service
class InsuranceAPI extends ExternalAPI {

    @Autowired
    Environment env

    String call(Map body) {
        body.adCode = env.getProperty('life.insurance.adCode')
        body.activityConfigNum = 0
        body.sign = MD5.MD5Encode(body.adCode + env.getProperty('life.insurance.sign') + body.mobile)

        ClientResponse response = super.call([
                body: body
        ])

        response.getEntity(String)

    }


    @Override
    String method() {
        'POST'
    }

    @Override
    Class responseType() {
        ClientResponse
    }

    @Override
    String host() {
        env.getProperty('life.insurance.host')
    }
}
