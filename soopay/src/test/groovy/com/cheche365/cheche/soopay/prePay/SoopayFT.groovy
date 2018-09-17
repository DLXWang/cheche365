package com.cheche365.cheche.soopay.prePay

import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.soopay.app.config.SoopayConfig
import com.cheche365.cheche.soopay.payment.front.SoopayFrontTradeHandler
import com.cheche365.cheche.test.common.ASpockSpecification
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static com.cheche365.cheche.soopay.payment.SoopayProcessor.isCorrect
import static com.umpay.api.paygate.v40.Mer2Plat_v40.merNotifyResData

/**
 * Created by wangxin on 2017/6/20.
 */
@ContextConfiguration(classes = [SoopayConfig, CoreConfig])
@Slf4j
class SoopayFT extends ASpockSpecification {

    @Autowired
    private SoopayFrontTradeHandler soopayFrontTradeHandler

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PaymentRepository paymentRepository;


    def '测试soopay的支付请求接口'() {
        given:

        when:
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(Long.valueOf('125'));
            Payment payment1 = paymentRepository.findFirstByPurchaseOrder(purchaseOrder);

            def requestMap = [
                orderNo: purchaseOrder.orderNo,
                amount: '0.01',
                serialNumber: payment1.outTradeNo,
                dialogTitle: "车险服务订单"
            ]
            def result = soopayFrontTradeHandler.prePay(requestMap)

        then:
            result

    }


    def '验签'(){
        given :
            def respMap = [
                charset:'UTF-8',
                pay_date:'20170623',
                amount:'1',
                trade_state:'TRADE_SUCCESS',
                mer_id:'50024',
                settle_date:'20170623', sign:'AuXLBgxwcs2bWep8B9XGeubiPijAzjB9TNaZqjUQwkjHxoXNM5QS809bqWKgZas3C83Pnk1Zv1WnRbZEDdCtoeSTLb6gxVqg7IxsaJQjx52p5aeuOsmPcv0nI1voMtcbG9ZLxjUAOsWu5OuDVD1V3DOs+wFln/lR5eo/dUIpPd4=',
                amt_type:'RMB',
                version:'4.0',
                last_four_cardid:'4729',
                mer_date:'20170623',
                media_type:'MOBILE',
                gate_id:'CEB',
                service:'pay_result_notify',
                media_id:'18500215970',
                trade_no:'3706231404710895',
                error_code:'0000',
                pay_type:'DEBITCARD',
                order_id:'20170623370544',
                sign_type:'RSA',
                pay_seq:'D170623052126861585'
//                pay_seq:'D170623052126861584' //错误的数据
            ]

        when :
            def result = isCorrect(respMap)
        then:
            result
    }


    def '退款测试'(){
        given :
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(Long.valueOf('125'));
            Payment payment1 = paymentRepository.findFirstByPurchaseOrder(purchaseOrder);
                def reqMap = [
                    orderNo: purchaseOrder.orderNo,
                    amount: '0.02',
                    serialNumber: payment1.outTradeNo,
                    dialogTitle: "车险服务订单"
                ]
        when:
            def result = soopayFrontTradeHandler.refund(reqMap)
        then:

            result

    }


}
