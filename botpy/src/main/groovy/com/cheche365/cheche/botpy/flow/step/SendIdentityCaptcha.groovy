package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.util.BusinessUtils.getIdCardDTO
import static com.cheche365.cheche.botpy.util.BusinessUtils.getInsuranceCompanyAccount
import static com.cheche365.cheche.botpy.util.BusinessUtils.getNotificationIdForPath
import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.botpy.util.BusinessUtils.setNotificationIdForPath
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getDoInsuranceFailedFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.Method.POST



/**
 * 发送身份证验证码
 */
@Component
@Slf4j
class SendIdentityCaptcha extends ASyncResult implements IStep {

    private static final _API_PATH_SEND_IDENTITY_CAPTCHA = '/ids/'

    @Override
    run(context) {
        def path = _API_PATH_SEND_IDENTITY_CAPTCHA + getInsuranceCompanyAccount(context).code
        if ('UW_SUCC' != context.proposal_status) {
            log.info '非核保成功状态，不能发送身份证短信验证码'

            return getDoInsuranceFailedFSRV([ proposal_id: context.proposal_id, type: this.class.name], '努力核保中，大约10分钟后查看结果')

        }

        def applicant = context.order.applicant
        def userMobile = context.extendedAttributes?.verificationMobile ?: applicant.mobile   // 用户手机
        def body = [
            machine_code: getInsuranceCompanyAccount(context).sam_code, // 身份证设备机器编码
            id          : getIdCardDTO(context),
            phone       : userMobile,
            proposal_id : context.proposal_id // 投保单号
        ]

        def result = sendParamsAndReceive context, path, body, POST, log

        if (result.error) {
            log.info '发送身份证验证码失败，notification_id：{}', result.error
            getFatalErrorFSRV result.error
        } else {
            context.waitIdentityCaptcha = true
            setNotificationIdForPath context, result, path
            log.info '发送身份证验证码成功， notification_id：{}', getNotificationIdForPath(context, path)
            syncResult(context, path, 'SendIDCode')
        }
    }

    @Override
    protected getApiPath(context, path) {
        path
    }

    @Override
    protected resolveResult(context, result, type) {
        def data = result.data
        if (result.is_done) {
            if (result.is_success) {
                log.info '发送内容为：{}，结果为：{}', data.comment, data.message
                getContinueFSRV data.message
            } else {
                getKnownReasonErrorFSRV data.comment ?: data.message
            }
        } else {
            getFatalErrorFSRV result
        }
    }

}
