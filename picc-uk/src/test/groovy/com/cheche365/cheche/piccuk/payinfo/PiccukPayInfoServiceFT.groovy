package com.cheche365.cheche.piccuk.payinfo

import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.piccuk.app.config.PiccUKTestConfig
import com.cheche365.cheche.test.parser.AParserServiceFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

@Slf4j
@ContextConfiguration(classes = [PiccUKTestConfig])
class PiccukPayInfoServiceFT extends AParserServiceFT {

    @Autowired
    IThirdPartyPaymentService service

    @Unroll
    '利用policyNos 测试获取支付方式接口'() {

        when: '构造发起支付信息applyPolicyNos并且发起支付申请'
        service.getPaymentChannels([applyPolicyNo:'TDAA201841010000481757'],null)

        then: '检查结果'
        true
    }

    @Unroll
    '利用policyNos 测试获取支付信息接口'() {

        when: '构造发起支付信息applyPolicyNos并且发起支付申请'
        service.getPaymentInfo([applyPolicyNo:'TDAA201841010000483495'],null)

        then: '检查结果'
        true
    }

    @Unroll
    '利用policyNos 测试校验支付状态接口'() {

        when: '构造发起支付信息applyPolicyNos并且校验支付状态'

        def list = [
            [commercial:'TDAA201841010000511178',compulsory:'',orderNo:'12313',paymentNo:'4101180717902135', serialNo:'4', payType:'9'],
            [commercial:'TDAA201841010000511178',compulsory:'',orderNo:'12313',paymentNo:'4101180717902135', serialNo:'4', payType:'9']
        ]
        service.checkPaymentState(list,null)

        then: '检查结果'
        true
    }
}
