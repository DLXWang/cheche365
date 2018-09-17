package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.picc.flow.Constants._PICC_GET_VEHICLE_OPTION2
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 根据品牌型号查找车型(与交管所车型校验)
 */
@Component
@Slf4j
class GetCarModelsByPlatFormModelCode implements IStep {

    private static final _API_PATH_GET_CAR_MODELS_BY_PLATFORM_MODEL_CODE = '/newecar/car/getCarModelsByPlatFormModelCode'


    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_CAR_MODELS_BY_PLATFORM_MODEL_CODE,
            body              : [
                uniqueID         : context.uniqueID,
                platFormModelCode: context.platFormModelCode
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        def brandModelList = result

        if (brandModelList) {
            getSelectedCarModelFSRV context, brandModelList, result, [
                updateContext: { ctx, res, fsrv ->
                    ctx.carModelInfo = fsrv[2]
                }, getVehicleOption: _PICC_GET_VEHICLE_OPTION2]
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

}
