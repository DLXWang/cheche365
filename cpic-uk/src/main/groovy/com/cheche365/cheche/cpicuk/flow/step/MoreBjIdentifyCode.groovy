package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * Created by chukh on 2018/5/10.
 * 提交承保验证码北京特有
 *
 * 作用：提交了验证码后，在payment中更新2个字段
 * 'bjIdentifyCode':'1234' 手机接收到的短信验证码，需写真实
 * 'bjIdentifyCodeFlag':'1',
 *
 * 在支付保单之前会进行这2个字段的判断，如果没有，则会要求上传承保验证码  返回这个步骤
 */
@Component
@Slf4j
class MoreBjIdentifyCode implements IStep {

    private static final _API_PATH_UPDATE_BJ_IDENTIFY_CODE = '/ecar/update/moreBjIdentifyCode'

    @Override
    Object run(Object context) {
        def verificationCode = context.additionalParameters.supplementInfo?.verificationCode
        if (!verificationCode) {
            log.debug '请确保首先在手机上点击确认，然后提交验证码核实信息'
            return getSupplementInfoFSRV([mergeMaps(_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order?.orderNo]])])
        }

        RESTClient client = context.client
        //手机验证码
        def args = [
            path              : _API_PATH_UPDATE_BJ_IDENTIFY_CODE,
            requestContentType: JSON,
            contentType       : JSON,
            body              : [
                meta  : [:],
                redata: [
                    bjIdentifyCodeVos: [
                        [
                            quotationNo   : context.quotationNo,
                            bjIdentifyCode: verificationCode
                        ]
                    ]
                ]
            ]
        ]
        log.debug '请求体：\n{}', args.body
        def result = client.post args, { resp, json -> json }
        log.debug '上传承保验证码：{}', result
        //这里不做验证码正确性的校验

        if (result.message.code == 'success') {
            log.debug '调用验证码操作完成'
            getContinueFSRV null
        } else {
            log.error '上传承保验证码失败：{}', result.message.message
            getKnownReasonErrorFSRV result.message.message
        }
    }
}
