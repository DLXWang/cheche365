package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants.get_VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.checkVehicleSupplementInfo
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 *
 * 精友地区根据车架号获取车型（列表）
 * @author liuxiwu
 */
@Component
@Slf4j
class FindCarModelByVin01 implements IStep {

    private static final _API_PATH_FIND_CAR_MODEL_JY_QUERY = '/newecar/car/findCarModelByVin'

    @Override
    run(context) {
        RESTClient client = context.client

        def uniqueID = context.uniqueID
        def areaCode = context.areaCode
        def vinNo = context.auto.vinNo
        def cityCode = context.cityCode
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_FIND_CAR_MODEL_JY_QUERY,
            body              : [
                'carModelQuery.uniqueId'   : uniqueID,
                'carModelQuery.areaCode'   : areaCode,
                'carModelQuery.frameNo'    : vinNo,
                'carModelQuery.requestType': '04',
                'carModelQuery.enrollDate' : '',
                'carModelQuery.cityCode'   : cityCode
            ]
        ]

        log.info 'FindCarModelByVin01用如下信息查找车型：{}', args

        def result = client.post args, { resp, json ->
            json
        }

        def brandModelList = result.body?.queryVehicle

        /**
         * 根据车架号查车目前看均为一辆车
         */
        if (brandModelList) {
            def fsrv = checkVehicleSupplementInfo context, brandModelList, context.getVehicleOption, false, { ctx, item -> item }, true
            context.newAutoTypes = fsrv[-1]()[0].options
            getContinueFSRV context.newAutoTypes
        } else {
            log.error '无法获得车型，通常是车辆信息有误导致的：{}', result
            getValuableHintsFSRV(context,
                [
                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
                ])
        }
    }

}
