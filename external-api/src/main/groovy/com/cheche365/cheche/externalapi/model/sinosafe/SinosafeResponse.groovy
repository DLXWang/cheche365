package com.cheche365.cheche.externalapi.model.sinosafe

/**
 * Created by zhengwei on 11/02/2018.
 */
class SinosafeResponse {

    static final RESPONSE_CODE_SUCCESS = 'C00000000'
    static final REINSURANCE_ERROR = 'N0MBUC0UV9ZQG59L'

    Map response

    SinosafeResponse(Map response){
        this.response = response
    }

    boolean success(){
        RESPONSE_CODE_SUCCESS == response.PACKET.HEAD.RESPONSECODE
    }

    def errorMessage() {
        response.PACKET.HEAD.ERRORMESSAGE as String
    }

}
