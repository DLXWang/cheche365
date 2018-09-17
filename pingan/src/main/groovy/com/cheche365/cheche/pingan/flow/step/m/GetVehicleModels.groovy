package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.Constants._CAR_MODEL_LIST_PAGING_SIZE
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.checkSupplementInfo
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV
import static com.cheche365.flow.core.util.FlowUtils.getProvideValuableHintsFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT



/**
 * 根据品牌型号获取车型列表
 */
@Component
@Slf4j
class GetVehicleModels implements IStep {

    private static final _API_PATH_GET_MODEL = 'rsupport/vehicle/brand'

    @Override
    run(context) {

        //补充信息的验证，查看是不是有补充信息,就是看有没有要查询的关键字
        def supplementInfo = getCarModelSupplementInfoFSRV(context)
        if (supplementInfo) {
            return supplementInfo
        }

        //根据品牌信息去查询,获取车辆列表,vehicleModelList的size不可能为0，假设为0时，表示没有查到车，continued一定是false
        def (continued, vehicleModels) = requestAndGetVehicleModels(context)
        if (!continued) {
            //品牌查詢失敗，推有价值提示信息
            return getVehicleModeFailedFSRV(context)
        }

        //查看用户是否有选车,若没有，则推车型列表
        if (vehicleModels) {
            getSelectedCarModelFSRV context, vehicleModels, vehicleModels, [updateContext: { ctx, res, fsrv ->
                ctx.carInfo = fsrv[2]
            }]
        }

    }

    /**
     * 获取车辆品牌型号补充信息FSRV
     * @param context
     * @return
     */
    private getCarModelSupplementInfoFSRV(context) {
        def keyword = context.auto?.autoType?.code
        log.info '按照品牌型号[{}]查询车型信息', keyword
        if (!keyword) {
            def supplement = checkSupplementInfo context, 'vehicleModelSupplementInfoMapping'
            def supplementInfoAutoTypeCode = supplement.find { item ->
                item.fieldPath == 'supplementInfo.code'
            }
            log.info '品牌型号不能为空，请补充品牌型号'
            getNeedSupplementInfoFSRV { [supplementInfoAutoTypeCode] }
        }
    }

    private requestAndGetVehicleModels(context) {
        def vehicleModels = (1..10).inject([[], true]) { listWithState, page ->
            def (list, notSkipped) = listWithState
            if (notSkipped && list.size() < _CAR_MODEL_LIST_PAGING_SIZE) {
                def vehicle = getVehicles(page, context)
                [list + vehicle, vehicle as boolean]
            } else {
                listWithState
            }
        }.first()

        if (vehicleModels) {
            def originalVehicleModels = vehicleModels[0..<Math.min(vehicleModels.size(), _CAR_MODEL_LIST_PAGING_SIZE)]
            context.originalVehicleModels = originalVehicleModels
            [true, originalVehicleModels]
        } else {
            log.warn '品牌查询失败'
            [false, null]
        }


    }

    private getVehicles(page, context) {
        RESTClient client = context.client
        def result = null
        10.times {
            try {
                result = client.request(GET, JSON) { req ->
                    req.params.setParameter(SO_TIMEOUT, context.env.getProperty('pingan.so_timeout') as Integer)
                    uri.path = _API_PATH_GET_MODEL
                    uri.query = [
                        k   : context.auto.autoType.code,
                        page: page
                    ]
                    response.success = { resp, json ->
                        json
                    }
                }
                directive = Closure.DONE
            } catch (SocketTimeoutException stEx) {
                log.warn '用品牌型号获取车型列表第{}页时超时，重试！', page
            }
        }

        if (!result) {
            log.warn '用品牌型号获取车型列表第{}页时超时且重试了10次，放弃', page
            []
        } else if (0 != result.code || !result.results.vehicles) {
            []
        } else {
            result.results.vehicles
        }
    }

    private getVehicleModeFailedFSRV(context) {
        log.error '根据品牌型号查询车型失败，请确认车型信息是否正确'
        def hints = [
            _VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING.with {
                it.originalValue = context.auto?.autoType?.code
                it
            }
        ]
        getProvideValuableHintsFSRV { hints }
    }

}

