package com.cheche365.cheche.piccuk.flow.step.v3

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component
import sun.misc.BASE64Encoder

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getXmlStrNew
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getXmlStrOld
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 上传影像
 */
@Component
@Slf4j
class UploadImage implements IStep {

    private static final _API_PATH_UPLOAD_IMAGE = '/piccuk/upload/image'

    @Override
    run(context) {
        RESTClient client = new RESTClient(context.uploadImageUrl)
        def xmlStr = context.xmlStrOld ? getXmlStrOld(context) : getXmlStrNew(context)
        def args = [
            path              : _API_PATH_UPLOAD_IMAGE,
            requestContentType: JSON,
            contentType       : JSON,
            body              : [
                batchScanIp    : context.batchScanHost,
                batchScanSocket: context.batchScanPort,
                appcode        : "prpol",
                xmlStr         : new BASE64Encoder().encode(xmlStr.bytes),
                busino         : context.proposalNos.last().first(),
                batchId        : context.batchID,
                interVer       : context.interVer,
                batchImages    : context.batchImages,
                xmlFileName    : context.batchID + '_' + context.interVer + '.syd'
            ]
        ]
        def result
        try {
            result = client.post args, { re, json ->
                json
            }
        } catch (e) {
            log.error '上传影像服务连接不上'
            return getKnownReasonErrorFSRV('上传影像服务连接不上')
        }
        if (result) {
            if (result.code == 200) {
                log.debug '上传影像服务：{}', result.message
                getContinueFSRV null
            } else if (result.code == 301) {
                log.debug '上传影像服务：{}', result.message
                getKnownReasonErrorFSRV('上传影像服务提示：' + result.message)
            } else {
                log.debug '上传影像服务：{}', result.message
                getKnownReasonErrorFSRV('上传影像服务提示：' + result.message)
            }
        } else {
            getKnownReasonErrorFSRV '上传影像服务无回应'
        }


    }
}
