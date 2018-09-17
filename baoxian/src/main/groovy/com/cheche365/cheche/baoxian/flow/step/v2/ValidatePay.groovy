package com.cheche365.cheche.baoxian.flow.step.v2

import com.cheche365.cheche.common.Constants
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * @author wangxin
 */
@Component
@Slf4j
class ValidatePay extends ValidateTime {

    def getValidateTime(callBackResult){
        callBackResult.payValidTime
    }

    def getErrorDescriptionMsg(){
        '超过支付有效期，请重新报价'
    }
}
