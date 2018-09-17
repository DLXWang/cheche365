package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CaptchaUtils.getCaptcha
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.cpic.util.BusinessUtils._PREPROCESS_CAPTCHA_IMAGE

/**
 * 获取并识别验证码
 * Created by yuhao on 2015/12/31.
 */
@Component
@Slf4j
class VerifyCaptcha implements IStep {

    private static final _API_PATH_VALIDATE_CODE = '/cpiccar/sales/businessCollect/generateCaptcha'

    @Override
    run(context) {
        def (captchaText, errorCode, message) = getCaptcha(
            context,
            _API_PATH_VALIDATE_CODE,
            {
                [ q : System.currentTimeMillis() as String ]
            },
            'cpic',
            false,
            log,
            _PREPROCESS_CAPTCHA_IMAGE)

        if (errorCode) {
            log.warn '获取验证码失败：{}，{}', errorCode, message
            getLoopContinueFSRV errorCode, '获取验证码失败'
        } else {
            log.info '成功获取验证码：{}', captchaText
            context.captchaText = captchaText
            getLoopBreakFSRV captchaText
        }
    }

}
