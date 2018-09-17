package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.botpy.util.BusinessUtils.getNotificationIdForPath
import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.botpy.util.BusinessUtils.setNotificationIdForPath
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getDoInsuranceFailedFSRV
import static groovyx.net.http.Method.POST



/**
 * 提交同步
 */
@Slf4j
class SubmitSync extends ASyncResult implements IStep {


    private static final _API_PATH_SUBMIT_SYNC = '/synchronizations'

    @Override
    protected getApiPath(context, path) {
        _API_PATH_SUBMIT_SYNC
    }

    @Override
    def resolveResult(context, result, type) {

        def data = result?.data
        //目前只有上传照片才会调用这个接口，凡是上传照片的都会人工核保，所有异步回来的result.is_success都会为false
        if (result.is_done) {
            log.info '跟踪单号为：{}，当前投保单状态为：{}，结果为：{}', data.tracking_no, data.proposal_status, data.message
            getDoInsuranceFailedFSRV([
                notification_id: getNotificationIdForPath(context, _API_PATH_SUBMIT_SYNC),
                proposal_id: context.proposal_id,
                type: type
            ], data.comment ?: data.message)
        } else {
            getFatalErrorFSRV result
        }
    }

    @Override
    Object run(context) {

        def params = [
            proposal_id: context.proposal_id
        ]
        def result = sendParamsAndReceive context, _API_PATH_SUBMIT_SYNC, params, POST, log
        if (result.error) {
            log.info '金斗云提交同步失败， {}', result.error
            getFatalErrorFSRV result.error
        } else {
            setNotificationIdForPath context, result, _API_PATH_SUBMIT_SYNC
            log.info '金斗云提交同步成功， 单号为：{}', getNotificationIdForPath(context, _API_PATH_SUBMIT_SYNC)
        }
        syncResult(context, _API_PATH_SUBMIT_SYNC, 'submitSync')
    }
}
