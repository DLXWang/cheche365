package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.core.service.spi.IPayService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.externalapi.api.botpy.BotpyPaymentAPI
import com.cheche365.cheche.externalapi.api.botpy.BotpyPaymentStatusAPI
import com.cheche365.cheche.externalpayment.handler.QrUploadHandler
import com.cheche365.cheche.externalpayment.handler.botpy.polling.BotpyPollingHandler
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.externalpayment.model.BotpyCallBackBody.PAYMENT_CHANNEL_CO_BANK

@Service
class BotpyPaymentService implements IPayService {

    private Logger logger = LoggerFactory.getLogger(BotpyPaymentService.class);

    @Autowired
    BotpyPaymentAPI botpyPaymentAPI

    @Autowired
    BotpyPollingHandler pollingHandler

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    QrUploadHandler botpyUploadHandler

    @Autowired
    StringRedisTemplate stringRedisTemplate

    @Autowired
    BotpyPaymentStatusAPI botpyPaymentStatusAPI

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Autowired
    QuoteRecordRepository quoteRecordRepository


    @Override
    def prePay(Map<String, Object> params) {

        hint(params) //金斗云支付前提示用户点击短信链接

        Payment payment = params.payment

        Map cacheResultChannels=CacheUtil.doJacksonDeserialize(stringRedisTemplate.opsForHash().get(PAYMENT_CHANNEL_CO_BANK,payment.purchaseOrder.orderNo),Map)
        logger.debug("获取金斗云支付渠道缓存 ${cacheResultChannels}")

        String bankCode
        if(cacheResultChannels && cacheResultChannels.channels.find{it.code ==payment.channel.name}?.require_co_bank == Boolean.TRUE && cacheResultChannels.co_banks){
            bankCode=cacheResultChannels.co_banks[0].code
            logger.debug("金斗云支付渠道支持合作银行 code:${bankCode}")
        }else{
            logger.debug("金斗云支付渠道不支持合作银行")
        }

        Map result = botpyPaymentAPI.call(payment.channel.name, payment.purchaseOrder.orderSourceId,bankCode)

        if(!result){
            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,'支付二维码获取失败')
        }

        logger.info("支付二维码信息轮训获取中,订单号${payment.purchaseOrder.orderNo}")
        String cacheCallBackBody = pollingHandler.polling(result.notification_id)
        if(!cacheCallBackBody){
            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,'支付二维码获取超时,请稍后重试')
        }

        payResultHandle(payment,cacheCallBackBody)

    }

    private hint(Map<String, Object> params) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(params.orderNo)
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.objId)
        if (InsuranceCompany.Enum.PICC_10000 ==  quoteRecord.insuranceCompany && Area.Enum.BJ == quoteRecord.area && params.channel.isLevelAgent()) {
            String NEED_HINT_KEY = 'need_hint:' + params.orderNo
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
            request.session.getAttribute(NEED_HINT_KEY) ? request.session.setAttribute(NEED_HINT_KEY, false) : request.session.setAttribute(NEED_HINT_KEY, true)
            if (request.session.getAttribute(NEED_HINT_KEY)) {
                throw new BusinessException(BusinessException.Code.SMS_CONFIRMATION, '请阅读短信中的投保单链接，并点击链接中的确认按钮')
            }
        }
    }


    def payResultHandle(Payment payment,String cacheCallBackBody){
        def callBackBody= new BotpyCallBackBody(cacheCallBackBody)
        if(callBackBody.success()){
            def qrUrl=callBackBody.paymentQRCodeUrl()
            if(!qrUrl){
                throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,"当前订单不支持${payment.channel.description}扫码支付")
            }

            payment.thirdpartyPaymentNo = callBackBody.paymentTradeNo()
            paymentRepository.save(payment)
            logger.info("支付二维码源地址 : ${qrUrl}")

            def convertUrl=botpyUploadHandler.convertToServer(qrUrl)
            logger.info("支付二维码转换后地址 : ${convertUrl}")

            return  [qrCodePayUrl : convertUrl]
        } else {
            def message=callBackBody.dataComment() ?: callBackBody.dataMessage()
            logger.debug("金斗云渠道未返回支付二维码,跟踪单号: ${callBackBody.trackingNo()}; 订单号:${payment.purchaseOrder.orderNo}, message : ${message}")
            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,message)
        }
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
        return PaymentChannel.Enum.BOTPY_SUPPORT_CHANNELS.contains(pc)
    }

}
