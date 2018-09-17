package com.cheche365.cheche.externalapi.api.botpy

import org.springframework.stereotype.Service

/**
 * Created by Administrator on 2018/6/8.
 */
@Service
class BotpyGetAccountAPI extends BotpyAPI {

    def call(String accountId) {

        super.call(
            [
                path : [accountId]
            ]
        )

    }

    @Override
    String method(){
        "GET"
    }


    @Override
    String path(){
        "accounts/{account_id}"
    }
}
