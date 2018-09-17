package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC


/**
 * 获取代理人授权信息
 * @author: lp
 * @date: 2018/4/20 10:23
 */
@Component
@Slf4j
class GetAuthorization implements IStep {

    private static final _API_PATH_GET_AUTHORIZATION = '/icore_pnbs/do/app/cache/agent/queryConfer'

    @Override
    run(Object context) {
        RESTClient client = context.client
        def baseInfo = context.baseInfo

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_AUTHORIZATION,
            body              : [
                agentCode               : baseInfo.agentCode,
                partnerWorknetCode      : baseInfo.partnerWorkNetCode,
                channelSourceCode       : baseInfo.channelSourceCode,
                departmentCode          : baseInfo.departmentCode,
                businessSourceDetailCode: baseInfo.businessSourceDetailCode
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result) {
            log.debug '获取代理人授权信息：{}', result.encodeDict
            context.baseInfo.authorization = result.encodeDict[0]
            getContinueFSRV result.encodeDict
        } else {
            getFatalErrorFSRV '获取代理人授权信息失败'
        }
    }
}

