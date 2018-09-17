package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getModelsByCondition
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static groovyx.net.http.ContentType.JSON
import static java.lang.Math.ceil

/**
 * 提交车辆信息表单含车牌号和车主姓名
 */
@Component
@Slf4j
class QueryVehicleModelOnPlatformNew implements IStep {

    private static final _API_PATH_GET_MODEL = 'cpiccar/sales/businessCollect/queryVehicleModelOnPlatformNew'

    private static final _PAGE_SIZE = 5 // 官网品牌型号筛选不支持其他pageSize

    private final _GET_VEHICLE_MODELS_BY_PAGE = { context, pageIndex ->
        def args = getRequestParams context, pageIndex
        context.client.get args, {resp, json ->
            json.vehicleModelPlatList
        }
    }

    @Override
    run(context) {
        def args = getRequestParams context, 1
        def result
        try {
            result = context.client.get args, { resp, json ->
                json
            }
        } catch (ex) {
            log.warn "关键字查询车型信息异常：{}，, 尝试重试", ex.message
            return getLoopContinueFSRV(null, "关键字查询车型信息异常")
        }

        if (result.vehicleModelPlatList) {
            def sum = result.vehicleModelPlatList[0].sum
            def pageCount = (ceil(sum / _PAGE_SIZE)) as int
            def pageList = 1..pageCount
            def vehicleModels = getModelsByCondition context, pageList, result.vehicleModelPlatList, _GET_VEHICLE_MODELS_BY_PAGE
            context.originalVehicleModels = vehicleModels
            getSelectedCarModelFSRV context, vehicleModels, result, [updateContext: { ctx, res, fsrv ->
                ctx.moldCharacterCode = fsrv[2]?.moldCharacterCode
                ctx.vehicleInfo += fsrv[2] ?: [:]
            }]
        } else {
            log.error '获取车型信息失败'
            getValuableHintsFSRV context, [_VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING]
        }
    }

    private getRequestParams(context, pageIndex) {
        def vehicleInfo = context.vehicleInfo
        [
            contentType: JSON,
            path       : _API_PATH_GET_MODEL,
            query      : [
                q           : context.auto.autoType?.code ?: '',
                limit       : _PAGE_SIZE,
                pageIndex   : pageIndex,
                random      : vehicleInfo.random,
                branchCode  : context.branchCode,
                productKind : vehicleInfo.productKind,
                vehicleModel: context.auto.autoType?.code ?: '',
                timestamp   : new Date().getTime()
            ]
        ]
    }

}
