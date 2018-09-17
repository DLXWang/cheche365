package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getResponseResult
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 注册UniqueID到车型
 */
@Component
@Slf4j
class RegisterUniqueId implements IStep {

    private static final _API_PATH_FIND_CAR_MODEL = '/ecar/car/carModel/findCarModel'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType  : URLENC,
            contentType         : JSON,
            path                : _API_PATH_FIND_CAR_MODEL,
            body                : generateRequestParameters(context, this)
        ]
        log.info '注册UniqueID到车型请求参数:{}', args
        def json = client.post args, { resp, json ->
            json
        }
        log.info '注册UniqueID到车型的结果：{}', json

        getResponseResult json, context, this
    }

}
