package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 代理人登录
 */
@Component
@Slf4j
class PartnerLogin implements IStep {

    private static final _API_PATH_J_SPRING_SECURITY_CHECK = '/ecar/j_spring_security_check'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_J_SPRING_SECURITY_CHECK,
            body              : [
                access_token: context.accessToken[0],
                partner_code: context.partnerCode[0],
                j_username  : context.username,
                agent_code  : context.agentCode[0]
            ],
            headers           : [
                'X-Requested-With': 'XMLHttpRequest'
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result?.authentication) {
            log.info '代理人登录成功'
            getContinueFSRV result
        } else {
            getFatalErrorFSRV '代理人登录失败'
        }
    }

}
