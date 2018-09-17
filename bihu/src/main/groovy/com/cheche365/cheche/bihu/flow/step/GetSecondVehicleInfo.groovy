package com.cheche365.cheche.bihu.flow.step

import com.cheche365.cheche.bihu.util.BusinessUtils
import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.bihu.flow.CityCodeMappings.getCityCode
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3

/**
 * 选择新车车型
 */
@Component
@Slf4j
class GetSecondVehicleInfo implements IStep {

    private static final _API_PATH_GET_SECOND_VEHICLE_INFO = '/api/CarInsurance/GetSecondVehicleInfo'

    @Override
    run(context) {
        def auto = context.auto
        def queryBody = [
            EngineNo    : auto.engineNo,
            CarVin      : auto.vinNo,
            VehicleName : auto.autoType.code,
            VehicleNo   : '', // 精友编码
            CityCode    : getCityCode(context.area.id),
            RegisterDate: _DATE_FORMAT3.format(auto.enrollDate),
        ]

        def result = BusinessUtils.sendAndReceive context, _API_PATH_GET_SECOND_VEHICLE_INFO, queryBody, this.class.name

        if (1 == result.BusinessStatus) {
            getContinueFSRV result
        } else {
            log.error "壁虎请获取新车异常状态码：{}，详细信息：{}", result.BusinessStatus, result.Item.QuoteResult ?: result.StatusMessage
            getFatalErrorFSRV result.StatusMessage
        }
    }

}
