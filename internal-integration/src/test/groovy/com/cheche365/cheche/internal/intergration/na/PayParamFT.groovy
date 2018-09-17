package com.cheche365.cheche.internal.intergration.na

import com.cheche365.cheche.internal.integration.na.api.PayParams
import spock.lang.Specification

/**
 * Created by zhengwei on 6/24/17.
 */
class PayParamFT extends Specification{
    def "非车支付参数读取测试"(){
        when:
        def responseBody = new PayParams().call('T1001')

        then:
        responseBody.orderNo
        responseBody.serialNumber

    }
}
