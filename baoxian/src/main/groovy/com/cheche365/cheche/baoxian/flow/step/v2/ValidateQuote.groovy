package com.cheche365.cheche.baoxian.flow.step.v2

import com.cheche365.cheche.common.Constants
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * @author wangxin
 */
@Component
@Slf4j
class ValidateQuote extends ValidateTime {

    @Override
    getValidateTime(callBackResult){
        callBackResult.quoteValidTime
    }

    @Override
    getErrorDescriptionMsg(){
        '超过报价有效期，请重新报价'
    }
}
