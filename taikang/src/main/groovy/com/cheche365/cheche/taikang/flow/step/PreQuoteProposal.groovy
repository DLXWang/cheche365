package com.cheche365.cheche.taikang.flow.step

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.taikang.util.BusinessUtils.preProcessCaptcha
import static com.cheche365.cheche.taikang.util.BusinessUtils.getFirstCheck
import groovy.util.logging.Slf4j
import static com.cheche365.cheche.taikang.util.BusinessUtils.sendParamsAndReceive



/**
 * 预核保处理验证码
 * Created by LIU GUO on 2018/7/13.
 */
@Slf4j
class PreQuoteProposal extends QuoteToProposal {

    @Override
    run(Object context) {

        if (context.additionalParameters.supplementInfo?.commercialCaptchaImage || context.additionalParameters.supplementInfo?.compulsoryCaptchaImage) {
            log.debug 'TK预核保获核保取前台商业险验证码：{},交强险验证码：{}', context.additionalParameters.supplementInfo?.commercialCaptchaImage, context.additionalParameters.supplementInfo?.compulsoryCaptchaImage

            def result = sendParamsAndReceive context, _FUNCTION, generateRequestParameters(context, this), log
            //预处理存在图片验证码
            if (getFirstCheck(result)) {
                preProcessCaptcha context, result
                getContinueFSRV '验证码处理'
            } else {
                if ('200' == result.apply_content.reponseCode) {
                    dealResult context, result.apply_content
                    getContinueFSRV 'TK核保完成'
                } else {
                    log.error '核保失败 resultMessage：{}', result.apply_content.messageBody
                    getFatalErrorFSRV result.apply_content.messageBody ?: '核保失败，请联系人工处理'
                }
            }

        } else {
            getContinueFSRV '无图片验证码输入'
        }

    }

}
