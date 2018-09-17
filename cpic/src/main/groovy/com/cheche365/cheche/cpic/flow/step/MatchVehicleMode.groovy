package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON

/**
 * 匹配车型
 * Created by wangxiaofei on 2016-08-16.
 */
@Slf4j
class MatchVehicleMode implements IStep {

    private static final _URL_PATH_MATCH_VEHICLE_MODE = '/cpiccar/salesNew/businessCollect/matchVehicleMode'

    @Override
    run(context) {
        def client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _URL_PATH_MATCH_VEHICLE_MODE,
            body              : [
                jgVehicleId       : context.vehicleInfo.jgVehicleId,
                modeCharacterCode : context.modeCharacterCode,
                random            : context.baseInfoResult?.random ?: '',
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result) {
            log.info '成功匹配车型'
            getContinueFSRV result
        } else {
            getLoopContinueFSRV result, '匹配车型失败'
        }
    }

}
