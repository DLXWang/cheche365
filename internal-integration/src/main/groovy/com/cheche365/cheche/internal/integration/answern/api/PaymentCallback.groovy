package com.cheche365.cheche.internal.integration.answern.api

import com.cheche365.cheche.core.model.PaymentChannel
import com.sun.jersey.api.representation.Form

import static com.cheche365.cheche.internal.integration.ApiClient.answernClient
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.*

/**
 * Created by zhengwei on 7/12/17.
 */
class PaymentCallback {

    static final String PATH = 'ax/pay/notify'
    static final String PAY_SUCCESS_TAG = '1'
    static final String PAY_FAIL_TAG = '0'
    static final String ALI_PAY = '2'
    static final String WECHAT_PAY = '1'

    static def call(PaymentChannel pc, Map callbackParams){
        answernClient().path(PATH).post(String, toAnswnerFormat(pc, callbackParams))?.with {
            it
        }
    }

    static toAnswnerFormat(PaymentChannel pc, Map callbackParams){

        Form answnerParams = new Form()
        answnerParams.putSingle('payResult', callbackParams.payResult ? PAY_SUCCESS_TAG : PAY_FAIL_TAG)
        answnerParams.putSingle('orderNo', callbackParams.outTradeNo)
        answnerParams.putSingle('attach', 'restart')
        answnerParams.putSingle('checheRestart', true)

        if(ALIPAY_1 == pc){
            answnerParams.putSingle('payType', ALI_PAY)
        }else {
            answnerParams.putSingle('payType', WECHAT_PAY)
        }

        return answnerParams
    }

}
