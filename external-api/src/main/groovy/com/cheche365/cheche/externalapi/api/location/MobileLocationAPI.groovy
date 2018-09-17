package com.cheche365.cheche.externalapi.api.location

import com.cheche365.cheche.externalapi.ExternalAPI
import groovy.json.JsonSlurper
import net.sf.json.util.JSONUtils
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 12/02/2018.
 */

@Service
class MobileLocationAPI extends ExternalAPI {


    def call(String mobile) {

        String response = super.call([
                qs: [
                        number: mobile
                ],
                header: [
                        apikey: '142f43d26ba765121f898a37b1637f3b'
                ]
        ])

        if(!JSONUtils.mayBeJSON(response)){
            return null
        }

        def responseObj = new JsonSlurper().parseText(response)
        def city = responseObj?.data?.city ?: responseObj?.data?.province

        return city ? city+'市' : null

    }
    @Override
    String method() {
        'GET'
    }

    @Override
    Class responseType() {
        String
    }

    @Override
    String path() {
        'phonearea.php'
    }

    @Override
    String host() {
        'https://cx.shouji.360.cn'
    }
}
