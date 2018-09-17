package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by wangxin on 2017/2/28.
 */
@Slf4j
@Component
class InsureMsgHandler implements IStep {

    def run(context) {

        if (context.additionalParameters.persistentState.insureResult) { //有核保信息,直接返回核保状态，进行流程的匹配

            def taskState = context.additionalParameters.persistentState.insureResult.taskState
            if (taskState in _INSURE_SUCCESS_STATUS_LIST) {
                getContinueFSRV '6'
            } else if (taskState in _INSURE_FAILURE_STATUS_LIST) {
                getContinueFSRV '19'
            } else {
                log.info "InsureMsgHandler步骤，获取的核保状态taskState:${taskState}"
                getContinueFSRV taskState
            }
        } else { //无核保信息,影响上传，请求支付接口
            log.info "InsureMsgHandler步骤，没有获取到taskState,走默认的获取支付链接流程"
            getContinueFSRV '3'
        }
    }

    private static final _INSURE_SUCCESS_STATUS_LIST = [
        '6', '8', '11'
    ]

    private static final _INSURE_FAILURE_STATUS_LIST = [
        '19', '22'
    ]
}
