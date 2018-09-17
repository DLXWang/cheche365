package com.cheche365.cheche.externalpayment.model

/**
 * Created by wenling on 7/05/2018.
 * 投保单状态model
 */

class BotpyBodyStatus extends BotpyCallBackBody {

    BotpyBodyStatus(Map parsed) {
        super(parsed)
    }

    boolean isError(){
        parsed.error
    }

    @Override
    String proposalStatus() {
        parsed.status
    }

    Map billNos() {
        parsed.ic_nos
    }
}
