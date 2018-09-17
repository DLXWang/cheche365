package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getResponseResult
import static com.cheche365.cheche.parser.util.BusinessUtils.checkCompulsoryPackageOptionEnabled
import static com.cheche365.cheche.pingan.flow.Constants._QUOTE_FAIL_MESSAGE
import static com.cheche365.cheche.pingan.util.BusinessUtils.textToJson
import static groovyx.net.http.ContentType.BINARY



/**
 * 商业险规则校验
 * Created by wangxin on 2015/11/5.
 */
@Component
@Slf4j
class QuoteResultInspection implements IStep {

    private static final _API_PATH_TO_SUPPLEMENT_INFO = 'autox/do/api/to-supplement-info'

    private static final _RH_S = { result ->
        log.info '获取报价补充信息失败: {}', result.resultMessage
        false
    }

    private static final _RH_C = { result ->
        log.info '获取报价补充信息: {}', result?.orderInfo?.bizResult ?: result.bizRules?.failRules ?: '无商业险报价'
        true
    }

    private static final _RH_F = { result ->
        log.info '不明原因异常，官网信息：: {}', result.resultMessage
        false
    }

    private static final _RH_DEFAULT = { result ->
        log.info '获取报价补充信息: {}', result.bizResult
        false
    }

    private static final _RH_MAPPINGS = [
        S      : _RH_S,
        C      : _RH_C,
        F      : _RH_F,
        default: _RH_DEFAULT
    ]

    @Override
    run(context) {

        if (!isSupplementInfoRequestSuccess(context)) {
            return getFatalErrorFSRV (_QUOTE_FAIL_MESSAGE)
        }

        def (needToChangeCommercial, needToStopFlow, flowStopMsg) = getResponseResult(context.failRules, context, this)

        if (needToStopFlow) {
            return getFatalErrorFSRV (flowStopMsg.message)
        } else {
            if (needToChangeCommercial) {
                //loop循环继续,第三个参数也为true，出循环为规则校验未成功
                return getLoopContinueFSRV (null, '商业险报价规则校验失败')
            } else {
                //loop循环正常中断，第三个参数一定为true，因为跳出循环后就可计算商业险报价
                return getLoopBreakFSRV (true)
            }
        }
    }



    //验证M站是否正确的返回了核保的信息
    private isSupplementInfoRequestSuccess(context) {
        RESTClient client = context.client
        def args = [
            contentType: BINARY,
            path       : _API_PATH_TO_SUPPLEMENT_INFO,
            query      : [
                flowId                  : context.flowId,
                __xrc                   : context.__xrc,
                'forceInfo.isApplyForce': checkCompulsoryPackageOptionEnabled(context)
            ]
        ]
        def result = client.get args, { resp, stream ->
            def text = new StringWriter().with { writer ->
                writer << new InputStreamReader(stream)
            }.toString()
            textToJson(text)
        }
        context.failRules = result?.bizRules?.failRules
        context.bizResult = result?.orderInfo?.bizResult
        _RH_MAPPINGS[result.resultCode ? result.resultCode[0] : 'default'].call(result)
    }
}
