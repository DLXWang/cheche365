package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopForceContinueFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_INSURED_ID_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV

/**
 * 获取并校验用户的图片验证码的基类
 */
@Component
@Slf4j
abstract class AVerifyCaptcha implements IStep {

    @Override
    run(context) {
        RESTClient client = context.client
        def captchaText = context.captchaText

        def result = verifyCaptcha captchaText, client, context

        //sFlag==1  1校验码不正确  2 证件号码不匹配    0正确
        //sFlag!=1  0校验码不正确  1或2证件号码不正确  success正确
        if (('1' != context.sFlag && 'success' == result.resultFlag) || ('1' == context.sFlag && '0' == result.resultFlag)) {
            context.historicalVehicleInfo = result
            getLoopBreakFSRV result
        } else if ('2' == result.resultFlag || ('1' != context.sFlag && '1' == result.resultFlag)) {
            log.error '证件号码不匹配：{}', result
            if (context.renewable) {
                def hints = [
                    _VALUABLE_HINT_INSURED_ID_TEMPLATE_QUOTING.with {
                        it.hints = [
                            '输入错误',
                            '不是上年被保人身份证'
                        ]
                        it
                    }
                ]
                getValuableHintsFSRV context, hints
            } else {
                // 历史客户，但是证件校验失败，我们可以尝试直接走转保流程（但是一定是出loop之后）
                getLoopForceContinueFSRV result, '非续保历史客户证件号码校验失败，但是强行继续后续流程'
            }
        } else if ('5' == result.resultFlag) {
            log.error '车主姓名不匹配：{}', result
            getValuableHintsFSRV context, [_VALUABLE_HINT_OWNER_TEMPLATE_QUOTING]
        } else {
            log.warn '验证码校验失败：{}', result
            getLoopContinueFSRV null, '验证码校验失败'
        }
    }

    /**
     * 由于groovy不知什么原因，在上面“result = verifyCaptcha captchaText, client, context”这句中，
     * verifyCaptcha总被认为是子类的方法而导致无法访问，所以不得不改为protected限定符
     * @param captchaText
     * @param client
     * @param context
     * @return
     */
    private verifyCaptcha(captchaText, RESTClient client, context) {
        context.captchaText = captchaText

        def args = getRequestParams(context)

        def result = client.get args, { resp, json ->
            json
        }

        log.info '校验验证码结果：{}', result

        result
    }

    abstract protected getRequestParams(context)

}
