package com.cheche365.cheche.externalpayment.model

import com.cheche365.cheche.core.model.OrderStatus

/**
 * Created by wen on 2018/8/7.
 */
class HuanongCallbackBody extends BaseCallbackBody{


    static final Map HUANONG_UNDERWRITE_STATUS = [
        ('1') : [
            desc  : '核保通过',
            toStatus : OrderStatus.Enum.PENDING_PAYMENT_1
        ],
        ('2') : [
            desc  : '提交上级',
            statusDisplay : '核保中'
        ],
        ('3') : [
            desc  : '审核不通过'
        ],
        ('4') : [
            desc  : '转人工审核',
            statusDisplay : '核保中'
        ].asImmutable()
    ]

    static final TYPE_UNDERWRITE = 'UNDWRTNOTICEFORTHIRD' ///核保
    static final TYPE_ORDER = 'TPCALLBACKFORTHIRD'//承保
    static final CALLBACK_TYPES = [TYPE_UNDERWRITE,TYPE_ORDER]

    def NEED_UPLOAD_KEYWORD = ['影像', '图片', '照片', '上传','补充', '材料']

    Map callbackBody

    def HuanongCallbackBody(Map params){
        callbackBody = params
    }

    def head(){
        callbackBody?.head
    }

    def transCode(){
        head().transCode
    }

    def isUnderWrite(){
        transCode() == TYPE_UNDERWRITE
    }

    def isOrder(){
        transCode() == TYPE_ORDER
    }

    def orderNo(){
        callbackBody?.orderNo
    }

    def status(){
        callbackBody?.status
    }

    String auditOpinion(){
        callbackBody?.auditOpinion
    }

    def isNeedUploadImages(){
        auditOpinion() &&  NEED_UPLOAD_KEYWORD.any{ auditOpinion().contains(it)}
    }

    def isNeedVehicleExaminatios(){
        auditOpinion() && auditOpinion().contains("验车")
    }

    def auditTime(){
        callbackBody?.auditTime
    }

    def isOrderSuccess(){
        status() == '2'
    }

    @Override
    String ciPolicyNo() {
        return callbackBody?.forcePolicyNo
    }

    @Override
    String ciProposalNo() {
        return null
    }

    @Override
    Date ciStartDate() {
        return null
    }

    @Override
    Date ciEndDate() {
        return null
    }

    @Override
    String insurancePolicyNo() {
        return callbackBody?.bizPolicyNo
    }

    @Override
    String insuranceProposalNo() {
        return null
    }

    @Override
    Date insuranceStartDate() {
        return null
    }

    @Override
    Date insuranceEndDate() {
        return null
    }


    def buildResponse(){
        [
            Head:
                [
                    logId: UUID.randomUUID().toString(),
                    responseCode:0,
                    responseMsg:"操作成功",
                    transCode:transCode(),
                    transType:"RES"
                ]
        ]
    }
}
