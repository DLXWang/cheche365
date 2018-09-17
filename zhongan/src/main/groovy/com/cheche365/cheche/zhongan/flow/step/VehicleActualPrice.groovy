package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getCarEnrollDate
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getStandardHintsFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive



/**
 * 车辆实际价值查询
 */
@Component
@Slf4j
class VehicleActualPrice implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.query.vehicleActualPrice'

    @Override
    def run(Object context) {
        def bsStartDateText = getCommercialInsurancePeriodTexts(context, _DATETIME_FORMAT3, false)?.first()
        def enrollDate = _DATE_FORMAT3.format getCarEnrollDate(context)
        def vehicleAcquisitionPrice = context.selectedCarModel.vehicleAcquisitionPrice
        def params = [
            insureFlowCode       : context.insureFlowCode,
            businessEffectiveDate: bsStartDateText,          //商业险起期
            vehicleRegisterDate  : enrollDate,               //初登日期
            purchasePrice        : vehicleAcquisitionPrice,  //新车购置价
        ]
        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)
        log.info '车辆实际价值result = {}', result

        if ('0' == result.result) {
            context.vehicleActualPrice = result.vehicleActualPrice
            getContinueFSRV result
        } else {
            getStandardHintsFSRV result
        }
    }

}
