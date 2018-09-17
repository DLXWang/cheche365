package com.cheche365.cheche.bihu.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.bihu.flow.CityCodeMappings.getCityCode
import static com.cheche365.cheche.bihu.util.BusinessUtils.sendAndReceive
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * 获取新车车型
 */
@Component
@Slf4j
class GetFirstVehicleInfo implements IStep {

    private static final _API_PATH_GET_FIRST_VEHICLE_INFO = '/api/CarInsurance/GetFirstVehicleInfo'

    @Override
    run(context) {
        def auto = context.auto
        def queryBody = [
            EngineNo: auto.engineNo,
            CarVin  : auto.vinNo,
            MoldName: auto.autoType?.code,
            CityCode: getCityCode(context.area.id)
        ]

        def result = sendAndReceive context, _API_PATH_GET_FIRST_VEHICLE_INFO, queryBody, this.class.name

        if (1 == result.BusinessStatus) {
            getContinueFSRV result
        } else {
            log.error "壁虎请获取新车异常状态码：{}，详细信息：{}", result.BusinessStatus, result.Item.QuoteResult ?: result.StatusMessage
            getFatalErrorFSRV result.StatusMessage
        }
    }

}
