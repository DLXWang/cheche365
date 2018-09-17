package com.cheche365.cheche.picc.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV

/**
 * 获取图片验证码
 */
@Component
@Slf4j
class GetCaptcha extends AGetCaptcha {

    private static final _API_PATH_VALIDATE_CODE  = '/ecar/validateCode'


    @Override
    protected getApi() {
        _API_PATH_VALIDATE_CODE
    }

    @Override
    protected getFSRV(context) {
        if (!context.renewable && '1' != context.sFlag) {
            log.info '此车不是历史车辆，也不是续保车辆，不需要获取及校验验证码'
            return getLoopBreakFSRV (null)
        }
    }

}
