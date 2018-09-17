package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.ThirdPartyPaymentTemplate
import com.cheche365.cheche.core.util.FormUtil
import com.cheche365.cheche.externalapi.api.tk.TkPaymentAPI
import com.cheche365.cheche.externalapi.api.tk.TkPaymentValidAPI
import com.cheche365.cheche.externalpayment.model.TkResponseBody
import com.cheche365.cheche.web.service.PaymentCallbackURLHandler
import com.cheche365.cheche.web.util.ClientTypeUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class TkPaymentService implements ThirdPartyPaymentTemplate {

    private Logger logger = LoggerFactory.getLogger(TkPaymentService.class);

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    TkPaymentAPI tkPaymentAPI

    @Autowired
    private PaymentCallbackURLHandler paymentCallbackURLHandler

    @Autowired(required = false)
    public HttpServletRequest request;

    @Autowired
    TkPaymentValidAPI tkPaymentValidAPI


    @Override
    Object prePay(PurchaseOrder purchaseOrder, Channel channel, QuoteRecord quoteRecord) {

        Payment payment = findPaymentByPurchaseOrder(purchaseOrder)
        preValidate(payment)

        TkResponseBody body = getPaymentUrl(payment)

        return FormUtil.buildForm( body.payUrl(),body.payParams(),'GET')

    }

    def preValidate(Payment payment) {

        TkResponseBody validBody = new TkResponseBody(tkPaymentValidAPI.call(payment,null))
        if(!validBody?.isSuccess()){
            logger.error("泰康支付校验失败，message : ${validBody.messageBody()}")
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED , validBody.messageBody() ?: '支付校验失败')
        }

    }

    def getPaymentUrl(Payment payment){
        Map result = tkPaymentAPI.call(
            payment,
            [
                platformId:convertChannel(),
                frontUrl:paymentCallbackURLHandler.toFrontCallBackPage(payment.purchaseOrder, request)
            ]
        )
        TkResponseBody body = new TkResponseBody(result)
        if(!body?.isSuccess() || !body?.payUrl()){
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED , body.messageBody() ?: '支付链接获取失败')
        }

        logger.debug("泰康在线${payment.channel.description}收银台地址: ${body.payUrl()}")
        savePayment(payment,body)

        body
    }


    @Override
    boolean acceptable(QuoteRecord quoteRecord) {
        return InsuranceCompany.Enum.TK_80000== quoteRecord.insuranceCompany
    }


    def savePayment(Payment payment,TkResponseBody body){
        payment.outTradeNo = body.proposalFormId()
        payment.channel = PaymentChannel.Enum.TK_55
        paymentRepository.save(payment)
        logger.debug("泰康订单号 ${payment.purchaseOrder.orderNo} , 流水号更新为 ${payment.outTradeNo}")
    }

    def convertChannel(){
        ClientTypeUtil.inWechat(request) ? 'PUB_ONLINE' : 'APP'
    }

    def findPaymentByPurchaseOrder(PurchaseOrder order){
        List<Payment> payments = paymentRepository.findCustomerPendingPayments(order)
        if(!payments){
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "支付订单不存在");
        }
        if(payments.size() > 1){
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "存在多余一笔待支付记录");
        }
        payments[0]
    }

}
