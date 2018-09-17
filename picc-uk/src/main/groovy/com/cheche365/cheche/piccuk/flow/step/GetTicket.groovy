package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getFailedNoticeFSRV
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取Ticket
 * Created by wangxiaofei on 2016.9.1
 */
@Slf4j
class GetTicket implements IStep {

    private static final _URL_LOGIN = '/casserver/login'

    @Override
    run(context) {
        RESTClient client = context.client
        def portalHost = context.portal_host
        def args = [
            requestContentType: URLENC,
            contentType       : BINARY,
            path              : _URL_LOGIN,
            query             : [
                service: "${portalHost}/index.jsp"
            ],
            body              : [
                key        : 'yes',
                _eventId   : 'submit',
                loginMethod: 'nameAndPwd',
                username   : context.username,
                password   : context.password,
                lt         : context.token
            ]
        ]
        log.info 'username:{}m', context.username
        def ticket = client.post args, { resp, stream ->
            def html = stream.text
            def m = html =~ /.*\?ticket=(.*)".*/
            if (m.find()) {
                m[0][1]
            }
        }

        if (ticket) {
            context.ticket = ticket
            log.info '获取Ticket：{}', ticket

            getContinueFSRV ticket
        } else {
            log.info '人保登录失败'
            def errorMsg = '小鳄鱼人保登录失败，原因可能是：获取ticket失败，或者账号密码错误，地区：' +
                context.area?.name + ',username:' + context.username + ',password:' + context.password
            getFailedNoticeFSRV 'crocodile', errorMsg
        }
    }

}
