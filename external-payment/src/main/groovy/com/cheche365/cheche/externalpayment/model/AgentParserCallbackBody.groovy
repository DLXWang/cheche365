package com.cheche365.cheche.externalpayment.model

import static com.cheche365.cheche.common.util.DateUtils.DATE_SHORTDATE_PATTERN
import static com.cheche365.cheche.common.util.DateUtils.getDate
import static com.cheche365.cheche.core.constants.ModelConstants._PAYMENT_STATUS_FAIL
import static com.cheche365.cheche.core.constants.ModelConstants._PAYMENT_STATUS_PROCESSING
import static com.cheche365.cheche.core.constants.ModelConstants._PAYMENT_STATUS_SUCCESS

/**
 * Created by wen on 2018/5/28.
 */
class AgentParserCallbackBody extends BaseCallbackBody{

    Map parsed

    AgentParserCallbackBody(parsed) {
        this.parsed = parsed
    }

    String orderNo(){
        parsed.orderNo
    }

    def payStatus(){
        parsed.payStatus
    }

    boolean isSuccess(){
        _PAYMENT_STATUS_SUCCESS == payStatus()
    }

    boolean isFail(){
        _PAYMENT_STATUS_FAIL == payStatus()
    }

    boolean isProcessing(){
        _PAYMENT_STATUS_PROCESSING == payStatus()
    }


    def message(){
        parsed.message
    }

    def outTradeNo(){
        parsed.outTradeNo
    }

    def compulsoryInsurance(){
        parsed.compulsoryInsurance
    }

    String ciProposalNo(){
        compulsoryInsurance()?.ProposalNo
    }

    String ciPolicyNo(){
        compulsoryInsurance()?.PolicyNo
    }

    Date ciStartDate(){
        getDate(compulsoryInsurance()?.EffectiveDate as String, DATE_SHORTDATE_PATTERN)
    }

    Date ciEndDate(){
        getDate(compulsoryInsurance()?.ExpireDate as String, DATE_SHORTDATE_PATTERN)
    }

    def commercialInsurance(){
        parsed.commercialInsurance
    }

    String insuranceProposalNo(){
        commercialInsurance()?.ProposalNo
    }

    String insurancePolicyNo(){
        commercialInsurance()?.PolicyNo
    }

    Date insuranceStartDate(){
        getDate( commercialInsurance()?.EffectiveDate as String, DATE_SHORTDATE_PATTERN)
    }

    Date insuranceEndDate(){
        getDate(commercialInsurance()?.ExpireDate as String, DATE_SHORTDATE_PATTERN)
    }

}
