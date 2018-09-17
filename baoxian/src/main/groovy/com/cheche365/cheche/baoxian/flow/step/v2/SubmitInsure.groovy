package com.cheche365.cheche.baoxian.flow.step.v2

import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.flow.Constants._STATUS_CODE_INSURE_SUCCESS
import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV



/**
 * @author wangxin
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class SubmitInsure extends ABaoXianCommonStep {

    private static final _API_PATH_SUBMIT_INSURE = '/cm/channelService/submitInsure'

    @Override
    run(context) {

        def params = [
            taskId: context.taskId,
            prvId : context.provider.prvId
        ]

        def result = send context, _API_PATH_SUBMIT_INSURE, params

        if (result) {
            if ('00' == result.respCode) {
                log.debug '{}:提交核保任务成功', context.token
                [_ROUTE_FLAG_DONE, _STATUS_CODE_INSURE_SUCCESS, null, '提交核保成功，但是人为抛出异常']
            } else if ('01' == result.respCode && result.errorMsg?.contains('仅支持提交一家')) {
                [_ROUTE_FLAG_DONE, _STATUS_CODE_INSURE_SUCCESS, null, '仅支持提交一家公司的核保']
            } else if ('01' == result.respCode && result.errorMsg?.contains('退回修改才能提交核保')) {
                log.debug '{}:提交核保任务失败:{}', context.token, result
                getKnownReasonErrorFSRV '您的订单需要人工处理，请联系客服'
            } else {
                log.debug '{}:提交核保任务失败:{}', context.token, result
                getKnownReasonErrorFSRV result.errorMsg ?: '提交核保失败'
            }
        } else {
            log.error '核保提交任务失败'
            getFatalErrorFSRV '核保提交任务失败'
        }
    }

}
