package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取车价
 */
@Component
@Slf4j
class AdjustPurchasePrice implements IStep {

    private static final _API_PATH_ADJUST_PURCHASE_PRICE = '/ecar/car/carModel/adjustPurchasePrice'


    @Override
    run(context) {
        RESTClient client = context.client
        Auto auto = context.auto

        def args = [
            requestContentType  : URLENC,
            contentType         : JSON,
            path                : _API_PATH_ADJUST_PURCHASE_PRICE,
            body                : [
                areaCode    : context.areaCode,
                cityCode    : context.cityCode,
                comCode     : context.comCode,
                uniqueId    : context.uniqueID,
                licenseNo   : auto.licensePlateNo,
                useYears    : context.autoUseYears,
                isRenewal   : context.renewable ? 1 : 0,
                nbCheckFlag : 0 // 宁波需要
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        log.info '获取到的价格：{}', result
        context.adjustedPrice = result

        getContinueFSRV result
    }

}
