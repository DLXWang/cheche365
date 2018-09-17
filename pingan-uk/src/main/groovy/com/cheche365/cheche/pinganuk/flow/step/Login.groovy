package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_1
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC

/**
 * 登录
 */
@Component
@Slf4j
class Login implements IStep {

    private static final _URL_LOGIN = '/ebusiness/j_security_check'

    @Override
    run(context) {

        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_1

        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _URL_LOGIN,
            query             : [
                'j_username'  : getEnvProperty(context, 'pinganuk.username'),
                'j_password'  : getEnvProperty(context, 'pinganuk.password'),
                'SMAUTHREASON': '0',
            ]
        ]

        client.get args

        log.info '登录成功'
        getContinueFSRV true
    }

}
