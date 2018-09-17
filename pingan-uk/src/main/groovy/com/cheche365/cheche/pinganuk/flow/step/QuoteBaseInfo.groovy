package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取登录后的关于报价的一些基本信息，包括cypherText，报价机构，代理人等相关信息，混杂在一起
 */
@Component
@Slf4j
class QuoteBaseInfo implements IStep {

    private static final _URL_TO_IBCSWRITER = '/ebusiness/auto/newness/toibcswriter.do'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _URL_TO_IBCSWRITER,
            query             : [
                transmitId: 'apply'
            ]
        ]

        def quoteBaseInfo = client.get args, { resp, stream ->
            def inputs = htmlParser.parse(stream).depthFirst().INPUT

            inputs.collectEntries { input ->
                [(input.@id): input.@value]
            }
        }

        if (quoteBaseInfo.cypherText) {
            log.info '获取报价基础信息成功'
            context.baseInfo = quoteBaseInfo
            getContinueFSRV quoteBaseInfo
        } else {
            getFatalErrorFSRV '获取报价基础信息失败'
        }
    }

}
