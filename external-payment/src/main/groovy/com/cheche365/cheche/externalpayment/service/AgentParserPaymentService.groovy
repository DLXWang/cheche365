package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.AttributeType
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.AGENT_PARSER_SUPPORT_CHANNELS
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.core.service.OrderAttributeService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.core.service.spi.IPayService
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.externalpayment.handler.QrUploadHandler
import org.apache.commons.codec.binary.Base64
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.servlet.http.HttpSession

import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey

/**
 * Created by wen on 2018/9/10.
 */
@Service
class AgentParserPaymentService implements IPayService {

    private Logger logger = LoggerFactory.getLogger(AgentParserPaymentService.class)

    @Autowired(required = false)
    List<IThirdPartyPaymentService> iThirdPartyPaymentInfoService

    @Autowired
    InsuranceRepository insuranceRepo

    @Autowired
    CompulsoryInsuranceRepository compulsoryInsuranceRepo
    @Autowired
    QrUploadHandler qrUploadHandler

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    PurchaseOrderRepository poRepository

    @Autowired
    QuoteRecordCacheService cacheService

    @Autowired(required = false)
    HttpSession session

    @Autowired
    OrderAttributeService orderAttributeService

    @Override
    def prePay(Map<String, Object> params) {

        QuoteRecord quoteRecord = params.quoteRecord
        IThirdPartyPaymentService paymentService = findService(quoteRecord)

        PurchaseOrder purchaseOrder = poRepository.findFirstByOrderNo(params.orderNo)
        def paymentInfo = getPaymentInfo(paymentService, quoteRecord, purchaseOrder)

        Payment payment = paymentRepository.findFirstByPurchaseOrder(purchaseOrder)
        savePayment(payment, paymentInfo.metaInfo)
        saveOrderAttribute(purchaseOrder, paymentInfo)

        [qrCodePayUrl: qrUploadHandler.upload(new Base64().decode(paymentInfo.paymentURL))]
    }

    def getPaymentInfo(IThirdPartyPaymentService paymentService,QuoteRecord quoteRecord, PurchaseOrder purchaseOrder) {

        def paymentParams = buildPaymentParams(quoteRecord,purchaseOrder)
        def paymentInfo = paymentService.getPaymentInfo(paymentParams[0],paymentParams[1])

        logger.info("根据订单号 ${purchaseOrder.orderNo} 获取小鳄鱼的支付二维码信息： ${paymentInfo} ")
        if (!paymentInfo?.paymentURL) {
            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED, "${quoteRecord.insuranceCompany.name}不支持在线扫码支付")
        }

        paymentInfo
    }

    def buildPaymentParams(QuoteRecord quoteRecord, PurchaseOrder purchaseOrder){
        Map persistentState = cacheService.getPersistentState(persistQRParamHashKey(purchaseOrder.objId))?.persistentState
        logger.info("小鳄鱼支付persistentState： ${persistentState} ")

        [
            [
                orderNo: purchaseOrder.orderNo,
                channel : purchaseOrder.channel,
                commercial: insuranceRepo.findByQuoteRecordId(quoteRecord.id)?.proposalNo,
                compulsory: compulsoryInsuranceRepo.findByQuoteRecordId(quoteRecord.id)?.proposalNo
            ],
            [
                persistentState: persistentState,
                quoteRecord    : quoteRecord
            ] << MockUrlUtil.additionalParameters()
        ]
    }

     def findService(QuoteRecord quoteRecord){
        IThirdPartyPaymentService paymentService = iThirdPartyPaymentInfoService.find {
            it.isSuitable([insuranceCompany: quoteRecord.insuranceCompany, quoteSource: quoteRecord.type])
        }

        if (!paymentService) {
            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED, "未找到匹配的支付服务")
        }

        paymentService
    }

    def saveOrderAttribute(PurchaseOrder purchaseOrder, paymentInfo) {
        if (paymentInfo.metaInfo?.accountId) {
            orderAttributeService.savePurchaseOrderAttribute(purchaseOrder, AttributeType.Enum.AGENT_PARSER_ACCOUNT_2, paymentInfo.metaInfo.accountId as String)
        } else {
            logger.info("订单号 ${purchaseOrder.orderNo} 获取小鳄鱼账户信息异常")
        }
        if (paymentInfo.metaInfo?.serialNo){
            orderAttributeService.savePurchaseOrderAttribute(purchaseOrder, AttributeType.Enum.AGENT_PARSER_PICC_SERIAL_NO_3, paymentInfo.metaInfo.serialNo as String)
        }
        if (paymentInfo.metaInfo?.payType){
            orderAttributeService.savePurchaseOrderAttribute(purchaseOrder, AttributeType.Enum.AGENT_PARSER_PICC_PAY_TYPE_4, paymentInfo.metaInfo.payType as String)
        }
    }

    def savePayment(Payment payment, metaInfo) {
        payment.itpNo = metaInfo?.paymentNo as String
        paymentRepository.save(payment)

    }

    @Override
    def refund(Map<String, Object> params) {
        return null
    }

    @Override
    def syncCallback(Map<String, Object> params) {
        return null
    }

    @Override
    def asyncCallback(Map<String, Object> params) {
        return null
    }

    @Override
    boolean support(PaymentChannel pc) {
        return pc in AGENT_PARSER_SUPPORT_CHANNELS
    }
}
