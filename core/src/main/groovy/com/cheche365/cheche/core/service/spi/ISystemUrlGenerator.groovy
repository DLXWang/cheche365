package com.cheche365.cheche.core.service.spi

/**
 * Created by zhengwei on 08/02/2018.
 */
interface ISystemUrlGenerator {

    String toPaymentUrl(String orderNo)

    String toImageUrl(String orderNo)

    String toSuspendBillUrlOriginal(String orderNo)

    String renewalOrder(String orderNo)

    String toOrderDetailUrl(String orderNo)

}
