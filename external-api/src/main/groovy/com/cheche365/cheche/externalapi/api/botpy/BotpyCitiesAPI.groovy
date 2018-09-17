package com.cheche365.cheche.externalapi.api.botpy

import org.springframework.stereotype.Service

/**
 * Created by Administrator on 2018/3/5.
 */
@Service
class BotpyCitiesAPI extends BotpyAPI {

    def call() {

        super.call(
            [:]
        )

    }

    @Override
    String method(){
        "GET"
    }


    @Override
    String path(){
        "cities"
    }
}
