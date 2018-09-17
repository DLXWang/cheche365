package com.cheche365.cheche.baoxian.flow.step.v2m

import com.cheche365.cheche.baoxian.flow.step.ASubmitQuote
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV



/**
 * Created by wangxin on 2017/2/13.
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class SubmitQuote2M extends ASubmitQuote {

    private static final _API_PATH_SUBMIT_QUOTE = '/submitQuote'

    @Override
    protected getAPI() {
        _API_PATH_SUBMIT_QUOTE
    }

    @Override
    protected getParams(context) {
        [
            taskId: context.taskId
        ]
    }

    @Override
    protected getReturnFSRV(context,result){
        if ('0' == result.code || '00' == result.respCode) {
            log.info '提交报价成功'
            getContinueFSRV true
        } else if ('-1' == result.code || '01' == result.respCode) {
            def errorMsg = (result.msg ?: result.errorMsg).toString()
            log.info '提交报价失败，返回套餐建议，尝试调整套餐后再报价：{}', errorMsg
            getKnownReasonErrorFSRV errorMsg
        } else {
            log.error '提交报价失败：{}', result
            getFatalErrorFSRV result.msg
        }
    }
}
