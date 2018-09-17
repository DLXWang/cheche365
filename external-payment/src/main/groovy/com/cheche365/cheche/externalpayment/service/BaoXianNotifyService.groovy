package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.constants.BaoXianConstant
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.AddressRepository
import com.cheche365.cheche.core.repository.DeliveryInfoRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.*
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil
import com.cheche365.cheche.core.util.BigDecimalUtil
import com.cheche365.cheche.wallet.service.WalletTradeService
import com.cheche365.cheche.web.service.payment.PaymentService
import com.cheche365.cheche.web.service.payment.PaymentService.OrderRelated
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.constants.BaoXianConstant.BAOXIAN_ORDER_STATUS_MAPPING

@Service
@Slf4j
class BaoXianNotifyService {

    private PaymentService paymentService
    private PurchaseOrderImageService poiService
    private PurchaseOrderService purchaseOrderService
    private AddressRepository addressRepository
    private DeliveryInfoRepository deliveryInfoRepository
    private WalletTradeService walletTradeService
    @Autowired
    private QuoteRecordCacheService cacheService
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderOperationInfoService orderOperationInfoService
    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;
    @Autowired
    private DoubleDBService mongoDBService

    BaoXianNotifyService(PurchaseOrderService purchaseOrderService, PaymentService paymentService, PurchaseOrderImageService poiService,
                                AddressRepository addressRepository,DeliveryInfoRepository deliveryInfoRepository, WalletTradeService walletTradeService) {
        this.purchaseOrderService = purchaseOrderService
        this.paymentService = paymentService
        this.poiService = poiService
        this.addressRepository = addressRepository
        this.deliveryInfoRepository = deliveryInfoRepository
        this.walletTradeService = walletTradeService
    }

    def onPaySuccess(OrderRelated or, payableAmount, paidAmount ,Map state,def paymentState) {

        paymentService.onPaySuccess(or, { Payment currentPayment ->
            or.po.channel = PaymentChannel.Enum.BAOXIAN_PAY_16
            or.po.payableAmount = payableAmount as Double
            or.po.paidAmount = paidAmount as Double
            or.po.statusDisplay = state.get(paymentState).statusDisplay
            currentPayment.amount = or.po.paidAmount
            currentPayment.channel = PaymentChannel.Enum.BAOXIAN_PAY_16
        })

        ConditionTriggerUtil.sendPaymentSuccessMessage(conditionTriggerHandler, or.qr, or.po);
    }

    def onPayFail(OrderRelated or, payableAmount, paidAmount) {

        paymentService.onPayFail(or, { Payment currentPayment ->
            or.po.channel = PaymentChannel.Enum.BAOXIAN_PAY_16
            or.po.payableAmount = payableAmount as Double
            or.po.paidAmount = paidAmount as Double
            currentPayment.amount = or.po.paidAmount
            currentPayment.channel = PaymentChannel.Enum.BAOXIAN_PAY_16
        })
    }

    @Transactional
    void onOrder(OrderRelated or) {
        paymentService.onOrder(or, null)
    }

    void onOrderFail(OrderRelated or) {
        or.po.subStatus = OrderSubStatus.Enum.FAILED_1
        updatePurchaseOrder(or.po)
    }


    @Transactional
    void onExpress(OrderRelated or){
        log.info("泛华订单完成订单:{}",or.po.orderNo);
        if(null!=or.po.deliveryInfo) {
            DeliveryInfo deliveryInfo = deliveryInfoRepository.save(or.po.deliveryInfo)
            or.po.deliveryInfo = deliveryInfo
            purchaseOrderService.saveOrder(or.po)
        }

    }

    void updatePurchaseOrder(PurchaseOrder order){
        purchaseOrderService.saveOrder(order)
    }



    def bxInitOR(taskId){
        paymentService.initOR { poRepo ->
            poRepo.findByOrderSourceId(taskId, OrderSourceType.Enum.PLANTFORM_BX_5)[0]
        }
    }


    def updateOrderOrigin(params,OrderRelated or){
        if(BaoXianConstant.INSURE_SUCCESS == params.taskState){
            insureSuccess(params,or)
            log.info("该操作${params.taskId} 核保成功  ,车车状态为${or.po.status.description}")
        }else if (BaoXianConstant.LACK_OF_IMAGE == params.taskState){
            if(params.bodyObj.imageInfos){
                 poiService.onImage(or.po, params.bodyObj.imageInfos as List, BaoXianConstant.CALL_BACK_STATE as Map, BaoXianConstant.LACK_OF_IMAGE)
                log.info("该操作${params.taskId} 状态为补充影像信息  ,车车状态为${or.po.status.description}")
            }else{
                log.info("该操作${params.taskId} 状态为补充影像信息之审核意见方式")
                poiService.persistCustomImage(or.po, params.bodyObj.errorMsg)
                MoApplicationLog log=MoApplicationLog.applicationLogByPurchaseOrder(or.po)
                log.logId = params.taskId
                log.logType= LogType.Enum.BAOXIAN_35
                log.logMessage= params.errorMsg
                log.objId = or.po.id as String
                log.createTime = new Date()
                mongoDBService.saveApplicationLog(log)
            }

        }else{
            log.info("该操作${params.taskId}状态为${BaoXianConstant.CALL_BACK_STATE.get(params.taskState).desc}，车车状态为${or.po.status.description},子状态为:${or.po?.orderSubStatus?.name} ，此次不做处理")
            syncOrderOperationInfo(or.po)
        }
    }

    @Transactional
    def insureSuccess(params,OrderRelated or){
        //泛华核保成功后返回的金额可能会出现和报价的金额不一致的情况，这里需要更新一下订单
        def callBackAmount = Double.valueOf(params.insureInfo.totalPremium)
        def diffAmount = BigDecimalUtil.subtract(callBackAmount, or.po.payableAmount).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()
        if (diffAmount != 0) {
            or.po.payableAmount=callBackAmount
            or.po.paidAmount=callBackAmount
            List<Payment> payments=paymentRepository.findCustomerPendingPayments(or.po)
            Payment payment= payments[0]
            payment.amount=callBackAmount
            paymentRepository.save(payment);
        }
        or.po.status=OrderStatus.Enum.PENDING_PAYMENT_1

        updatePurchaseOrder(or.po)
        syncOrderOperationInfo(or.po)

    }


    def syncOrderOperationInfo(PurchaseOrder order) {
        OrderTransmissionStatus newStatus =BAOXIAN_ORDER_STATUS_MAPPING.get(order.status)
        if(newStatus){
            OrderOperationInfo operationInfo=orderOperationInfoService.updateOrderTransmissionStatus(order, newStatus)
            log.debug("泛华订单状态同步更新出单中心,订单号:{},订单状态:{},出单中心状态:{}", order.getOrderNo(), order?.status?.status, operationInfo?.currentStatus?.description);
        }
    }
}
