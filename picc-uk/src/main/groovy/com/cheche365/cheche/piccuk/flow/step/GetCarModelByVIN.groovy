package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getEnrollDateText
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取车型信息
 */
@Slf4j
class GetCarModelByVIN implements IStep {

    private static final _API_PATH_GET_CAR_MODEL_INFO = '/prpall/vehicle/queryVehicleByPrefillVIN.do'

    @Override
    run(context) {
        RESTClient client = context.client
        def carInfo = context.carInfo
        def auto = context.auto

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_CAR_MODEL_INFO,
            body              : [
                'prpCitemCar.licenseNo' : auto.licensePlateNo,
                'prpCitemCar.engineNo'  : auto.engineNo ?: carInfo?.engineNo,
                'prpCitemCar.vinNo'     : auto.vinNo ?: carInfo?.rackNo,
                'prpCitemCar.enrollDate': getEnrollDateText(context),
                'dmFlag'                : '0'
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }
        if (result) {
            context.carModelMsg = result.msg - '!'
            log.debug '获取到车型信息：{}', context.carModelMsg
            getContinueFSRV true
        }
    }

}
