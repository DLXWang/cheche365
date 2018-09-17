package com.cheche365.cheche.picc.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static groovyx.net.http.ContentType.JSON

/**
 * 历史用户获取并验证续保用户的图片校验码
 */
@Component
@Slf4j
class HistoryCarVerifyCaptcha extends AVerifyCaptcha {

    private static final _API_PATH_GET_CAR_REUSE_DATA = '/ecar/renewal/getCarReuseData'

    @Override
    protected getRequestParams( context ) {
        [
            contentType : JSON,
            path        : _API_PATH_GET_CAR_REUSE_DATA,
            query       : [
                    renewalRandom : context.captchaText ,
                    tokenNo       : context.auto.identity,
                    uniqueID      : context.uniqueID,
                    licenseno     : context.auto.licensePlateNo,
                    areaCode      : context.areaCode
                ]
        ]
    }
}
