package com.cheche365.cheche.pinganuk.payinfo

import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.pinganuk.app.config.PinganUKMinTestConfig
import com.cheche365.cheche.test.parser.AParserServiceFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll


@Slf4j
@ContextConfiguration(classes = [PinganUKMinTestConfig])
class PinganPayInfoServiceFT extends AParserServiceFT {

    @Autowired
    IThirdPartyPaymentService service

    @Unroll
    '利用policyNos 测试获取支付方式接口'() {

        when: '构造发起支付信息applyPolicyNos并且发起支付申请'
            service.getPaymentChannels(['50137003900628797028'],null)

        then: '检查结果'
            true
    }

    @Unroll
    '利用policyNos 测试获取支付信息接口'() {

        when: '构造发起支付信息applyPolicyNos并且发起支付申请'
            service.getPaymentInfo(['50137003900628899783'],null)

        then: '检查结果'
            true
    }
}
