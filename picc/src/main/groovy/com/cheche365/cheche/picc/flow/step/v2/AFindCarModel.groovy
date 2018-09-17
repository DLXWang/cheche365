package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 查询车型郑州
 */
@Component
@Slf4j
abstract class AFindCarModel implements IStep {

    private static final _API_PATH_FIND_CAR_MODEL = '/newecar/car/findCarModel'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_FIND_CAR_MODEL,
            body              : getBodyRequestParameters(context)
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('0000' == result.head.errorCode) {
            // 这里是用于后面校验步骤的车辆信息
            context.vehicleInfo = result.body + (context.vehicleListInfo?.subMap(['engineNo','enrollDate','frameNo']) ?: [:])
            //03 和04稍有不同
            getFSRV(result)
        } else {
            log.error '无法获得车型，通常是车辆/人员信息有误导致的：{}', result
            getValuableHintsFSRV(context,
                [
                    _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
                ])
        }
    }

    /**
     * 获取请求参数body部分
     */
    abstract protected getBodyRequestParameters(context)

    abstract protected getFSRV(result)

}
