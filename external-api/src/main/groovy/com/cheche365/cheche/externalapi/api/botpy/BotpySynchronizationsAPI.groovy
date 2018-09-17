package com.cheche365.cheche.externalapi.api.botpy

import org.springframework.stereotype.Service

/**
 * Created by Administrator on 2018/3/5.
 *
 * 提交同步
 *
 */
@Service
class BotpySynchronizationsAPI extends BotpyAPI{

    def call(String proposalId) {

       super.call(
           [
                   body : [
                       proposal_id : proposalId,
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
        "synchronizations"
    }
}
