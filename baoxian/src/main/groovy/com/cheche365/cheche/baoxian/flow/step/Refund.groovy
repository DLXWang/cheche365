package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.baoxian.flow.step.v2.ABaoXianCommonStep
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * Created by wangxin on 2017/2/28.
 */
@Slf4j
@Component
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class Refund extends ABaoXianCommonStep {

    private static final _API_PATH_REFUND = '/reimbursement'

    def run(context) {

        def params = [
            taskId: context.taskId,
            prvId : context.provider.prvId
        ]

        def result = send context,prefix + _API_PATH_REFUND, params

        if ('0' == result.code) {
            log.info '退款申请成功'
            getContinueFSRV result.msg
        } else {
            log.error "退款申请失败：{}", result.msg
            getFatalErrorFSRV result.msg
        }

    }
}
