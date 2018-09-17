package com.cheche365.cheche.mock.api

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.externalapi.ExternalAPI

class MockToken extends ExternalAPI{

    def call(Map params){
        super.call([
            body: params
        ])
    }

    @Override
    String method() {
        'POST'
    }

    @Override
    String host() {
        WebConstants.getDomainURL(false)
    }

    @Override
    String path() {
        '/v1.6/mock/token'
    }
}
