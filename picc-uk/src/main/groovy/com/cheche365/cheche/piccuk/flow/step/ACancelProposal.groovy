package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC

/**
 * 取消、撤销投保单基类
 * Created by liheng on 2016/11/3.
 */
@Slf4j
abstract class ACancelProposal implements IStep {

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : HTML,
            path              : getApiPath(context),
            query             : [
                bizNo: context.editProposalNo
            ]
        ]

        def result = client.post args, { resp, html ->
            html
        }

        (result =~ /message=(.*)/).with { m ->
            if (m.find()) {
                log.info m[0][1]
            }
        }
        getContinueFSRV result

    }

    protected abstract getApiPath(context)
}
