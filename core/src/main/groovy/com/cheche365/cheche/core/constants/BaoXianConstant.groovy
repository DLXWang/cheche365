package com.cheche365.cheche.core.constants

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.OrderTransmissionStatus

import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1


class BaoXianConstant {

    public static final String PAY_SUCCESS = "25"
    public static final String LACK_OF_IMAGE ="19"
    public static final String INSURE_SUCCESS ="20"
    public static final String ORDER_SUCCESS ="23"
    public static final String CLOSED ="30"
    public static final String FINISHED ="33"
    public static final String POLICY_LIMIT ="51"

    public static final CALL_BACK_STATE = [
        (PAY_SUCCESS)             : [
            desc         : '支付成功',
            syncBills    : true,
            action       : '更新order payment状态',
            statusDisplay: '出单中',
            validStatus: [PENDING_PAYMENT_1]
        ],
        (LACK_OF_IMAGE)           : [
            desc         : '补齐影像',
            syncBills    : false,
            action       : '保存待上传影像',
            statusDisplay: '核保中',
            validStatus: [OrderStatus.Enum.INSURE_FAILURE_7]
        ],
        (INSURE_SUCCESS)          : [
            desc         : '核保成功',
            syncBills    : true,
            action       : '更新订单状态',
            statusDisplay: null,
            validStatus: [OrderStatus.Enum.INSURE_FAILURE_7]
        ],
        (POLICY_LIMIT)             : [
            desc         : '承保失败',
            syncBills    : true,
            action       : '更新订单状态',
            statusDisplay: '承保失败',
            validStatus: [OrderStatus.Enum.PAID_3]
        ],
        (ORDER_SUCCESS)             : [
            desc         : '承保成功待配送',
            syncBills    : true,
            action       : '执行订单完成流程',
            statusDisplay: null,
            validStatus: [OrderStatus.Enum.PAID_3]
        ],
        (FINISHED)                  : [
            desc         : '完成',
            syncBills    : false,
            action       : '忽略',
            statusDisplay: null,
            validStatus: [OrderStatus.Enum.FINISHED_5]
        ],
        (CLOSED)                  : [
            desc         : '核保失败、支付失败、承保失败',
            syncBills    : false,
            action       : '更新订单状态',
            statusDisplay: null,
            validStatus: null
        ]
    ]

    public static final INSURE_FAIL_MAPPING=[
        POLICY_LIMIT,CLOSED
    ]
    public static final INSURE_FAILED ='核保失败'
    public static final BAOXIAN_ORDER_STATUS_MAPPING = [
        (OrderStatus.Enum.PENDING_PAYMENT_1) : OrderTransmissionStatus.Enum.UNPAID,
        (OrderStatus.Enum.INSURE_FAILURE_7) : OrderTransmissionStatus.Enum.UNDERWRITING_FAILED
    ]

    def static validateCBStatus(taskState, orderStatus){
        if(taskState == CLOSED) return true  //返回状态为30的时候，跳过检查，到具体的流程里再做校验
        CALL_BACK_STATE.get(taskState)?.validStatus?.contains(orderStatus)
    }

}
