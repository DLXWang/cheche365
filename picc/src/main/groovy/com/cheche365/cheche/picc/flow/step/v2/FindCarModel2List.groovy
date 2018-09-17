package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants.get_DATE_FORMAT1
import static com.cheche365.cheche.parser.util.BusinessUtils.filterSupplementVehicles
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 查询车型郑州
 */
@Component
@Slf4j
class FindCarModel2List implements IStep {

    private static final _API_PATH_FIND_CAR_MODEL = '/newecar/car/findCarModel'

    @Override
    run(context) {
        log.info "picc查车接口----上海地区查车"
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

        context.vehicleListInfo = result.body
        if (context.vehicleListInfo?.carModels) {

            def supplementInfo = filterSupplementVehicles context, context.vehicleListInfo?.carModels, context.getVehicleOption,true

            context.newAutoTypes = supplementInfo[0].options

            getContinueFSRV  context.newAutoTypes
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

    protected getBodyRequestParameters(context) {

        Auto auto = context.auto
        [
                'carModelQuery.requestType': '02',
                'carModelQuery.areaCode'   : context.areaCode,
                'carModelQuery.uniqueId'   : context.uniqueID,
                'carModelQuery.licenseNo'  : auto.licensePlateNo,
                'carModelQuery.carModel'   : auto.autoType.code ?: auto.engineNo,
                'carModelQuery.frameNo'    : auto.vinNo,
                'carModelQuery.engineNo'   : auto.engineNo,
                'carModelQuery.enrollDate' : auto.enrollDate ? _DATE_FORMAT1.format(auto.enrollDate) : null,
                'carModelQuery.licenseType': '02'
        ]
    }
}
