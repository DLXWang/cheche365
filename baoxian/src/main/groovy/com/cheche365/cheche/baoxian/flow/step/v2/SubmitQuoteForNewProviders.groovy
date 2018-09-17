package com.cheche365.cheche.baoxian.flow.step.v2

import com.cheche365.cheche.baoxian.flow.step.ASubmitQuote
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.flow.Constants._COMPANY_I2O_MAPPINGS
import static com.cheche365.cheche.baoxian.util.BusinessUtils.saveInfo
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV

/**
 * Created by wangxin on 2017/2/13.
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class SubmitQuoteForNewProviders extends ASubmitQuote {

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
        //查找当前保险公司的错误消息
        def errorMsg = context.jsonErrorData[context.provider.prvId]
        if ('0' == result.code || '00' == result.respCode) {
            //缓存taskID以及其他的信息
            saveInfo context, log
            //当前保险公司如果有错误消息，则流程终止
            if(errorMsg) {
                log.info '{} 提交报价成功,但是 {} 提交报价失败，原因是：{}', context.providers.prvName, context.insuranceCompany.name, errorMsg
                getKnownReasonErrorFSRV errorMsg
            } else {
                getContinueFSRV '重新提交报价成功'
            }
        } else if ('-1' == result.code || '01' == result.respCode) {
            log.info '剔除不可投保保险公司后提交报价依然失败，失败原因{}', result.errorMsg
            getFatalErrorFSRV(errorMsg ?: result.errorMsg)
        } else {
            log.error '提交报价失败：{}', result
            getFatalErrorFSRV result.errorMsg
        }
    }
}
