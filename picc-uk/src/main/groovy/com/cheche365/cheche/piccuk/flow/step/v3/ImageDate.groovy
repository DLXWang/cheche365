package com.cheche365.cheche.piccuk.flow.step.v3

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC

/**
 * 生成影像提交时的更新时间
 */
@Component
@Slf4j
class ImageDate implements IStep {

    private static final _URL_PATH_IMAGE_DATE = '/SunECM/imageDate.action'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : HTML,
            path              : _URL_PATH_IMAGE_DATE,
        ]

        def result = client.post args, { resp, html ->
            html
        }
        if (result) {
            context.modifyTime = result
            getContinueFSRV null
        } else {
            getFatalErrorFSRV '生成影像提交时间有误'
        }
    }

}
