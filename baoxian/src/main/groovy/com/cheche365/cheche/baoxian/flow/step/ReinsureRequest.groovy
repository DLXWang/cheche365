package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.baoxian.flow.step.v2.ABaoXianCommonStep
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * Created by wangxin on 2017/3/2.
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class ReinsureRequest  extends ABaoXianCommonStep {

    private static final _API_PATH_SUBMIT_INSURE = '/submitInsure'

    def run(context) {

        def params = [
            taskId : context.taskId,
            prvId : context.provider.prvId
        ]

        log.info "重新核保申请请求：${params}"

        def result = send context,prefix + _API_PATH_SUBMIT_INSURE, params

        log.info "重新核保申请响应：${result}"

        '0' == result.code ? getContinueFSRV(result.msg) : getFatalErrorFSRV(result.msg)

    }
}
