package com.cheche365.cheche.externalpayment.controller

import com.cheche365.cheche.core.annotation.ConcurrentApiCall
import com.cheche365.cheche.core.constants.BaoXianConstant
import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.mongo.MongoUser
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.*
import com.cheche365.cheche.web.service.payment.PaymentService.OrderRelated
import com.cheche365.cheche.externalpayment.service.BaoXianNotifyService
import com.cheche365.cheche.externalpayment.template.BaoXianPayService
import com.cheche365.cheche.externalpayment.util.BaoXianCode
import com.cheche365.cheche.web.response.RestResponseEnvelope
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey
import static com.cheche365.cheche.externalpayment.util.BaoXianCode.syncBills

@Controller
@RequestMapping("/api/callback/baoxian/v2")
class BaoXianCallBackResourceV2 {

    private final Logger logger = LoggerFactory.getLogger(BaoXianCallBackResourceV2.class);

    @Autowired
    private BaoXianNotifyService baoXianNotifyService

    @Autowired
    private IRefundService baoXianRefundService

    @Autowired
    private List<UnifiedRefundHandler> refundPayChannelHandlers

    @Autowired
    private PaymentRepository paymentRepo

    @Autowired
    private DoubleDBService mongoDBService

    @Autowired
    private QuoteRecordCacheService cacheService

    @Autowired
    private OrderOperationInfoService ooiService

    @Autowired
    private QuoteConfigService quoteConfigService
    @Autowired
    private BaoXianPayService baoXianPayService
    @Autowired
    private PurchaseOrderService purchaseOrderService

    @RequestMapping(value = "/payments", method = RequestMethod.POST)
    @ConcurrentApiCall(exclusive = true, value = { Object[] args -> new groovy.json.JsonSlurper().parseText(args[0]).with{it.taskId + '_' + it.taskState}})
    HttpEntity<RestResponseEnvelope<Object>> payments(@RequestBody String body){
        def params = syncLocal(body)
        OrderRelated or = params.or

        if( BaoXianConstant.PAY_SUCCESS == params.taskState) {
            logger.info("泛化支付成功回调taskId: {}, orderNo: {} ,orderStatus : {}", params.taskId,  params.or.po.orderNo,or.po.status.status)
            baoXianNotifyService.onPaySuccess(or, params.insureInfo.totalPremium, params.insureInfo.totalPremium, BaoXianConstant.CALL_BACK_STATE, BaoXianConstant.PAY_SUCCESS)
        }else if(BaoXianConstant.CLOSED == params.taskState && or.po.status == OrderStatus.Enum.PENDING_PAYMENT_1){
            logger.info("泛化支付成功回调taskId: {}, orderNo: {} ,orderStatus : {}", params.taskId,  params.or.po.orderNo,or.po.status.status)
            baoXianNotifyService.onPayFail(or, params.insureInfo.totalPremium, params.insureInfo.totalPremium)
        }else {
            logger.info("泛华支付 ${params.taskId} 回调为非预期状态，此次不做处理")
        }

        return new ResponseEntity<>(new RestResponseEnvelope(["payload": ["code": 200]]), HttpStatus.OK);
    }

