package com.cheche365.cheche.baoxian.flow.step.v2m

import com.cheche365.cheche.baoxian.flow.step.AUpdateQuoteInfo
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.flow.Handlers._KIND_CODE_CONVERTERS_CONFIG
import static com.cheche365.cheche.common.Constants.get_DATE_FORMAT3
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static java.time.LocalDate.now as today


@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class UpdateQuoteInfoForChangeItemAndCarInfo extends AUpdateQuoteInfo {

    @Override
    protected getParams(context) {
        def auto = context.auto
        def isNew = !(auto.licensePlateNo as boolean)
        def transferFlag = context?.additionalParameters?.supplementInfo?.transferDate
        def enrollDate = auto.enrollDate ? _DATE_FORMAT3.format(auto.enrollDate) : context.historicalInfo.carInfo.registDate
        def isNeedPrice = getLocalDate(_DATE_FORMAT3.parse(enrollDate)) >= today().minusMonths(9)
        [
            taskId    : context.taskId,
            prvId     : context.provider.prvId,
            carInfo   : [
                isNew        : isNew ? 'Y' : 'N',
                carLicenseNo : isNew ? '' : auto.licensePlateNo,
                price        : isNew || isNeedPrice ? context.selectedCarModel.price : '',
                vinCode      : auto.vinNo,
                engineNo     : auto.engineNo,
                registDate   : enrollDate,
                vehicleId    : context.selectedCarModel.vehicleId,
                isTransferCar: transferFlag ? 'Y' : 'N',
                //当isTransferCar=Y时，此参数必传
                transferDate : transferFlag ? _DATE_FORMAT3.format(context.additionalParameters.supplementInfo?.transferDate) : null,
                vehicleName  : auto.autoType.code,
                seat         : auto.autoType.seats ?: context.additionalParameters.supplementInfo.seats,
            ],
            insureInfo: toFormedParams(context, _KIND_CODE_CONVERTERS_CONFIG),
        ]
    }

    @Override
    protected getResultFSRV(result,context) {
        if ('0' == result.code || '00' == result.respCode) {
            log.info '提交更改险种信息成功'
            getContinueFSRV result
        } else {
            log.error '信息修改失败：{}', result.msg
            getFatalErrorFSRV result.msg
        }
    }
}
