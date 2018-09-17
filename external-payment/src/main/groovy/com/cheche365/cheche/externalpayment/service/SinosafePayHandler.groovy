package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.core.service.OrderRelatedService.OrderRelated
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.core.service.ThirdPartyPaymentTemplate
import com.cheche365.cheche.core.util.FormUtil
import com.cheche365.cheche.externalapi.api.sinosafe.SinosafePrePayAPI
import com.cheche365.cheche.externalapi.model.sinosafe.SinosafePrePayResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSAFE_205000
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey
import static com.cheche365.cheche.externalpayment.constants.SinosafeConstant.*

/**
 * 华安支付
 * Created by wenling on 2018/1/8.
 */
@Component
class SinosafePayHandler implements ThirdPartyPaymentTemplate  {
    
    private Logger logger = LoggerFactory.getLogger(SinosafePayHandler.class);

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    QuoteRecordCacheService quoteRecordCacheService
    @Autowired
    private PurchaseOrderRepository orderRepository
    @Autowired
    private MoApplicationLogRepository logRepository

    @Autowired
    OrderRelatedService orderRelatedService

    @Autowired
    SinosafePrePayAPI prePayAPI

    @Autowired
    Environment env

    @Override
    boolean acceptable(QuoteRecord quoteRecord) {
        return SINOSAFE_205000 == quoteRecord.getInsuranceCompany()
    }

    @Override
    Object prePay(PurchaseOrder purchaseOrder, Channel channel, QuoteRecord quoteRecord) {

        String verifyCode
        Map additionalQRMap
        if(AREAS_REQUIRE_VERIFY_CODE.contains(purchaseOrder.area.id)){
            additionalQRMap = quoteRecordCacheService.getPersistentState(persistQRParamHashKey(quoteRecord.getId()))
            verifyCode = additionalQRMap?.persistentState?.verifyCode
            if(!verifyCode){
                throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,'请输入验证码')
            }
        }

        OrderRelated or = orderRelatedService.initByOrderNo(purchaseOrder.orderNo)

        SinosafePrePayResponse result = prePayAPI.call(or, verifyCode, BACK_URL)

        if(result.success()){
            savePaymentOutTradeNo(or, result.outTradeNo())
            return FormUtil.buildForm(result.payAddress(), result.payParams(), 'GET')
        }else{

            saveApplicationLog(purchaseOrder,result.errorMessage())

            if(result.reInsure()){
                updatePurchaseOrder(purchaseOrder)
                throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,'您已在华安保险投保，请勿重复下单')
            }

            if(verifyCode){
                additionalQRMap?.persistentState?.remove('verifyCode')
                quoteRecordCacheService.cachePersistentState(persistQRParamHashKey(quoteRecord.getId()), additionalQRMap)
                updatePurchaseOrder(purchaseOrder)
            }

            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,'支付申请失败')
        }
    }

    def updatePurchaseOrder(PurchaseOrder purchaseOrder){
        if(OrderStatus.Enum.PENDING_PAYMENT_1 == purchaseOrder.status){
            purchaseOrder.status=OrderStatus.Enum.INSURE_FAILURE_7
            orderRepository.save(purchaseOrder)
        }
    }

    def savePaymentOutTradeNo(OrderRelated or,String outTradeNo){
        Payment pending = or.findPending()
        if(!pending){
            return
        }
        savePayment(pending, outTradeNo)
    }

    private Payment savePayment(Payment payment, String outTradeNo) {
        payment.setOutTradeNo(outTradeNo);
        return paymentRepository.save(payment);
    }

    def saveApplicationLog(PurchaseOrder order,message){
        MoApplicationLog appLog = MoApplicationLog.applicationLogByPurchaseOrder(order, LogType.Enum.INSURE_FAILURE_1);
        appLog.setLogMessage(message);
        appLog.setCreateTime(Calendar.getInstance().getTime());
        logRepository.save(appLog);
    }
}
