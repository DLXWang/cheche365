package com.cheche365.cheche.idcredit.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.service.IContext
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.idcredit.flow.Constants._STATUS_CODE_IDCREDIT_API_UPPER_LIMIT_ERROR
import static com.cheche365.cheche.idcredit.util.BusinessUtils.saveApplicationLog
import static groovyx.net.http.ContentType.JSON
import static java.util.concurrent.TimeUnit.SECONDS

/**
 * 获取Token
 * 有API调用次数的限制
 */
@Component
@Slf4j
class GetToken implements IStep {

    private static final _API_PATH_GET_TOKEN = '/common/get_token'

    @Override
    run(context) {

        /**
         * step 1：先从redis获取之前的token（竞态）：
         * 场景一：
         * 某服务实例流程获取到token，但是token即刻过期消失，
         * 那么其他服务实例流程执行到此处时token就没了，
         * 在短时间内，会同时存在两个token，可能造成错误
         */
        IContext globalContext = context.globalContext
        def oldToken = globalContext.get 'token'
        if (oldToken) {
            context.token = oldToken
            return getLoopBreakFSRV(oldToken)
        }

        /**
         * step 2：如果token不存在则获取一个新的（竞态）：
         * 场景一：
         * 多个服务实例流程同时执行到这里，且gettingNewToken都不存在
         */
        if (!context.getTokenCurrentLimiter.allowed) {
            log.error '绿湾API调用次数已经达到当天上限，当天不再尝试'
            return [_ROUTE_FLAG_DONE, _STATUS_CODE_IDCREDIT_API_UPPER_LIMIT_ERROR, null, '绿湾GetToken API调用次数达到当天上限']
        }

        def gettingNewToken = globalContext.exists 'gettingNewToken'
        if (!gettingNewToken) {
            def successful = globalContext.bindIfAbsentWithTTL 'gettingNewToken', true, 5L, SECONDS
            if (!successful) {
                Thread.sleep 2000L
                return getLoopContinueFSRV(null, '其他流程刚刚开始获取新的Token，稍后重试')
            }
        } else {
            Thread.sleep 2000L
            return getLoopContinueFSRV(null, '其他流程正在获取新的Token，稍后重试')
        }

        // 开始获取Token，应该只有一个服务实例流程可以进入这个位置
        def appId = getEnvProperty context, 'idcredit.auth_app_id'
        def appSecret = getEnvProperty context, 'idcredit.auth_app_secret'

        RESTClient client = context.client

        def args = [
            contentType : JSON,
            path        : _API_PATH_GET_TOKEN,
            query       : [
                app_id      : appId,
                app_secret  : appSecret
            ]
        ]

        log.info '以{}、{}为参数获取Token', appId, appSecret
        saveApplicationLog context, args as String, this.class.simpleName

        def result = client.get args, { resp, json ->
            json
        }

        saveApplicationLog context, result as String, this.class.simpleName

        // step 3：将新的token存入redis（带ttl）
        if (!result.error) {
            log.debug '成功获取Token：{}', result
            context.token = result.data.access_token
            globalContext.bindIfAbsentWithTTL 'token', context.token, result.data.expires - 600 /* 7200 - 600 */, SECONDS
            context.getTokenCurrentLimiter.increment 1 // TODO：是否应该参考FindVehicleInfo，无论怎样都递增
            globalContext.unbind 'gettingNewToken'
            getLoopBreakFSRV result
        } else {
            log.error '获取Token失败：{}', result
            if (40007 == result.error) {
                log.error '绿湾API调用次数达到当天上限'
            }
            getFatalErrorFSRV '获取Token失败'
        }
    }

}
