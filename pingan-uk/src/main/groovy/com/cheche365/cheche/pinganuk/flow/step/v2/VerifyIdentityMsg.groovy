package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static groovyx.net.http.ContentType.JSON

/**
 * 验证采集身份验证码
 * @author: lp
 * @date: 2018/4/23 21:45
 */
@Component
@Slf4j
class VerifyIdentityMsg implements IStep {

    private static final _API_PATH_VERIFY_IDENTITY_MSG = '/icore_pnbs/do/app/identityCollection/applyForPreconfirm'

    @Override
    run(context) {
        def applyPolicyNo = context.applyPolicyList?.applyPolicyNo ?: context.applyPolicyNo
        def identityCaptcha = context.additionalParameters.supplementInfo?.verificationCode
        if (!identityCaptcha) {
            log.info '需身份验证码核实信息'
            return getSupplementInfoFSRV([mergeMaps(_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])])
        }

        RESTClient client = context.client
        client.uri = getEnvProperty(context, 'pinganuk.pnbs_host')


        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_VERIFY_IDENTITY_MSG,
            body              : [
                applyPolicyNos: applyPolicyNo,
                issueCode     : identityCaptcha
            ]
        ]

        client.post args, { resp, json ->
            json
        }

        log.info '校验采集身份验证码成功， applyPolicyNo：{}', applyPolicyNo
        getContinueFSRV'校验采集身份验证码成功'
    }

}
