package com.cheche365.cheche.picc.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static groovyx.net.http.ContentType.JSON

/**
 * 获取并校验续保用户的图片验证码
 */
@Component
@Slf4j
class VerifyCaptcha extends AVerifyCaptcha {

    private static final _API_PATH_RENEWAL_CHECK    = '/ecar/renewal/renewalCheck'

    @Override
    protected getRequestParams(context) {
        [
            contentType : JSON,
            path        : _API_PATH_RENEWAL_CHECK,
            query       : generateRequestParameters(context, this)
        ]
    }

}
