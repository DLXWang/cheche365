package com.cheche365.cheche.piccuk.flow.step.v3

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * 影像浏览
 */
@Component
@Slf4j
class ViewScan implements IStep {

    private static final _URL_PATH_VIEW_SCAN = '/SunECM/viewScan.action'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: 'text/xml; charset=gbk',
            contentType       : 'text/html',
            path              : _URL_PATH_VIEW_SCAN,
            query             : [
                method: 'serialView',
                resort: false,
            ],
            body :generateRequestParameters(context,this)
        ]
        def result = client.post args, { resp, html ->
            html
        }

        if (result) {
            getContinueFSRV null
        } else {
            getFatalErrorFSRV '无法初始化影像上传接口'
        }
    }

}
