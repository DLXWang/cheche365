package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取车辆信息
 */
@Slf4j
class GetCarInfo implements IStep {

    private static final _API_PATH_GET_CAR_INFO = '/prpall/carInf/getDataFromCiCarInfo.do'

    @Override
    run(context) {

        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_CAR_INFO,
            body              : [
                'prpCitemCar.licenseNo'  : context.auto.licensePlateNo,
                'prpCitemCar.licenseType': '02',
            ]
        ]

        def carInfo = client.post args, { resp, json ->
            if (1 == json.totalRecords) { // TODO 是否会出现多辆
                json.data[0]
            }

        }
        context.carInfo = carInfo
        log.info '获取车辆信息结果：{}', carInfo

        getContinueFSRV carInfo
    }

}
