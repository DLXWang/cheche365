package com.cheche365.cheche.baoxian.flow.step.v2

import com.cheche365.cheche.baoxian.flow.step.ASubmitQuote
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.util.BusinessUtils._ADVICE_REGULATOR_MAPPINGS
import static com.cheche365.cheche.baoxian.util.BusinessUtils._GET_EFFECTIVE_ADVICES
import static com.cheche365.cheche.baoxian.util.BusinessUtils._GET_EFFECTIVE_ADVICES_SUBMIT_QUOTE
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.InsuranceUtils.adjustInsurancePackageFSRV

/**
 * Created by wangxin on 2017/2/13.
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
/**
 * Created by wangxin on 2018/1/24.
 */
class ReSubmitQuote extends ASubmitQuote {

    private static final _API_PATH_SUBMIT_QUOTE = '/submitQuote'

    @Override
    protected getAPI() {
        _API_PATH_SUBMIT_QUOTE
    }

    @Override
    protected getParams(context) {
        [
            taskId: context.taskId,
            prvId : context.provider.prvId
        ]
    }

    @Override
    protected getReturnFSRV(context, result) {
        if ('00' == result.respCode) {
            log.info '重新提交报价成功'
            getContinueFSRV true
        } else if ('01' == result.respCode) {
            def errorMsg = (result.msg ?: result.errorMsg).toString()
            log.info '提交报价失败，返回套餐建议，尝试调整套餐后再报价：{}', errorMsg
            adjustInsurancePackageFSRV _ADVICE_REGULATOR_MAPPINGS, _GET_EFFECTIVE_ADVICES_SUBMIT_QUOTE, errorMsg, context
        } else {
            log.error '重新提交报价失败：{}', result
            getFatalErrorFSRV result.msg
        }
    }
}
