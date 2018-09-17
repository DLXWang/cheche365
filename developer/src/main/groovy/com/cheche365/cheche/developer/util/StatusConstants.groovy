package com.cheche365.cheche.developer.util

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.OrderSubStatus
import com.cheche365.cheche.core.model.OrderTransmissionStatus

/**
 * @Author shanxf
 * @Date 2018/4/24  21:25
 */
class StatusConstants {

    public static final List<OrderStatus> SUPPORT_STATUS = [OrderStatus.Enum.PAID_3, OrderStatus.Enum.FINISHED_5]

    public static final Map CHECHE_STATUS_MAPPING = [
        (OrderStatus.Enum.INSURE_FAILURE_7) : OrderTransmissionStatus.Enum.UNDERWRITING_FAILED,
        (OrderStatus.Enum.PENDING_PAYMENT_1): OrderTransmissionStatus.Enum.UNPAID,
        (OrderStatus.Enum.PAID_3)           : OrderTransmissionStatus.Enum.UNCONFIRMED,
        (OrderStatus.Enum.CANCELED_6)       : OrderTransmissionStatus.Enum.CANCELED,
        (OrderSubStatus.Enum.FAILED_1)      : OrderTransmissionStatus.Enum.UNCONFIRMED,
        (OrderStatus.Enum.REFUNDING_10)     : OrderTransmissionStatus.Enum.APPLY_FOR_REFUND,
        (OrderStatus.Enum.REFUNDED_9)       : OrderTransmissionStatus.Enum.REFUNDED
    ]
}
