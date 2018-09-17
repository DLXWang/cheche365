package com.cheche365.cheche.cpicuk.client

import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.cpicuk.client.app.config.CpicUKClientTestConfig
import com.cheche365.cheche.test.parser.AParserServiceFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll



@Slf4j
@ContextConfiguration(classes = [CpicUKClientTestConfig])
class Cpicukpayat110000beijingFT extends AParserServiceFT {

    @Autowired(required = false)
    IThirdPartyPaymentService service

    private final additionalParameters = [quoteRecord:[channel: [id: 8], insuranceCompany: [id: 25000], area: [id: 110000]]]

    @Unroll
    '利用policyNos 测试获取支付方式接口'() {

        when: '构造发起支付信息applyPolicyNos并且发起支付申请'

            service.getPaymentChannels([commercial:'ABEJ920Y1418F235653J'],additionalParameters)

        then: '检查结果'
            true
    }

    @Unroll
    '利用policyNos 测试获取支付信息接口'() {

        when: '构造发起支付信息applyPolicyNos并且发起支付申请'
            service.getPaymentInfo([commercial:'ABEJ920Y1418B052930I',compulsory:''],additionalParameters)

        then: '检查结果'
            true
    }

    @Unroll
    '利用policyNos 测试校验支付状态接口'() {

        when: '构造发起支付信息applyPolicyNos并且校验支付状态'

        def list = [
            [commercial:'ABEJ920Y1418F235653J',compulsory:'',orderNo:'12312',paymentNo:'171885619399']
        ]
        service.checkPaymentState(list,additionalParameters)

        then: '检查结果'
        true
    }

    @Unroll
    '利用policyNos 测试作废支付单接口'() {

        when: '构造发起作废订单接口applyPolicyNos 并且发起作废申请'

        service.cancelPay([commercial:'ABEJ920Y1418B052930I',compulsory:''],additionalParameters)

        then: '检查结果'
        true
    }
}
