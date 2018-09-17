package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 校验新验证码
 * @author xuzewen
 */
@Component
@Slf4j
class VerifyNewCaptcha implements IStep {

    private static final _API_PATH_QUERY_DM_VEHICLE_INFO = 'autox/do/query-DMVehicleInfo-confirm'

    @Override
    run(context) {
        def client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_QUERY_DM_VEHICLE_INFO,
            body              : [
                flowId        : context.flowId,
                checkCode     : context.captchaText,
                isNewness     : true,
                licenseNo     : context.auto.licensePlateNo,
                modelId       : context.renewable? context.renewalVehicleInfo.'vehicle.model' : context.carInfo.vehicle_fgw_code,
                vehicleFrameNo: context.auto.vinNo,
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('00' == result.resultCode) {
            log.info '成功校验验证码'
            getLoopBreakFSRV result
        } else {
            if ('-2' == result.resultCode) {
                log.warn '车辆在交管平台无匹配：{}', result
                getValuableHintsFSRV context, [_VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING]
            } else if ('-400' == result.resultCode) {
                getLoopContinueFSRV result, '校验验证码失败，稍后重试'
            }
        }
    }

}
