package com.cheche365.cheche.gshell.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.BINARY
import static org.apache.commons.codec.binary.Base64.encodeBase64String
import static org.apache.commons.io.IOUtils.toByteArray



/**
 * 获取验证码
 */
@Component
@Slf4j
class Jcaptcha implements IStep {

    private static final _API_PATH_J_CAPTCHA = '/jcaptcha'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: BINARY,
            path              : _API_PATH_J_CAPTCHA,
            query              : [
                'now' : System.currentTimeMillis() as String
            ]
        ]

        def result = client.get args, { resp, is ->
            encodeBase64String toByteArray(is)
        }

        if (result) {
            log.info '成功获得登录验证码'
            context.imageBase64 = result
            getContinueFSRV result
        } else {
            getLoopContinueFSRV result, '没有获取验证码，稍后重试'
        }
    }

}
