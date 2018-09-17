package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.picc.flow.Constants._PICC_GET_VEHICLE_OPTION2
import static com.cheche365.cheche.picc.flow.Constants._STATUS_CODE_PICC_CANNOT_SEND_NEW_CAR_FAILURE
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * Created by suyq on 2015/6/18.
 * /uecar/car/carModel/underWriteCheckPriceForCar
 * 根据选取的车型获取价格，并注册到uniqueId
 */

@Component
@Slf4j
class CheckPriceForCar implements IStep {

    private static final _API_PATH_CHECK_PRICE_FOR_CAR = '/ecar/car/carModel/underWriteCheckPriceForCar'

    @Override
    run(Object context) {

        RESTClient client = context.client
        def args = getRequestParams(context)
        def result = client.post args, { resp, json ->
            json
        }

        log.info '获取车辆价格的结果：{}', result
        if (result instanceof Map) {
            context.carPrice = result
            getContinueFSRV false
        } else if (result instanceof List) {
            if (result[1]?.contains('填写车辆型号时选择上年使用的车型')) {
                //返回true继续查找车型选择车型
                getContinueFSRV true
            } else if ('4' == (result[0] as String) && result.size() > 2) {
                if (result[2] instanceof Map) {
                    getSelectedCarModelFSRV context, result[2..-1], result, [
                        updateContext   : { ctx, res, fsrv ->
                            ctx.queryCarModelCode = fsrv[2].vehicleId
                        },
                        wrapFsrv        : { fsrv -> getContinueFSRV true },
                        getVehicleOption: _PICC_GET_VEHICLE_OPTION2]
                } else {
                    context.queryCarModelCode = result[2]
                    getContinueFSRV true
                }
            } else {
                log.warn '获取车辆价格错误：{}', result[1]
                [_ROUTE_FLAG_DONE, _STATUS_CODE_PICC_CANNOT_SEND_NEW_CAR_FAILURE, false, result[1]]
            }
        } else {
            log.error '获取车辆价格失败'
            getFatalErrorFSRV '获取车辆价格失败'
        }
    }

    private getRequestParams(context) {
        [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_CHECK_PRICE_FOR_CAR,
            body              : generateRequestParameters(context, this)
        ]
    }

}
