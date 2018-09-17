package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * Created by chukh on 2018/5/10.
 * 向手机发送短信验证码  北京特供
 * 接收验证码的手机号码为 投保人的手机号，所以需要填写真实的手机号
 *
 */
@Component
@Slf4j
class SmsConfirm implements IStep {

    private static final _URL_SMS_CONFIRM = '/ecar/payment/smsconfirm'

    @Override
    Object run(Object context) {
        RESTClient client = context.client
        def args = [
            path              : _URL_SMS_CONFIRM,
            requestContentType: JSON,
            contentType       : JSON,
            body              : [
                meta  : [:],
                redata: [
                    quotationNo: context.quotationNo
                ]
            ]
        ]
        def result = client.post args, { resp, json ->
            json
        }
        log.debug '发送验证码：{}', result
        def code = result.message.code
        def message = result.message.message
        if (code == 'success') {
            log.debug '电子投保单短信确认成功!,请注意查看手机验证码'
            //直接校对身份验证码 的流程分支
            context.waitIdentityCaptcha = true
            getContinueFSRV null
        } else {
            log.error '发送短信验证码失败，太平洋返回信息为:{}', message
            getKnownReasonErrorFSRV message
        }

    }
}
