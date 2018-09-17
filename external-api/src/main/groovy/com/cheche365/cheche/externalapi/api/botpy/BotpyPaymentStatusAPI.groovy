package com.cheche365.cheche.externalapi.api.botpy

import org.springframework.stereotype.Service

@Service
class BotpyPaymentStatusAPI extends BotpyAPI{

    def call(String proposalNo, String clientIdentifier = null) {
        this.clientIdentifier = clientIdentifier
        super.call(
            [
                path: [proposalNo],
                body: [
                    type: 'payment_status'
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
