package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.flow.Constants._UPDATE_CONTEXT_FIND_CAR_MODEL_INFO
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_1
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENROLL_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.flow.core.util.FlowUtils.getProvideValuableHintsFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取车型信息及车辆相关信息
 */
@Component
@Slf4j
class FindCarModelInfoByMultiBrand implements IStep {
    private static final _URL_FIND_CAR_MODEL_INFO = '/online/saleNewCar/carProposalfindCarModelInfo.do'

    @Override
    run(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_1

        RESTClient client = context.client

        def results = context.vehicleInfoList.inject([]) { prevResults, vehicle ->
            context.multiVehicleInfo = vehicle
            def args = [
                requestContentType: URLENC,
                contentType       : JSON,
                path              : _URL_FIND_CAR_MODEL_INFO,
                body              : generateRequestParameters(context, this)
            ]

            def currResult = client.post args, { resp, json ->
                json
            }

            prevResults + currResult
        }
        def carList = results.collect { result ->
            result.list
        }.sum()
        def result = results.find { result ->
            result.list
        }

        def vehicleList = carList?.unique {
            [it.RBCode, it.purchasePrice, it.purchasePriceTax]
        }

        if (vehicleList) {
            getSelectedCarModelFSRV context, vehicleList, result, [updateContext: _UPDATE_CONTEXT_FIND_CAR_MODEL_INFO.curry(log)]
        } else {
            def hints = [
                _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.licensePlateNo
                    it
                },
                _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.vinNo
                    it
                },
                _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.engineNo
                    it
                },
                _VALUABLE_HINT_ENROLL_DATE_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.enrollDate
                    it
                },
                _VALUABLE_HINT_OWNER_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.owner
                    it
                },
                _VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.autoType?.code
                    it
                }
            ]
            getProvideValuableHintsFSRV { hints }
        }
    }

}
