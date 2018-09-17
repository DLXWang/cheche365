package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.ThirdPartyPaymentTemplate
import groovy.util.logging.Slf4j
import groovy.xml.StreamingMarkupBuilder
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.common.util.AreaUtils.getProvinceCode
import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.exception.BusinessException.Code.EXTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1
import static com.cheche365.cheche.core.model.PaymentType.Enum.INITIALPAYMENT_1
import static com.cheche365.cheche.core.util.RuntimeUtil.isProductionEnv
import static com.cheche365.cheche.externalpayment.constants.AnswernConstant.CHECK_VALUE
import static com.cheche365.cheche.externalpayment.constants.AnswernConstant.ORDER_NO
import static com.cheche365.cheche.externalpayment.constants.AnswernConstant.PAY_AMT
import static com.cheche365.cheche.externalpayment.constants.AnswernConstant.REQUEST_CODE
import static com.cheche365.cheche.externalpayment.util.PayUtil.getSign
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static java.lang.Math.round



/**
 * Created by chenqc on 2016/11/22.
 */
@Service
@Order(value = 3)
@Slf4j
class AnswernPayService implements ThirdPartyPaymentTemplate {

    @Autowired
    private PaymentRepository paymentRepository

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository

    @Autowired(required = false)
    public HttpServletRequest request

    @Autowired
    private StringRedisTemplate stringRedisTemplate

    @Autowired
    private IConfigService configService

    @Autowired
    private Environment env

    @Override
    boolean acceptable(QuoteRecord quoteRecord) {
        ANSWERN_65000 == quoteRecord.insuranceCompany && PENDING_PAYMENT_1 == purchaseOrderRepository.findByQuoteRecordId(quoteRecord.getId()).status
    }

    @Override
    def prePay(PurchaseOrder purchaseOrder, Channel channel, QuoteRecord quoteRecord) {
        def payment = paymentRepository.findFirstByPurchaseOrderAndPaymentTypeOrderByIdDesc purchaseOrder, INITIALPAYMENT_1
        double amount = isProductionEnv() ? payment.amount : 0.01
        def outTradeNo = stringRedisTemplate.opsForValue().get answernOutTradeNoKey(purchaseOrder.orderNo)
        paymentRepository.save payment.with {
            it.outTradeNo = outTradeNo ?: it.outTradeNo
            it
        }
        def param = [
            (ORDER_NO)      : payment.outTradeNo,
            (PAY_AMT)       : round(amount * 100).toString(),
//            (PAY_TYPE_USUAL): [WE_CHAT_3, WE_CHAT_APP_39].contains(channel) || inWechat(request) ? WECHAT_WEB : ALIPAY_WEB,
            (REQUEST_CODE)  : getEnvPropertyNew([env: env, configService: configService, namespace: 'answern'],'pay_channel_code',null,[quoteRecord.area.id,getProvinceCode(quoteRecord.area.id)].toArray())
        ]
        def payKey = getEnvPropertyNew([env: env, configService: configService, namespace: 'answern'],'pay_key',null,[quoteRecord.area.id,getProvinceCode(quoteRecord.area.id)].toArray())
        param << [(CHECK_VALUE): getSign(payKey, param)]
        log.info '安心保险收银台，交易单号：{}，交易金额：{}，请求报文{}', purchaseOrder.orderNo, amount, param
        def axPayURL  = getEnvPropertyNew([env: env, configService: configService, namespace: 'answern'],'pay_url',null,[quoteRecord.area.id,getProvinceCode(quoteRecord.area.id)].toArray())
        [form: buildAppH5Form(new RESTClient(axPayURL).post([
            requestContentType: URLENC,
            contentType       : JSON,
            body              : param
        ], { resp, json ->
            log.info '安心保险收银台，响应报文{}', json
            if (1 != json.code) {
                throw new BusinessException(EXTERNAL_SERVICE_ERROR, '申请支付失败：' + json.msg)
            }
            json.data.payUrl
        }))]
    }

    private static buildAppH5Form(payUrl) {
        def html = {
            mkp.yieldUnescaped('<!DOCTYPE html>')
            html(lang: 'zh-CN') {
                head {
                    meta(charset: 'UTF-8')
                    title('')
                }
                body {
                    script(type: 'text/javascript', 'location.href="' + payUrl + '";')
                }
            }
        }
        new StreamingMarkupBuilder().bind(html).toString()
    }

    static answernOutTradeNoKey(orderNo) {
        'answern_out_trade_no' + orderNo
    }
}
