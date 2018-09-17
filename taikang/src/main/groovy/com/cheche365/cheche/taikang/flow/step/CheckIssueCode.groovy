package com.cheche365.cheche.taikang.flow.step

import com.cheche365.cheche.common.flow.IStep
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.taikang.util.BusinessUtils.sendParamsAndReceive



/**
 * 校验保险平台短信验证码
 * Created by LIU GUO on 2018/6/7.
 */
@Slf4j
class CheckIssueCode implements IStep {

    private static final _FUNCTION = 'doGetIssueCodeImpl'

    @Override
    run(Object context) {
        log.info '开始执行验证码校验接口'
        def verificationCode = context.additionalParameters.supplementInfo?.verificationCode
        if (!verificationCode) {
            log.info '需身份验证码核实信息'
            return getSupplementInfoFSRV([mergeMaps(_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])])
        }

        def result = sendParamsAndReceive context, _FUNCTION, getRequestParams(context, verificationCode), log

        if ('200' == result.apply_content.reponseCode) {
            log.info '验证码校验成功'
            //保存验证码共前端支付使用
            getContinueFSRV result
        } else {
            log.error '验证码校验失败 resultMessage：{}', result.apply_content.messageBody
            getFatalErrorFSRV result.apply_content.messageBody ?: '验证码校验失败'
        }
    }

    private static getRequestParams(context, verificationCode) {
        [
            proposalNo: context.formId,
            issueCode : verificationCode
        ]
    }

}
