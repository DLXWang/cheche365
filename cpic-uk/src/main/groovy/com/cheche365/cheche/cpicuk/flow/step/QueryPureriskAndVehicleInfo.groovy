package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.cpicuk.util.BusinessUtils.selectVehicleType
import static com.cheche365.cheche.parser.Constants.get_DATE_FORMAT3
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 获取纯风险保费和车辆信息
 * author yujingtai
 */
@Component
@Slf4j
class QueryPureriskAndVehicleInfo implements IStep {

    private static final _API_PATH_QUERY_PURERISK_AND_VEHICLEINFO = '/ecar/quickoffer/queryPureriskAndVehicleInfo'

    @Override
    Object run(Object context) {
        RESTClient client = context.client
        def auto = context.auto
        def selectedCarModel = context.selectedCarModel

        def queryResult = client.request(Method.POST) { req ->
            requestContentType = JSON
            contentType = JSON
            uri.path = _API_PATH_QUERY_PURERISK_AND_VEHICLEINFO
            body = [
                redata: [
                    "plateNo"          : auto.licensePlateNo,
                    "carVIN"           : auto.vinNo,
                    "searchInterFlag"  : 1,
                    "moldCharacterCode": selectedCarModel.moldCharacterCode,
                    "modelCode"        : selectedCarModel.hyVehicleCode,
                    "engineNo"         : auto.engineNo,
                    "stRegisterDate"   : _DATE_FORMAT3.format(auto.enrollDate ?: new Date()),
                    "usage"            : "101",  // 使用性质
                    "vehicleType"      : selectVehicleType(selectedCarModel.seatCount), // 车辆种类
                    "plateType"        : "02", // 号牌类型

                ]
            ]

            response.success = { resp, json ->
                json
            }

            response.failure = { resp, json ->
                json
            }
        }

        if (queryResult.result) {
            log.debug '车辆详情查询成功'
            // TODO 车型匹配不正确的情况
            context.vehicleInfo = queryResult.result.models[0]
            getContinueFSRV queryResult.result
        } else {
            log.info "车辆详情查询失败， 错误原因：{}", queryResult?.message?.message ?: '纯风险保费查询失败'
            getKnownReasonErrorFSRV queryResult?.message?.message as String ?: '纯风险保费查询失败，请与客服联系'
        }
    }
}
