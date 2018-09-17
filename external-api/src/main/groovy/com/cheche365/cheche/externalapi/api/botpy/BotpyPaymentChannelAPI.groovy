package com.cheche365.cheche.externalapi.api.botpy

import org.springframework.stereotype.Service


@Service
class BotpyPaymentChannelAPI extends BotpyAPI{


    def call(String orderSourceId) {
               super.call(
                   [
                           path : [orderSourceId],
                   ]
               )
    }

    @Override
    String method(){
      "GET"
    }

    @Override
    String path(){
        "proposals/{proposal_id}/payment-channels"
    }
}
