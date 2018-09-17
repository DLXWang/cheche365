package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getResponseResult
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_1
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取车型信息及车辆相关信息
 */
@Component
@Slf4j
class FindCarModelInfo implements IStep {
    private static final _URL_FIND_CAR_MODEL_INFO = '/online/saleNewCar/carProposalfindCarModelInfo.do'

    @Override
    run(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_1

        RESTClient client = context.client

        def args = [
            requestContentType : URLENC,
            contentType        : JSON,
            path               : _URL_FIND_CAR_MODEL_INFO,
            body               : generateRequestParameters(context, this)
        ]

        def result = client.post args, { resp, json ->
            json
        }

        getResponseResult result, context, this
    }

}
