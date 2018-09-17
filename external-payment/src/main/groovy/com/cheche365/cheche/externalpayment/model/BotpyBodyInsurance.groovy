package com.cheche365.cheche.externalpayment.model

/**
 * Created by wenling on 28/03/2018.
 * 验证身份证验证码回调，部分保险公司进行保费重算时包含多个商业险险种信息，每个BotpyBodyInsurance对应一个险种
 */

class BotpyBodyInsurance extends BotpyCallBackBody {

    BotpyBodyInsurance(Map parsed) {
        super(parsed)
    }

    Double amount() {
        parsed.amount as Double
    }

    String code() {
        parsed.code
    }

    boolean isInsure() {
        parsed.insured as boolean
    }

    Double premium() {
        parsed.premium as Double
    }

}