    @RequestMapping(value = "/insurances", method = RequestMethod.POST)
    @ConcurrentApiCall(exclusive = true, value = { Object[] args -> new groovy.json.JsonSlurper().parseText(args[0]).with{it.taskId + '_' + it.taskState}})
    HttpEntity<RestResponseEnvelope<Object>> insurances(@RequestBody String body){

        def params = syncLocal(body)
        OrderRelated or = params.or

        Map additionalQRMap = cacheService.getPersistentState(persistQRParamHashKey(or.qr.id))
        if(additionalQRMap?.persistentState){
            additionalQRMap.persistentState.insureResult = params.bodyObj
            cacheService.cachePersistentState(persistQRParamHashKey(or.qr.id), additionalQRMap);
        }

        baoXianNotifyService.updateOrderOrigin(params,or)

        return new ResponseEntity<>(new RestResponseEnvelope(["payload": ["code": 200]]), HttpStatus.OK);
    }


    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    @ConcurrentApiCall(exclusive = true, value = { Object[] args -> new groovy.json.JsonSlurper().parseText(args[0]).with{it.taskId + '_' + it.taskState}})
    HttpEntity<RestResponseEnvelope<Object>> orders(@RequestBody String body){

        def params = syncLocal(body)
        OrderRelated or = params.or

        if(BaoXianConstant.ORDER_SUCCESS == params.taskState) {
            baoXianNotifyService.onOrder(or)
        }else if (BaoXianConstant.FINISHED == params.taskState) {
            def delivery = params.bodyObj.delivery;
            BaoXianCode.setExpress(delivery, or.po);
            baoXianNotifyService.onExpress(or)
        }else if(BaoXianConstant.CLOSED == params.taskState && or.po.status == OrderStatus.Enum.PAID_3){
            baoXianNotifyService.onOrderFail(or)
            logger.debug('泛华回调收到承保失败状态， 泛华状态码 {}, 车车订单状态 {},子状态 {}',params.taskState, or.po.status.id,or.po?.orderSubStatus?.name)
        } else {
            purchaseOrderService.saveOrder(or.po)
            logger.info("非预期task state $params.taskState")
        }
        return new ResponseEntity<>(new RestResponseEnvelope(["payload": ["code": 200]]), HttpStatus.OK);
    }

    def syncLocal(String requestBody){
        logger.info("泛华回调,原始参数:{}", requestBody)
        def params = [:]
        params << new JsonSlurper().parseText(requestBody).with{
            [
                bodyObj: it,
                taskId: it.taskId,
                taskState: it.taskState,
                insureInfo: it.insureInfo,
            ]
        }

        params << [or: baoXianNotifyService.bxInitOR(params.taskId)]

        if(BaoXianConstant.CALL_BACK_STATE.get(params.taskState)){
            params.or.po.statusDisplay =  BaoXianConstant.CALL_BACK_STATE.get(params.taskState).statusDisplay
        }else{
            params.or.po.statusDisplay = null
            logger.debug("taskId : ${params.taskId} , 泛华状态码 ${params.taskState} ,状态描述 ${params.taskState?.taskStateDescription} statusDisplay置为null")
        }

        if (!BaoXianConstant.validateCBStatus(params.taskState, params.or.po.status)) {
            createApplicationLog(requestBody, params, "状态验证失败,${BaoXianConstant.CALL_BACK_STATE.get(params.taskState).desc},${params.or.po.status.status}")
            logger.info("状态验证失败,${BaoXianConstant.CALL_BACK_STATE.get(params.taskState).desc},${params.or.po.status.status}")
            return new ResponseEntity<>(new RestResponseEnvelope(["payload": ["code": 200]]), HttpStatus.OK);
        }

        if (BaoXianConstant.CALL_BACK_STATE.get(params.taskState).syncBills) {
            syncBills(params.insureInfo, params.or as OrderRelated)
        }

        logger.info("泛化回调taskId: {}, taskState: {}, orderNo: {}", params.taskId, params.taskState, params.or.po.orderNo)
        logger.info("泛华状态: {}, 车车策略: {}", BaoXianConstant.CALL_BACK_STATE.get(params.taskState).desc, BaoXianConstant.CALL_BACK_STATE.get(params.taskState).action)

        def desc = "${params.taskState} ${ BaoXianConstant.CALL_BACK_STATE.get(params.taskState).desc} ${ BaoXianConstant.CALL_BACK_STATE.get(params.taskState).action}"
        createApplicationLog(requestBody,params,desc)

        return params
    }

    def createApplicationLog(requestBody,params,desc){
        new MoApplicationLog(
            logType: LogType.Enum.BAOXIAN_35,
            logId: params.taskId,
            instanceNo: desc,
            logMessage: requestBody,
            user: MongoUser.toMongoUser(params.or.po.applicant),
            objTable: 'purchase_order',
            objId: params.or.po.id as String,
            createTime: new Date()
        ).with {
            mongoDBService.saveApplicationLog(it)
        }
    }

}
