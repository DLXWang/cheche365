package com.cheche365.cheche.baoxian.flow.step.v2

import com.cheche365.cheche.common.Constants
import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV

/**
 * @author wangxin
 */
@Component
@Slf4j
abstract class ValidateTime implements IStep {

    abstract def getValidateTime(callBackResult)

    abstract def getErrorDescriptionMsg()


    @Override
    run(context) {

        def quoteResult = context.additionalParams

        def validateTime = getValidateTime quoteResult

        if(Constants.get_DATE_FORMAT5().parse(validateTime) < new Date()){
            //超过支付有效期，请重新报价
            getKnownReasonErrorFSRV(getErrorDescriptionMsg())
        }else{
            getContinueFSRV quoteResult
        }
    }
}
