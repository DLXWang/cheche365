package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.apache.http.impl.cookie.BasicClientCookie2
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CaptchaUtils.getCaptcha
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV

/**
 * 获取图片验证码基类
 */
@Component
@Slf4j
 abstract class AGetCaptcha implements IStep {

    @Override
    run(context) {
        def fsrv = getFSRV(context)
        if (fsrv) {
            return fsrv
        } else {
            RESTClient client = context.client
            client.client.cookieStore.addCookie new BasicClientCookie2('s_cc', Boolean.TRUE.toString())

            def (captchaText, errorCode, message) = getCaptcha(
                context,
                getApi(),
                { [uniqueId: context.uniqueID] },
                'picc',
                true,
                log)

            if (errorCode) {
                log.warn '获取验证码失败：{}，{}', errorCode, message
                getLoopContinueFSRV null, message
            } else {
                log.info '成功获取验证码：{}', captchaText
                context.captchaText = captchaText
                getContinueFSRV captchaText
            }
        }

    }

    protected getFSRV(context) {
        null
    }

    abstract protected getApi()

}
