package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getResponseResult
import static com.cheche365.cheche.picc.util.BusinessUtils.getAutoEngineNo
import static com.cheche365.cheche.picc.util.BusinessUtils.getAutoVinNo
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * Created by suyq on 2015/7/10.
 * 从人保的中科软车型库获取车型
 */
abstract class AFindCarModelByBrandName implements IStep {

    private static final _API_PATH_FIND_CAR_MODEL = '/ecar/car/carModel/findCarModel'

    @Override
    run(context) {
        RESTClient client = context.client
        Auto auto = context.auto

        def args = [
            requestContentType : URLENC,
            contentType        : JSON,
            path               : _API_PATH_FIND_CAR_MODEL,
            body               : generateRequestParameters(context, this)
        ]
        log.info '用如下信息查找车型：{}、{}、{}、{}、{}', auto.licensePlateNo, auto.owner, getAutoVinNo(context), getAutoEngineNo(context), auto.identity
        def result = client.post args, { resp, json ->
            json
        }

        log.debug '获取到的车型信息：{}', result
        getResponseResult result, context, this
    }

}
