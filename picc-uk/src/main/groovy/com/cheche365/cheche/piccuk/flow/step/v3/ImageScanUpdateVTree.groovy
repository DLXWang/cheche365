package com.cheche365.cheche.piccuk.flow.step.v3

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC

@Component
@Slf4j
class ImageScanUpdateVTree implements IStep {

    private static
    final _URL_PATH_IMAGE_SCAN_UPDATE_VTREE = '/SunECM/public/frame/imageScanUpdate/imageScanUpdteVTree.jsp'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : BINARY,
            path              : _URL_PATH_IMAGE_SCAN_UPDATE_VTREE,
        ]
        def result = client.get args, { resp, html ->
            html.text
        }
        //使用正则获取已知的参数
        def regStr = result =~ /xmlStr='(.*)'/
        if (regStr.find()) {
            def xmlStrOld = regStr[0][1]
            if (xmlStrOld) {
                //已有旧照片，追加新图片信息
                context.xmlStrOld = xmlStrOld
            }
        }

        getContinueFSRV null
    }
}
