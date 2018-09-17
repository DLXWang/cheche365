package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static groovyx.net.http.ContentType.JSON

/**
 * 利用车型vehicleMode查找车型
 * Created by wangxiaofei on 2016-08-16.
 */
@Slf4j
class FindJYVehicleMode implements IStep {

    private static final _URL_PATH_FIND_JY_VEHICLE_MODE = '/cpiccar/salesNew/businessCollect/findJYVehicleMode'

    @Override
    run(context) {
        def client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _URL_PATH_FIND_JY_VEHICLE_MODE,
            body              : [
                branchCode  : context.branchCode,
                jgVehicleId : context.vehicleInfo.jgVehicleId,
                random      : context.baseInfoResult?.random ?: '',
                vehicleModel: context.vehicleInfo.vehicleModel,
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        //之前modeCharacterCode为空,需要推送补充信息modeCharacterCode选车型
        if (result.vehicleList) {
            context.originalVehicleModels = result.vehicleList
            getSelectedCarModelFSRV context, context.originalVehicleModels, result, [updateContext: { ctx, res, fsrv ->
                ctx.moldCharacterCode = fsrv[2].moldCharacterCode
                ctx.vehicleInfo += fsrv[2]
            }]
        } else {
            getLoopContinueFSRV result, '没有能够查询到车型'
        }

    }
}
