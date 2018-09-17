package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.externalapi.model.TkProposal
import com.cheche365.cheche.externalpayment.handler.SyncPurchaseOrderHandler
import com.cheche365.cheche.externalpayment.model.TkResponseBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by wen on 2018/4/10.
 */
@Service
class TkCallbackService {

    private Logger logger = LoggerFactory.getLogger(TkCallbackService.class);

    @Autowired
    MoApplicationLogRepository logRepository

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    InsuranceRepository iRepo

    @Autowired
    CompulsoryInsuranceRepository ciRepo

    @Autowired
    SyncPurchaseOrderHandler syncOrderHandler

    @Autowired
    QuoteRecordCacheService cacheService

    def handle(TkResponseBody body) {

        Payment payment = paymentRepository.findByOutTradeNo(body.proposalFormId())
        PurchaseOrder purchaseOrder = payment?.purchaseOrder
        if (!purchaseOrder) {
            logger.info("泰康订单 ${body.proposalFormId()} 不存在")
            return
        }

        if(purchaseOrder.statusFinished()){
            logger.info("泰康订单 ${purchaseOrder.orderNo} 已成功出单,此次不做处理")
            return
        }

        logger.info("泰康订单 ${purchaseOrder.orderNo} 异步回调处理,参数 ${body.response} ")
        purchaseOrderHandle(body,payment,purchaseOrder)

    }

    def purchaseOrderHandle(TkResponseBody body,Payment payment,PurchaseOrder purchaseOrder){
        syncOrderHandler.purchaseOrderPaidHandle(payment,body.success())
        if (body.success()) {
            updateInsurance(purchaseOrder,body)
        }else{
            persistLog(purchaseOrder, body)
        }

    }


    def updateInsurance(PurchaseOrder purchaseOrder,TkResponseBody body){
        TkProposal proposal=new TkProposal(purchaseOrder,cacheService)
        Insurance insurance = iRepo.findByQuoteRecordId(purchaseOrder.objId)
        insurance?.policyNo = body.subIPolicyNo()
        insurance?.expireDate = proposal.insuranceExpireDate()
        insurance?.effectiveDate = proposal.insuranceEffectiveDate()

        CompulsoryInsurance ci=ciRepo.findByQuoteRecordId(purchaseOrder.objId)
        ci?.policyNo = body.subCiPolicyNo()
        ci?.expireDate = proposal.ciExpireDate()
        ci?.effectiveDate = proposal.ciEffectiveDate()
        syncOrderHandler.syncInsurances(insurance,ci)
    }

    void persistLog(PurchaseOrder po, TkResponseBody body){
        def template = MoApplicationLog.applicationLogByPurchaseOrder(po, LogType.Enum.ORDER_RELATED_3)
        template.logId = body.proposalFormId()
        template.logMessage = body.errorMessage()
        logRepository.save(template)
    }

}
