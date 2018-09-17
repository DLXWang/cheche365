package com.cheche365.cheche.externalpayment.handler

import com.cheche365.cheche.common.util.AreaUtils
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.core.service.ThirdPartyPaymentTemplate
import com.cheche365.cheche.core.util.FormUtil
import com.cheche365.cheche.externalpayment.util.ZaSignUtil
import com.cheche365.cheche.zhongan.service.ZhonganService
import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ZHONGAN_50000
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey
import static com.cheche365.cheche.externalpayment.constants.ZaCashierConstant.*

/**
 * Created by wenling on 2017/11/27.
 */
@Component
@Slf4j
class ZaPayHandler implements ThirdPartyPaymentTemplate  {

    private static Logger logger = LoggerFactory.getLogger(ZaPayHandler.class);
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    ZhonganService zhonganService;
    @Autowired
    QuoteRecordCacheService cacheService

    @Override
    boolean acceptable(QuoteRecord quoteRecord) {
        return ZHONGAN_50000 == quoteRecord.getInsuranceCompany()
    }

    @Override
    Object prePay(PurchaseOrder purchaseOrder, Channel channel, QuoteRecord quoteRecord) {

        Payment payment = savePaymentOutTradeNo(purchaseOrder)

        Map persistentState = cacheService.getPersistentState(persistQRParamHashKey(purchaseOrder.objId))?.persistentState
        logger.info("众安支付 订单号 : ${purchaseOrder.orderNo} , persistentState: ${persistentState}")

        Map<String, String> payParams = buildRequest(purchaseOrder,payment,persistentState);
        FormUtil.buildForm(CASHIER_URL, ZaSignUtil.buildRequestPara(payParams, APP_KEY),'POST')
    }


    def buildRequest(PurchaseOrder purchaseOrder,Payment payment,Map persistentState) {
        def payParams = [
            merchant_code : MERCHANT,
            body : '车险服务订单',
            src_type : "mobile",
            sign_type :  "MD5",
            notify_url :  NOTIFY_URL,
            request_charset :  CHARSET,
            out_trade_no :  payment.outTradeNo,
            return_url :  FRONT_URL,
            amt :  purchaseOrder.payableAmount as String,
            extra_info: JsonOutput.toJson([product_source : 1 ,insure_place_code : purchaseOrder?.area?.id]) //如果同时接入众安和平安，需要传扩展投保城市代码、所属产品等字段
        ]

        if(PINGAN_AREA_CITY_MAPPING.contains(purchaseOrder?.area?.id)  || PINGAN_AREA_MAPPING.contains(AreaUtils.getProvinceCode(purchaseOrder?.area?.id ))){//平安地区
            payParams.put("subject",  "平安车险")
        }else{
            payParams.put("subject",  "保骉车险")
        }

        if(ZA_REAL_NAME_AREA_MAPPING.contains(purchaseOrder?.area?.id)  ){ //深圳地区需要实名支付
            payParams.put("account_info",JsonOutput.toJson([user_name : purchaseOrder.applicantName ,certif_id : purchaseOrder.applicantIdNo]) );

            List supportedChannels = persistentState?.payChannelList
            if(supportedChannels){
                payParams.put('pay_channel', supportedChannels.payChannelCode.join('^'))
            }
            log.info("众安深圳地区支持的支付方式 : ${supportedChannels}, pay_channel参数值 : ${payParams.pay_channel}")

        }

        return payParams;
    }

    //众安需要实名支付的城市:深圳
    static ZA_REAL_NAME_AREA_MAPPING=[
        440300L
    ]

    //平安地区支持的省：贵州，甘肃，宁夏，新疆
    static PINGAN_AREA_MAPPING=[
        520000L,
        620000L,
        640000L,
        650000L
    ]

    //平安地区支持的市：大连
    static PINGAN_AREA_CITY_MAPPING=[
        210200L
    ]

    def savePaymentOutTradeNo(PurchaseOrder purchaseOrder){
        List<Payment> payments=paymentRepository.findCustomerPendingPayments(purchaseOrder)
        if(!payments){
            return
        }
        savePayment(payments[0], purchaseOrder.orderNo)
    }

    private Payment savePayment(Payment payment, String outTradeNo) {
        payment.setOutTradeNo(zhonganService.env.getProperty('zhongan.auth_third_party_code')+'_'+outTradeNo);
        return paymentRepository.save(payment);
    }
}
