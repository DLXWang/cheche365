package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取新图片验证码
 * @author xuzewen
 */
@Component
@Slf4j
class GetNewCaptcha implements IStep {

    private static final _API_PATH_GET_DM_VEHICLE_INFO = 'autox/do/get-DMVehicleInfo'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_DM_VEHICLE_INFO,
            body              : [
                vehicleFrameNo: context.auto.vinNo,
                licenseNo     : context.auto.licensePlateNo,
                flowId        : context.flowId,
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('0' == result.resultCode) {
            log.info '成功获得验证码'
            context.imageBase64 = result.checkCode
            getContinueFSRV result
        } else {
            getLoopContinueFSRV result, '没有获取验证码，稍后重试'
        }
    }
}
