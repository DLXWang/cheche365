package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.util.BusinessUtils.getInsuranceCompanyAccount
import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.Method.GET
import static java.util.concurrent.TimeUnit.DAYS
import static com.cheche365.cheche.botpy.flow.Constants._ENGAGES_TTL

/**
 * 检索保险公司账号特别约定
 */
@Component
@Slf4j
class CheckSpecialEngage implements IStep {

    private static final _API_PATH_FIND_SPECIAL_ENGAGE = '/accounts/'

    @Override
    run(context) {
        def globalContext = context.globalContext
        def accountId = getInsuranceCompanyAccount(context).account_id
        def engagesKey = "engages-${accountId}".toString()
        if(globalContext.exists(engagesKey)) {
            context.engages = new JsonSlurper().parseText(globalContext.get(engagesKey))
            log.info '保险公司账号特别约定使用缓存，account_id：{}', accountId
            return getContinueFSRV('检索保险公司账号特别约定成功')
        }
        def path = _API_PATH_FIND_SPECIAL_ENGAGE + accountId + '/ic-engages'
        def result = sendParamsAndReceive context, path, [:], GET,log

        if (result.error) {
            log.error '检索保险公司账号特别约定失败，原因{}', result.error
            getFatalErrorFSRV result.error.contains("登录失败") ?  '登录失败，请尝试重新报价': result.error
        } else {
            context.engages = result?.engages
            //配置了特别约定时，将配置的特别约定补充进来
            if (context.icEngages) {
                def icEngages = context.icEngages.split(',') as List
                def supplement = sendParamsAndReceive context, path, [codes: icEngages], GET, log
                if (!supplement.error) {
                    if (context.engages) {
                        supplement?.engages?.each {
                            if (!(it.code in context.engages['code'])) {
                                context.engages += it
                            }
                        }
                    } else {
                        context.engages = supplement?.engages
                    }
                }
            }
            globalContext.bindWithTTL(engagesKey, new JsonBuilder(context.engages).toString(), _ENGAGES_TTL, DAYS)
            getContinueFSRV '检索保险公司账号特别约定成功'
        }

    }

}
