package com.cheche365.cheche.baoxian.flow.step

import groovy.json.JsonSlurper
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.flow.Constants._COMPANY_I2O_MAPPINGS
import static com.cheche365.cheche.baoxian.util.BusinessUtils._ADVICE_REGULATOR_MAPPINGS
import static com.cheche365.cheche.baoxian.util.BusinessUtils._GET_EFFECTIVE_ADVICES_SUBMIT_QUOTE
import static com.cheche365.cheche.baoxian.util.BusinessUtils.saveInfo
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.InsuranceUtils.adjustInsurancePackageFSRV

/**
 * Created by wangxin on 2017/2/13.
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class SubmitQuote extends ASubmitQuote {

    private static final _API_PATH_SUBMIT_QUOTE = '/submitQuote'

    @Override
    protected getAPI() {
        _API_PATH_SUBMIT_QUOTE
    }

    @Override
    protected getParams(context) {
        [
            taskId: context.taskId
        ]
    }

    @Override
    protected getReturnFSRV(context, result) {
        if ('0' == result.code || '00' == result.respCode) {
            log.info '提交报价成功'
            saveInfo context, log
            getContinueFSRV true
        } else if ('-1' == result.code || '01' == result.respCode) {
            def errorMsg = result.errorMsg
            log.info '提交报价失败，返回套餐建议，尝试调整套餐后再报价：{}', errorMsg
            try {
                def globalContext = context.globalContext
                def jsonErrorData = new JsonSlurper().parseText(errorMsg)
                context.jsonErrorData = jsonErrorData
                //找出不能提交报价的公司
                def includeFailedProviders = context.providers.findAll { provider ->
                    jsonErrorData[provider.prvId]
                }
                //剔除不能报价的公司
                context.providers = context.providers - includeFailedProviders

                //如果没有不能提交报价的公司，则错误不能被处理，流程终止
                if(includeFailedProviders) {
                    log.info '剔除不能投保的保险公司：{}', includeFailedProviders
                    getContinueFSRV false
                } else {
                    getFatalErrorFSRV "提交报价失败，保险公司：${context.insuranceCompany.name}，result.code：${result.code}，result.respCode：${result.respCode}，errorMsg：$errorMsg"
                }
            } catch (ex) {
                log.info '提交报价接口的错误消息：{}，并根据错误消息进行适当调整', result.errorMsg
                adjustInsurancePackageFSRV _ADVICE_REGULATOR_MAPPINGS, _GET_EFFECTIVE_ADVICES_SUBMIT_QUOTE, errorMsg, context
            }
        } else {
            log.error '提交报价失败：{}', result
            getFatalErrorFSRV result.msg
        }
    }
}
