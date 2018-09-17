package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.core.service.spi.IPayService
import com.cheche365.cheche.externalapi.api.huanong.HuanongPaymentAPI
import com.cheche365.cheche.externalapi.api.huanong.HuanongTokenAPI
import com.cheche365.cheche.externalapi.model.HuanongProposal
import com.cheche365.cheche.externalapi.model.huanong.HuanongPaymentResponse
import com.cheche365.cheche.externalpayment.handler.QrUploadHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import static com.cheche365.cheche.externalapi.api.huanong.HuanongPaymentAPI.PAYMENT_CHANNEL_WECHARTS

/**
 * Created by wen on 2018/8/6.
 */
@Service
class HuanongPaymentService implements IPayService {

    private Logger logger = LoggerFactory.getLogger(HuanongPaymentService.class);

    @Autowired
    Environment env

    @Autowired
    HuanongPaymentAPI huanongPaymentAPI

    @Autowired
    QuoteRecordCacheService cacheService

    @Autowired
    IConfigService configService

    @Autowired
    HuanongTokenAPI tokenAPI

    @Autowired
    QuoteRecordRepository quoteRecordRepository

    @Autowired
    QrUploadHandler qrUploadHandler

    @Override
    def prePay(Map<String, Object> params) {
        Payment payment = params.payment

        QuoteRecord quoteRecord = quoteRecordRepository.findOne(payment.purchaseOrder.objId)
        HuanongPaymentResponse response = huanongPaymentAPI.call(buildRequest(payment,getToken(quoteRecord)))

        validateResponse(response,payment)

        if(response.payLmg()){
            String convertUrl = qrUploadHandler.createQrCode(response.payLmg() as String)
            logger.info("支付二维码转换后地址 : ${convertUrl}")
            return  [qrCodePayUrl : convertUrl]
        }

        [externalUrl : response.payUrl()]

    }

    def buildRequest(Payment payment,String token){
        HuanongProposal proposal = new HuanongProposal(payment.purchaseOrder,cacheService)
        [
            OrderNo : [payment.purchaseOrder.orderSourceId],
            poaType : PAYMENT_CHANNEL_WECHARTS,
            VerificationCode : proposal.verificationCode(),
            Emile :   huanongPaymentAPI.envPropertyNew('email'),
            PhoneNo : proposal.phone(),
            token  : token
        ]
    }

    def validateResponse(HuanongPaymentResponse response,Payment payment){
        if(!response.isSuccess() || !response.payLmg() || !response.payUrl()){
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED , response.responseMsg() ?: '支付链接获取失败')
        }

        if(response.totalAmount().compareTo(payment.amount) != 0 ){
            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,'保险公司价格出现变动，请谨慎支付')
        }

        logger.debug("华农${payment.channel.description}支付源地址: ${response.payUrl()}")

    }

    def getToken(QuoteRecord quoteRecord){
        HuanongPaymentResponse tokenResp = tokenAPI.call(quoteRecord)
        if(!tokenResp.isSuccess()){
            throw new BusinessException(BusinessException.Code.PARTNER_TOKEN_OUT_TIME , tokenResp.responseMsg() ?: '获取token失败')
        }

        tokenResp.token()
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
        return PaymentChannel.Enum.HUANONG_SUPPORT_CHANNELS.contains(pc)
    }


}
