package com.cheche365.cheche.externalpayment.template

import com.cheche365.cheche.baoxian.model.PayInfo
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.*
import com.cheche365.cheche.web.service.PaymentCallbackURLHandler
import groovy.xml.MarkupBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest

/**
 * Created by tongsong on 2017/2/21 0021.
 * 泛华保险模版
 */
@Component
@Order(value=1)
public class BaoXianPayService implements ThirdPartyPaymentTemplate {

    private final Logger logger = LoggerFactory.getLogger(BaoXianPayService.class)

    @Autowired
    private IThirdPartyHandlerService baoXianService

    @Autowired
    private InsuranceRepository insuranceRepository

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository

    @Autowired
    private QuoteRecordCacheService cacheService

    @Autowired
    private QuoteConfigService quoteConfigService

    @Autowired
    IPayUrlService<Void, PayInfo> baoXianPayUrlService

    @Autowired
    PaymentCallbackURLHandler paymentCallbackURLHandler

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Autowired(required = false)
    public HttpServletRequest request

    boolean acceptable(QuoteRecord quoteRecord) {
        return QuoteSource.Enum.PLANTFORM_BX_6 == quoteRecord.getType()
    }

    Object prePay(PurchaseOrder purchaseOrder, Channel channel, QuoteRecord quoteRecord) {
        return (OrderStatus.Enum.FINISHED_5 == purchaseOrder.getStatus() || OrderStatus.Enum.PAID_3 == purchaseOrder.getStatus()) ? payFinish(purchaseOrder) : baoXianGetFrom(purchaseOrder, quoteRecord)
    }

    def payFinish(PurchaseOrder purchaseOrder){
        String successUrl = WebConstants.getDomainURL()+ "/api/callback/" + purchaseOrder.getOrderSourceId()
        return orderPrePay(successUrl)
    }

    Object orderPrePay(def payUrl) {
        Object payRequest = this.buildPayRequest(payUrl)
        Map<String, Object> result = new HashMap<>()
        result.put("form", payRequest)
        return result
    }

    Object baoXianGetFrom(PurchaseOrder order, QuoteRecord quoteRecord) {

        def retUrl = paymentCallbackURLHandler.toFrontCallBackPage(order, request)

        def payInfo = new PayInfo(taskId: order.orderSourceId, area: order.area, insuranceCompany: quoteRecord.insuranceCompany, additionalParameters: [auto: order.auto, paymentReturnUrl: retUrl,deliveryAddress : order?.deliveryAddress,supportCompanies:[[id:quoteRecord.insuranceCompany.id]]])
        baoXianPayUrlService.pay(payInfo)
        logger.debug("获取泛华支付链接请求参数,taskId:{},payInfo:{}", order.orderSourceId, payInfo)

        if (!payInfo.additionalParameters.payUrl) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "支付链接请求失败")
        }

        orderPrePay(payInfo.additionalParameters.payUrl)
    }

    private Object buildPayRequest(def url) {
        def strXml = new StringWriter()
        def html = new MarkupBuilder(strXml)
        html.html(lang:"zh-CN"){
            head{
                meta(charset:"UTF-8")
                title("")
            }
            body{
                form (action: url,id:"fanhua",method: "post"){
                }
                script(type:"text/javascript","document.getElementById('fanhua').submit()") {
                }
            }
        }
        return strXml.toString()
    }

}
