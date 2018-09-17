package com.cheche365.cheche.cpicuk.payinfo

import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.cpicuk.app.config.CpicUKMinTestConfig
import com.cheche365.cheche.test.parser.AParserServiceFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

@Slf4j
@ContextConfiguration(classes = [CpicUKMinTestConfig])
class CpicukPayInfoServiceFT extends AParserServiceFT {

    @Autowired
    IThirdPartyPaymentService service

    private final additionalParameters = [quoteRecord:[channel: [id: 8], insuranceCompany: [id: 25000], area: [id: 510100]]]

    @Unroll
    '利用policyNos 测试获取支付方式接口'() {

        when: '构造发起支付信息applyPolicyNos并且发起支付申请'

            service.getPaymentChannels([applyPolicyNo:'ABEJ920Y1418F121328L'],additionalParameters)

        then: '检查结果'
            true
    }

    @Unroll
    '利用policyNos 测试获取支付信息接口'() {

        when: '构造发起支付信息applyPolicyNos并且发起支付申请'
            service.getPaymentInfo([commercial:'',compulsory:'ACHD16LCTP18B005636H'],additionalParameters)

        then: '检查结果'
            true
    }

    @Unroll
    '利用policyNos 测试校验支付状态接口'() {

        when: '构造发起支付信息applyPolicyNos并且校验支付状态'

        def list = [
            [commercial:'ACHDA17Y1418F109083Y',compulsory:'',orderNo:'12312',paymentNo:'171881040747'],
            [commercial:'ACHDA17Y1418F108864W',compulsory:'',orderNo:'12313',paymentNo:'171880933816']
        ]
        service.checkPaymentState(list,additionalParameters)

        then: '检查结果'
        true
    }

    @Unroll
    '利用policyNos 测试作废支付单接口'() {

        when: '构造发起作废订单接口applyPolicyNos 并且发起作废申请'

        service.cancelPay([commercial:'ACHD16LY1418B005525Q',compulsory:'ACHD16LCTP18B005642M'],additionalParameters)

        then: '检查结果'
        true
    }
}
