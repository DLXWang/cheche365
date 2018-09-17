package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonSlurper
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.*
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_2
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercialTimeCause
import static com.cheche365.cheche.parser.util.InsuranceUtils.afterGeneratedRenewalPackage
import static com.cheche365.cheche.pingan.util.BusinessUtils.generateRenewalPackage
import static groovy.json.JsonParserType.LAX
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * Created by wangxin on 2015/11/5.
 * 商业险报价基类
 */
abstract class ABizQuote implements IStep {

    private static final _API_PATH_BIZ_QUOTE = 'autox/do/api/biz-quote'

    @Override
    run(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_2

        RESTClient client = context.client
        def bodyContent = getRequestParams(context, this)
        log.info '报价请求参数：{}', bodyContent
        client.request(Method.POST) { req ->
            uri.path = _API_PATH_BIZ_QUOTE
            contentType = TEXT
            requestContentType = URLENC
            body = bodyContent
            response.success = { resp, json ->
                def result = new JsonSlurper().with {
                    type = LAX
                    def jsonStr = json.readLines().inject('') { text, item ->
                        text + item
                    }
                    parseText(jsonStr)
                }
                if ('C0000' == result.resultCode) {
                    //为续保套餐创建一个空的insurancePackage
                    if (_FLOW_TYPE_RENEWAL_CHANNEL == context.flowType && !context.insurancePackage) {
                        def converts = generateRequestParameters(context, this)
                        context.insurancePackage = generateRenewalPackage(result,converts)
                        afterGeneratedRenewalPackage(context)
                        log.info '上年续保套餐：{}', context.insurancePackage
                    }
                    //判断商业险是否有提前投保
                    if (result.circResult) {
                        def errorMsg = checkCommercial(context, result.circResult)
                        if (errorMsg) {
                            //TODO:实现时间相关的不可投保的QFS
                            log.info '获取商业险报价失败 {}', errorMsg
                            return getContinueWithIgnorableErrorFSRV(false, errorMsg)
                        }
                    }
                    //判断商业险是否正确投保
                    getCommercialQuoteResult(context, result)
                } else {
                    log.info '系统获取商业险报价失败 {}', result.resultMessage
                    getFatalErrorFSRV result.resultMessage
                }
            }
            response.failure = { resp, json ->
                log.error '发送请求失败，请重试！'
                result = new JsonSlurper().with {
                    type = LAX
                    parseText(json.readLines()[0])
                }
            }
        }
    }

    /**
     * 检查商业险是否能投保
     * @param context
     * @param circResult 平安的错误信息、出险记录等
     * @return
     */
    static checkCommercial(context, circResult) {
        // TODO：下面调countCanInsureDays的地方好像可以参考其他写法
        if (circResult.resultCode) {
            if (circResult.resultCode == 'C3003') {
                // 时间相关的商业险不可投保
                disableCommercialTimeCause context, 90, true, _DATE_FORMAT3
                circResult.failMsg
            } else if (circResult.resultCode == 'C3010') {
                //这里C3010码不知道是什么用途，只是先写上，要不要禁用商业险稍后再说
            }
        }
    }

    protected abstract getRequestParams(context, step)

    protected abstract getCommercialQuoteResult(context, result)

}
