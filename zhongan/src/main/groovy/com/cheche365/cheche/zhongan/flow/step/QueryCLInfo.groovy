package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive



/**
 * 车辆查询
 */
@Component
@Slf4j
class QueryCLInfo implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.queryCLInfo'

    @Override
    def run(context) {
        Auto auto = context.auto

        def params = [
            vehicleLicenceType   : '02',
            vehicleLicencePlateNo: auto.licensePlateNo,
            vehicleOwnerName     : auto.owner,
            insurePlaceCode      : context.cityCode
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)
        log.debug '车辆查询：{}', result

        if ('0' == result.result) {
            context.carInfo = result
        }
        getContinueFSRV result
    }

}
