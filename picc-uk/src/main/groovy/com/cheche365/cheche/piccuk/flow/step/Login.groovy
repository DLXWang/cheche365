package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 进入车险承保系统
 * Created by wangxiaofei on 2016.9.1
 */
@Slf4j
class Login implements IStep {

    private static final _URL_PRPALL = '/prpall'

    @Override
    run(context) {
        RESTClient client = context.client
        client.uri = context.prpall_host

        def args = [
                requestContentType: URLENC,
                contentType       : TEXT,
                path              : _URL_PRPALL,
                query             : [
                    calogin: ''
                ]
        ]

        client.get args
        log.info '登录车险承保系统成功'

        getContinueFSRV true
    }
}
