package com.cheche365.cheche.externalapi.api.shorturl

import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.externalapi.ExternalAPI
import com.cheche365.cheche.externalapi.filter.ShortUrlHeaderFilter
import com.sun.jersey.api.client.filter.ClientFilter
import com.sun.jersey.api.representation.Form
import groovy.json.JsonOutput
import org.springframework.stereotype.Service

import javax.ws.rs.core.MediaType

/**
 * Created by zhengwei on 07/02/2018.
 */

@Service
class BatchCreateAPI extends ExternalAPI {

    String call(String longURL) {
        Form form = new Form()
        form.add(
            'payload',
            new JsonOutput().toJson(
                [
                    [
                        index  : '001',
                        longurl: RuntimeUtil.isDevEnv() ? replaceDevDomain(longURL) : longURL
                    ]
                ])
        )

        Map response = super.call([
            body: form
        ])

        if(1 == response.status){
            return response.data[0].data
        } else {
            return null
        }

    }

    static String replaceDevDomain(String longURL) {
        longURL.replace('http://localhost:7310', 'https://dev1.cheche365.com')  //短链接服务不能用localhost:7310做为域名生成，否则会报错，所以生成时用https://dev1.cheche365.com替换
    }

    List<ClientFilter> filters() {
        [
            new ShortUrlHeaderFilter()
        ]
    }

    @Override
    String method() {
        'POST'
    }

    @Override
    String host() {
        'https://0x3.me/apis'
    }

    @Override
    String path() {
        'urls/batchAdd'
    }

    @Override
    MediaType contentType() {
        MediaType.APPLICATION_FORM_URLENCODED_TYPE
    }
}
