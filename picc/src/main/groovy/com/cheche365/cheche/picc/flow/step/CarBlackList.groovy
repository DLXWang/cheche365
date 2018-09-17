package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.picc.flow.Constants._STATUS_CODE_PICC_CAR_IN_BLACK_LIST
import static groovyx.net.http.ContentType.JSON

/**
 * 检查车辆是否在PICC黑名单中（旧接口）
 */
@Component
@Slf4j
class CarBlackList implements IStep {

    private static final _API_PATH_BLACK_LIST = '/ecar/caculate/BlackList'


    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            contentType         : JSON,
            path                : _API_PATH_BLACK_LIST,
            query               : generateRequestParameters(context, this)
        ]

        // 检查是否在黑名单中
        def result = client.post args, { resp, json ->
            json
        }

        log.info '车辆黑名单（旧接口）检查结果：{}', result

        if (true == result.result) {
            [_ROUTE_FLAG_DONE, _STATUS_CODE_PICC_CAR_IN_BLACK_LIST, null, '车辆在黑名单中']
        } else {
            context.blackList = result
            getContinueFSRV result
        }

    }

}
