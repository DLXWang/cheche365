package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 获取代理人信息
 */
@Component
@Slf4j
class QueryFastLoginInfo implements IStep {

    private static final _API_PATH_QUERY_FAST_LOGIN_INFO = '/ecar/auth/queryFastLoginInfo'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_QUERY_FAST_LOGIN_INFO,
            body              : [
                meta  : [:],
                redata: [:]
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('success' == result?.message?.code) {
            def userAuthVos = result.result.userAuthVos
            log.debug '成功获取代理人信息：{}', userAuthVos
            if (userAuthVos instanceof List && userAuthVos.size() == 1) {
                context.accessToken = userAuthVos.accessToken
                context.partnerCode = userAuthVos.partnerCode
                context.agentCode = userAuthVos.agentAuthVos.agentCode
                getContinueFSRV userAuthVos
            } else if (context.partnerCode) {
                log.debug '代理人信息partnerCode：{}', context.partnerCode
                for (Map vo : userAuthVos) {
                    if (vo.containsKey('partnerCode') && vo.partnerCode == context.partnerCode) {
                        log.debug '代理人信息匹配成功：{}', context.partnerCode
                        context.accessToken = vo.accessToken instanceof List ?: [vo.accessToken]
                        context.partnerCode = vo.partnerCode instanceof List ?: [vo.partnerCode]
                        context.agentCode = vo.agentAuthVos.agentCode
                    }
                }
                getContinueFSRV userAuthVos
            } else {
                log.debug '代理人信息异常 partnerCode：{}', context.partnerCode
                getFatalErrorFSRV '获取代理人信息失败'
            }

        } else {
            getFatalErrorFSRV '获取代理人信息失败'
        }
    }

}
