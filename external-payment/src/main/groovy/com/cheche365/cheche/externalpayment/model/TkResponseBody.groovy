package com.cheche365.cheche.externalpayment.model

import com.cheche365.cheche.core.util.URLUtils

import static com.cheche365.cheche.common.util.DateUtils.DATE_LONGTIME24_PATTERN

/**
 * Created by wen on 2018/4/10.
 */
class TkResponseBody {

    Map response

    TkResponseBody(Map res){
        response = res
    }

    Map head(){
        response.head
    }

    Map content(){
        response.apply_content
    }

    boolean isSuccess(){
        content().reponseCode == '200'
    }

    String proposalFormId(){
        head().proposalFormId
    }

    String proposalFormToken(){
        head().proposalFormToken
    }

    String version(){
        head().version
    }

    def reqMsgId(){
        head().reqMsgId
    }

    String payUrl(){
        content()?.payWay
    }

    Map payParams() {
        URLUtils.splitQuery(payUrl().toURI().query)
    }

    String tradeNo(){
        content()?.wspTradeNo
    }

    boolean success(){
        content().success == '1'
    }

    String errorCode(){
        content().errorCode
    }

    String errorMessage(){
        content().errorMessage
    }

    String policyNo(){
        content().policyNo
    }

    String  subIPolicyNo(){
        content().subPolicyNo_BI
    }

    String subCiPolicyNo(){
        content().subPolicyNo_CI
    }

    def messageBody(){
        content().messageBody
    }



    def buildResponse(){
        [
            head : [
                proposalFormToken : proposalFormToken(),
                proposalFormId :  proposalFormId(),
                version : version(),
                function : 'policyNotice',
                respTime : Calendar.instance.format(DATE_LONGTIME24_PATTERN),
                reqMsgId : reqMsgId(),
            ],
            apply_content : [
                reponseCode : '200'
            ]
        ]
    }
}
