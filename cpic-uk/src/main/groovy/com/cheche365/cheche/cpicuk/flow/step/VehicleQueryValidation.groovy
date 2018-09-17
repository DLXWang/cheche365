package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 获取交管验证码
 */
@Component
@Slf4j
class VehicleQueryValidation implements IStep {

    private static final _URL_VEHICLE_QUERY_VALIDATION = '/ecar/ecar/vehicleQueryValidation'

    @Override
    run(context) {
        def auto = context.auto
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _URL_VEHICLE_QUERY_VALIDATION,
            body              : [
                meta  : [:],
                redata: [
                    plateNo: auto.licensePlateNo,
                    vin    : auto.vinNo,
                ]
            ]
        ]
        def result = client.post args, { resp, json ->
            json
        }

        if (result && 'success' == result.message?.code) {
            context.imageBase64 = result.result?.checkCode.replaceAll(' ', '')
            context.checkNo = result.result?.checkNo
            log.debug '成功获取交管验证码，{}' , context.checkNo
            getContinueFSRV '成功验证码'
        } else {
            getKnownReasonErrorFSRV '获取验证码失败'
        }

    }



}
