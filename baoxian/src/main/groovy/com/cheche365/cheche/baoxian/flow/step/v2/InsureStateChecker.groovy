package com.cheche365.cheche.baoxian.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * @author wangxin
 */
@Slf4j
class InsureStateChecker implements IStep {

    //总共有3种状态，去核保（14）、核保成功（20）、核保退回修改（19），核保失败直接由service-broker将回调结果传给web端，不在走核保流程
    @Override
    run(context) {

        def result = context.additionalParameters.persistentState.CallbackResult

        log.info '核保流程开始，核保回调信息为：{}', result

        getContinueFSRV(resultCode.taskState)

    }
}
