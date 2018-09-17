package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取Token
 * Created by wangxiaofei on 2016.9.1
 */
@Slf4j
class GetToken implements IStep {

    private static final _URL_LOGIN = '/casserver/login'

    @Override
    run(context) {

        RESTClient client = context.client
        def portalHost = context.portal_host
        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _URL_LOGIN,
            query             : [
                service: "${portalHost}/portal/index.jsp"
            ]
        ]

        def token = client.get args, { resp, reader ->
            htmlParser.parse(reader).depthFirst().INPUT.find {
                'lt' == it.@name
            }?.@value
        }

        if (token) {
            context.token = token
            log.info '获取Token成功：{}', token
            getContinueFSRV token
        } else {
            getFatalErrorFSRV '获取Token失败'
        }
    }

}
