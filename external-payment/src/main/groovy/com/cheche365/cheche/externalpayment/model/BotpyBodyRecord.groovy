package com.cheche365.cheche.externalpayment.model

/**
 * Created by zhengwei on 15/03/2018.
 * 金斗云StatusChange回调会包含多张保单信息，每个BotpyBodyRecord对象表示一张保单
 */

class BotpyBodyRecord extends BotpyCallBackBody {

    BotpyBodyRecord(Map parsed) {
        super(parsed)
    }

    boolean statusChange() {
        parsed.status_changed as boolean
    }

    String proposalStatus() {
        parsed.proposal_status
    }

    String oldProposalStatus() {
        parsed.old_proposal_status
    }

    Map billNos() {
        parsed.ic_nos
    }

    boolean isPaidProcessed(){
        proposalStatus() == STATUS_PAID && payStatusCallbackProcessed()
    }
}
