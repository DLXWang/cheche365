package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 身份采集后校验手机验证码
 */
@Component
@Slf4j
class CheckCodeIsRight implements IStep {

    private static final _API_CHECK_CODE_IS_RIGHT = '/prpall/idcard/checkCodeIsRight.do'

    @Override
    Object run(Object context) {
        RESTClient client = context.client

        def verificationCode = context.additionalParameters.supplementInfo?.verificationCode
        if (!verificationCode) {
            log.info '需身份验证码核实信息'

            return getSupplementInfoFSRV([mergeMaps(_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])])
        }
        def (bsProposalNo, bzProposalNo) = context.proposalNos.first()
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_CHECK_CODE_IS_RIGHT,
            body              : [
                proposalNoBI: bsProposalNo,
                proposalNoCI: bzProposalNo,
                checkCode   : verificationCode
            ]
        ]

        log.debug '身份验证码核实信息args ： {}', args.body
        def result = client.post args, { resp, json ->
            json
        }


        if (0 == result.totalRecords && result.msg.contains('验证通过')) {
            log.debug '身份采集验证码验证成功 ： {}', result.msg
            getContinueFSRV result
        } else {
            log.debug '身份采集验证码验证失败：{}', result.msg
            context.proposalStatus = '身份采集验证码发送失败'
            getKnownReasonErrorFSRV '身份采集验证码验证失败'
        }
    }
}
