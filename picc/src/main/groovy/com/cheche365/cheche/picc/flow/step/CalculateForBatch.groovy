package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取非续保客户商业险默认套餐报价：
 * 新改版的人保车险流程中仅仅包含经济、豪华，这两种套餐，
 * 我们的实现中选择豪华套餐。
 */
@Component
@Slf4j
class CalculateForBatch implements IStep {

    private static final _API_PATH_CALCULATE_FOR_BATCH = '/ecar/caculate/caculateForBatch'


    @Override
    run(context) {
        def json = retrieveAccurateQuote(context)

        if ('成功' == json.errorMsg) {

            log.info '全险JSON：{}', json

            context.defaultPackageJson = json

            getContinueFSRV json

        } else {
            getFatalErrorFSRV json.errorMsg
        }
    }

    private retrieveAccurateQuote(context) {
        RESTClient client = context.client

        def args = [
            requestContentType  : URLENC,
            contentType         : JSON,
            path                : _API_PATH_CALCULATE_FOR_BATCH,
            body                : generateRequestParameters(context, this)
        ]

        client.post args, { resp, json ->
            json
        }
    }

}
