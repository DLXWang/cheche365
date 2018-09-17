package com.cheche365.cheche.externalapi.api.za

import com.cheche365.cheche.externalapi.ExternalAPI
import com.sun.jersey.api.representation.Form
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import javax.ws.rs.core.MediaType

import static com.cheche365.cheche.core.util.MockUrlUtil.findBaseUrl

/**
 * Created by wen on 2018/5/29.
 */
@Service
class ZaSignStatusAPI extends ExternalAPI{


    @Autowired
    Environment env

    def call(Map reqParam){

        Form paramsInForm = new Form()
        reqParam.each {
            paramsInForm.putSingle(it.key, it.value)
       }
        super.call([
                body : paramsInForm
            ]
        )
    }

    @Override
    MediaType contentType() {
        return MediaType.APPLICATION_FORM_URLENCODED_TYPE
    }

    @Override
    Class responseType(){
        String
    }

    @Override
    String host() {
        return  findBaseUrl() ?: env.getProperty('zhongan.api_base_url')
    }

    @Override
    String method() {
        return 'POST'
    }
}
