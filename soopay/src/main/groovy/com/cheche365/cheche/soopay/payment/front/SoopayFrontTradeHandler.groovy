package com.cheche365.cheche.soopay.payment.front

import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.service.PaymentSerialNumberGenerator
import com.cheche365.cheche.core.service.spi.IPayService
import com.cheche365.cheche.core.util.URLUtils
import com.cheche365.cheche.externalapi.api.soopay.SoopayPrepayAPI
import com.cheche365.cheche.soopay.payment.ISoopayHandler
import com.cheche365.cheche.soopay.payment.SoopayProcessor
import groovy.util.logging.Slf4j
import groovy.xml.MarkupBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.cheche365.cheche.soopay.SoopayConstant.getSoopayCallbackBackUrl

/**
 * Created by mjg on 2017/6/19.
 */
@Component
@Slf4j
class SoopayFrontTradeHandler implements ISoopayHandler, IPayService {

    @Autowired
    PaymentSerialNumberGenerator serialNumberGenerator;

    @Autowired
    private SoopayProcessor soopayProcessor;

    @Autowired
    private SoopayPrepayAPI prepayAPI


    @Override
    prePay(Map<String, Object> params) {
        def request = [
            service   : SOOPAY_TXN_TYPE_01,
            version   : SOOPAY_VERSION,
            charset   : SOOPAY_ENCODING,
            sign_type : SOOPAY_SIGN_METHOD,
            mer_id    : SOOPAY_MERCHANT_ID,
            mer_date  : dateFormat.format(new Date()),
            amt_type  : SOOPAY_AMT_TYPE,
            order_id  : params.serialNumber,
            amount    : (((params.amount as Double) * 100) as Integer).toString(),
            ret_url   : params.redirectDetailUrl,
            res_format: SOOPAY_RES_FORMAT,
            notify_url: getSoopayCallbackBackUrl()
        ]

        def result = prepayAPI.call(request)
        log.debug '预支付请求结果{}',result
        buildForm result
    }


    static String buildForm(String url){

        def writer = new StringWriter()
        def markup = new MarkupBuilder(writer)
        markup.html{
            form (id:'sooPayForm', action: URLUtils.baseUrl(url), method: 'GET'){
                URLUtils.splitQuery(url.toURL().query).each {
                    input(type: 'hidden', name: it.key, id: it.key, value: it.value)
                }


            }
            script "document.getElementById('sooPayForm').submit()"
        }
        writer.toString()
    }

    //退款
    def refund(Map<String, Object> params) {
       return null
    }

    //同步回调
    def syncCallback(Map<String, Object> params) {}
    //异步回调
    def asyncCallback(Map<String, Object> params) {}
    //支持的支付方式
    boolean support(PaymentChannel pc) {
        PaymentChannel.Enum.SOO_PAY_17 == pc
    }

}
