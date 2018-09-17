package com.cheche365.cheche.core.service

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.model.Payment
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.PaymentStatus.Enum.*
import static com.cheche365.cheche.core.model.PaymentType.Enum.*
import static com.cheche365.cheche.core.util.BigDecimalUtil.bigDecimalValue
import static com.cheche365.cheche.core.util.BigDecimalUtil.subtract

/**
 * Created by zhengwei on 10/22/16.
 * payment计算器，提供常用计算方法
 */

@Service
class PaymentAmountCalculator {

    /**
     * 用户已付金额
     */
    BigDecimal customerPaid(List<Payment> payments){
        def paidAmount = payments.findAll {it.status == PAYMENTSUCCESS_2 && [INITIALPAYMENT_1, ADDITIONALPAYMENT_2].contains(it.paymentType)}.amount.sum() ?: 0
        bigDecimalValue(paidAmount)
    }

    /**
     * 用户已退金额
     */
    BigDecimal customerRefunded(List<Payment> payments){
        def refundAmount = payments.findAll {it.status == PAYMENTSUCCESS_2 && [PARTIALREFUND_3, FULLREFUND_4].contains(it.paymentType)}.amount.sum() ?: 0
        bigDecimalValue(refundAmount)
    }


    /**
     * 用户净付款，付款－退款
     */
    BigDecimal customerNetPaid(List<Payment> payments){
        subtract(customerPaid(payments), customerRefunded(payments))
    }

    /**
     * 用户应付增补
     */
    Double customerAdditionalPayable(List<Payment> payments) {

        def additionalAmount = payments.findAll {it.status == NOTPAYMENT_1 && ADDITIONALPAYMENT_2 == it.paymentType}.amount.sum() ?: 0
        return additionalAmount > 0.0 ? DoubleUtils.displayDoubleValue(additionalAmount) : null;
    }


    /**
     * 车车优惠
     */
    static BigDecimal chechePaid(List<Payment> payments){
        def cheCheAmount = payments.findAll { [DISCOUNT_5, CHECHEPAY_6, BAOXIANPAY_8].contains(it.paymentType) && it.status == PAYMENTSUCCESS_2}.amount.sum() ?: 0
        bigDecimalValue(cheCheAmount)
    }


}
