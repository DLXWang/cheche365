package com.cheche365.cheche.externalpayment

import com.cheche365.cheche.web.service.payment.PaymentService

/**
 * Created by zhengwei on 4/15/17.
 * 泛华支付成功回调用例
 */
class BX_CB_PAY_FT extends InputFileSpec {

    def "泛华支付成功回调测试"() {

        given:
        def paymentService = Stub(PaymentService){
            initOR(_) >> {

            }
        }
    }
}
