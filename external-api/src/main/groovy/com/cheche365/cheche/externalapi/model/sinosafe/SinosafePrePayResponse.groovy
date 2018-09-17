package com.cheche365.cheche.externalapi.model.sinosafe

import com.cheche365.cheche.core.util.URLUtils

/**
 * Created by zhengwei on 11/02/2018.
 */
class SinosafePrePayResponse extends SinosafeResponse {

    SinosafePrePayResponse(Map response) {
        super(response)
    }

    String outTradeNo() {
        base().PAY_APP_NO
    }

    String payAddress() {
        base().PAYADDRESS
    }

    Map payParams() {
        URLUtils.splitQuery(payAddress().toURI().query)
    }

    Map base() {
        response.PACKET.BODY.BASE
    }

    boolean reInsure(){
        errorMessage().contains(REINSURANCE_ERROR)
    }
}
