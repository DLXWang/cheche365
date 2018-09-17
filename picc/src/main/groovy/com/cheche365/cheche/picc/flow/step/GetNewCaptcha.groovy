package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.picc.util.BusinessUtils.getAutoVinNo
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取新图片验证码
 * @author wangxiaofei
 */
@Component
@Slf4j
class GetNewCaptcha implements IStep {

    private static final _API_PATH_GET_VERIFICATION_CODE = 'ecar/car/carModel/getverificationcode'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_VERIFICATION_CODE,
            body              : [
                cityCode : context.cityCode,
                uniqueId : context.uniqueID,
                frameNo  : getAutoVinNo(context),
                licenseno: context.auto.licensePlateNo
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('0000' == result.errorCode) {
            log.info '成功获得验证码'
            context.imageBase64 = result.checkCode
            getContinueFSRV result
        } else {
            getLoopContinueFSRV result, '没有获取验证码，稍后重试'
        }
    }
}
