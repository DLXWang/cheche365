package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.resolveAutoLicensePlate
import static groovyx.net.http.ContentType.JSON



/**
 * 车辆查询
 * Created by wangxiaofei on 2016.9.9
 */
@Component
@Slf4j
class CircVehicleInfoQuery implements IStep {

    private static final _API_PATH_CIRC_VEHICLE_INFO_QUERY = '/icore_pnbs/do/app/quotation/circVehicleInfoQuery'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_CIRC_VEHICLE_INFO_QUERY,
            body              : [
                departmentCode      : context.baseInfo.departmentCode,
                vehicleLicenseNo    : resolveAutoLicensePlate(context.auto.licensePlateNo),
                engineNo            : context.auto.engineNo,
                engineNoCipher      : '',
                vehicleFrameNo      : context.auto.vinNo,
                vehicleFrameNoCipher: ''
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('0' == result.errorCode) {
            log.info '获取车辆信息成功'
            //针对北京地区，车辆查询会查询出一辆车，即推补充信息选择的车型，但是在后续车型查询中会更改车辆信息，即认为北京地区推补充信息无用
            context.vehicleDataList = result.vehicleDataList
            getContinueFSRV result
        } else {
            log.info '获取车辆信息失败'
            getValuableHintsFSRV(context,
                [
                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
                ])
        }
    }

}
