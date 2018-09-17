package com.cheche365.cheche.core.service.spi

import com.cheche365.cheche.core.model.PaymentChannel

/**
 * Created by zhengwei on 6/20/17.
 */
interface IPayService {

    //支付
    def prePay(Map<String, Object> params)

    //退款
    def refund(Map<String, Object> params)

    //同步回调
    def syncCallback(Map<String, Object> params)

    //异步回调
    def asyncCallback(Map<String, Object> params)

    //支持的支付方式
    boolean support(PaymentChannel pc)
}
