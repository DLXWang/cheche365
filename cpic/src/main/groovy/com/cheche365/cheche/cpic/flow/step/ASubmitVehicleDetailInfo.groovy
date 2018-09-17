package com.cheche365.cheche.cpic.flow.step

import  com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 提交详细信息，原请求加上第一步返回的opportunityId, orderNo
 * Created by houjinxin on 2015/5/21.
 * TODO 子类实现差异，直接返回差异请求参数
 */
@Component
@Slf4j
abstract class ASubmitVehicleDetailInfo implements IStep {

    private static final _API_PATH_SUBMITVEHICLEDETAILINFO = 'cpiccar/sale/businessCollect/submitVehicleDetailInfo'

    private static final _ERROR_STATUS_CODES = [
        'TURN_INSURANCE_CALCULATE',
        'TURN_INSURANCE_TRAFFIC',
        'INSURANCE_CALCULATE_RETURN_PROBLEM'
    ] as Set



    @Override
    run(context) {
        RESTClient client = context.client
        context.submitVehicleDetailInfoStepFactor = stepFactor

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : apiPath,
            body              : generateRequestParameters(context, this)
        ]

        def result
        try {
            result = client.post args, { resp, json ->
                json
            }
        } catch (ex) {
            log.warn '提交车辆详细信息异常：{}，尝试重试', ex.message
            return getLoopContinueFSRV(null, '提交车辆详细信息异常')
        }
        log.info '提交车辆详细信息，响应为{}', result

        context.vehicleDetailInfo = result
        if (result.insurancePageStatus in _ERROR_STATUS_CODES) {
            getLoopBreakFSRV true
        } else if (result.code == 'B220' && result.error?.contains('未能查到您的车辆数据')) {
            def hints = [
                _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING,
                _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
            ]
            getValuableHintsFSRV context, hints
        } else {
            def matcher = result.totalBlockDescrtion =~ /车龄不能大于\d*年/
            if (matcher.size() > 0) {
                getKnownReasonErrorFSRV matcher[0]
            } else {
                getFatalErrorFSRV '提交车辆详细信息失败'
            }
        }

    }

    protected getApiPath() {
        _API_PATH_SUBMITVEHICLEDETAILINFO
    }

    abstract protected int getStepFactor()



}
