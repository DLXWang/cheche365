package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.picc.flow.step.AGetCaptcha
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 获取并识别图片验证码
 */
@Component
@Slf4j
class GetCaptcha extends AGetCaptcha {

    private static final _API_PATH_VALIDATE_CODE = '/newecar/CreateImageNew'

    @Override
    protected getApi() {
        _API_PATH_VALIDATE_CODE
    }

}
