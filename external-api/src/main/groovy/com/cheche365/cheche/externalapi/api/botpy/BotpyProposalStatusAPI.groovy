package com.cheche365.cheche.externalapi.api.botpy

import org.springframework.stereotype.Service

/**
 * Created by Administrator on 2018/3/5.
 *
 * 获取投保单状态
 *
 */
@Service
class BotpyProposalStatusAPI extends BotpyAPI{


    def call(String proposalId) {
        super.call(
            [
                path : [proposalId],
            ]
        )
    }

    @Override
    String method(){
        "GET"
    }

    @Override
    String path(){
        "proposals/{proposal_id}"
    }
}
