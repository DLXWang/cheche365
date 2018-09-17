package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC
import static org.apache.commons.codec.binary.Base64.encodeBase64String
import static org.apache.commons.io.IOUtils.toByteArray



/**
 * 获取验证码图片
 */
@Component
@Slf4j
class GetLoginCaptcha implements IStep {

    private static final _URL_GET_CAPTCHA_IMAGE = '/ecar/auth/getCaptchaImage'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : BINARY,
            path              : _URL_GET_CAPTCHA_IMAGE
        ]

        def result = client.get args, { resp, is ->
            encodeBase64String toByteArray(is)
        }

        if (result) {
            log.info '成功获取登录验证码'
            context.imageBase64 = result
            getContinueFSRV result
        } else {
            getLoopContinueFSRV result, '没有获取验证码'
        }
    }

}
