package com.cheche365.cheche.piccuk.flow.step.v3

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC

/**
 * 进入影像系统
 */
@Component
@Slf4j
class ImageScanUpdateAction implements IStep {
    private static final _URL_PATH_UPDATE_ACTION = '/SunECM/ImageScanUpdateAction.action'

    @Override
    Object run(Object context) {
        RESTClient client = context.client
        client.uri = context.sunecm_host
        def args = [
            requestContentType: URLENC,
            contentType       : HTML,
            path              : _URL_PATH_UPDATE_ACTION,
            body              : [
                url        : context.sunECMUrl,
                opreateType: context.sunECMOpreateType,
                businessNo : context.sunECMBusinessNo,
                applyNo    : context.sunECMApplyNo,
                xml        : context.sunECMXml
            ]
        ]

        def result = client.post args, { resp, html ->
            def m = html =~ /.*It's now at (.*)./
            if (m.find()) {
                log.info '跳转地址：{}', m[0][1]
                m[0][1]
            }
        }
        if (result) {
            getContinueFSRV null
        } else {
            getKnownReasonErrorFSRV '跳转失败'
        }
    }
}
