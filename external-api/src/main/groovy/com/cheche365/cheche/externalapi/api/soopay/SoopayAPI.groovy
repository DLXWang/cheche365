package com.cheche365.cheche.externalapi.api.soopay

import com.cheche365.cheche.externalapi.ExternalAPI
import com.umpay.api.common.ReqData
import com.umpay.api.paygate.v40.Mer2Plat_v40
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 12/02/2018.
 */

@Service
abstract class SoopayAPI extends ExternalAPI {

    @Autowired
    Environment env


    def call(Map params){
        ReqData signedParams = Mer2Plat_v40.makeReqDataByPost(params)

        super.call([
            qs: ([sign:signedParams.sign] + signedParams.getField())
        ])
    }

    @Override
    String host() {
        env.getProperty('soopay.host')
    }

    @Override
    String path() {
        'spay/pay/payservice.do'
    }


    @Override
    String method() {
        'GET'
    }

    @Override
    Class responseType() {
        String
    }
}
