package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.picc.util.BusinessUtils.getAutoEngineNo
import static com.cheche365.cheche.picc.util.BusinessUtils.getAutoVinNo
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 查询车型
 */
@Component
@Slf4j
class FindCarModel implements IStep {

    private static final _API_PATH_FIND_CAR_MODEL = '/ecar/car/carModel/findCarModel'

    @Override
    run(context) {
        RESTClient client = context.client
        Auto auto = context.auto

        log.debug '用如下信息查找车型：{}、{}、{}、{}、{}', auto.licensePlateNo, auto.owner, getAutoVinNo(context), getAutoEngineNo(context), auto.identity

        client.request(Method.POST) { req ->
            contentType = JSON
            requestContentType = URLENC
            uri.path = _API_PATH_FIND_CAR_MODEL
            body = generateRequestParameters(context, this)

            response.success = { resp, json ->
                def carInfo = json.body
                def brandModels = json.body ? json.body?.carModels ?: [json.body] : null
                if (brandModels) {
                    context.originalVehicleModels = brandModels
                    getSelectedCarModelFSRV context, brandModels, json, [
                        updateContext: { ctx, res, fsrv ->
                            ctx.carInfo = carInfo
                            ctx.selectedCarModel = fsrv[2]
                        }, wrapFsrv  : { fsrv ->
                        getLoopBreakFSRV fsrv[2]
                    }]
                } else {
                    log.error '无法获得车型，通常是车辆/人员信息有误导致的：{}', json
                    getValuableHintsFSRV(context,
                        [
                            _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING,
                            _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                            _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING,
                            _VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
                        ])
                }
            }

            response.failure = { resp, reader ->
                log.warn "获取车型时异常，稍后重试：{}", '连接异常，重新查询车型'
                return getLoopContinueFSRV(null, '车型查询失败')
            }
        }
    }

}
