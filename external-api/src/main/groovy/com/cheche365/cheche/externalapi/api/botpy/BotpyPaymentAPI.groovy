package com.cheche365.cheche.externalapi.api.botpy

import org.springframework.stereotype.Service

@Service
class BotpyPaymentAPI extends BotpyAPI{

    def call(String paymentChannelName, String proposalNo, String bankCode) {

        def arguments=[
            channel : paymentChannelName,
        ]

        if(bankCode){
            arguments << [co_bank : bankCode]
        }

       super.call(
           [
                   path : [proposalNo],
                   body : [
                       type : 'payment',
                       arguments : arguments
                   ]
           ]
       )

    }

    @Override
    String method(){
      "POST"
    }

    @Override
    String path(){
        "proposals/{proposal_id}/requests"
    }
}
