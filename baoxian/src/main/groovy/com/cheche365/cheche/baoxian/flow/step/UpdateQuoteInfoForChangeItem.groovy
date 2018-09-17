package com.cheche365.cheche.baoxian.flow.step

import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.flow.Handlers._KIND_CODE_CONVERTERS_CONFIG
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV



@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class UpdateQuoteInfoForChangeItem extends AUpdateQuoteInfo {

    @Override
    protected getParams(context) {

        [
            taskId    : context.taskId,
            prvId     : context.provider.prvId,
            insureInfo: toFormedParams(context, _KIND_CODE_CONVERTERS_CONFIG),
        ]
    }

    @Override
    protected getResultFSRV(result,context) {
        if ('0' == result.code || '00' == result.respCode) {
            log.info '提交更改险种信息成功'
            getContinueFSRV result
        } else {
            log.error '信息修改失败：{}', result.msg
            getFatalErrorFSRV result.msg
        }
    }
}
