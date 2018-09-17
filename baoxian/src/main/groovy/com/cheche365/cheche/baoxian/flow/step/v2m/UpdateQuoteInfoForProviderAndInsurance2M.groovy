package com.cheche365.cheche.baoxian.flow.step.v2m

import com.cheche365.cheche.baoxian.flow.step.AUpdateQuoteInfo
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.flow.Handlers._KIND_CODE_CONVERTERS_CONFIG
import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_ENROLL_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ID_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static java.time.LocalDate.now as today


/**
 * 修改信息:传供应商和险种信息
 * @author zhaoym
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class UpdateQuoteInfoForProviderAndInsurance2M extends AUpdateQuoteInfo {

    @Override
    protected getParams(context) {

        def taskId = context.taskId
        def auto = context.auto
        def isNew = !(auto.licensePlateNo as boolean)
//        def transferFlag = context?.additionalParameters?.supplementInfo?.transferFlag
        def transferFlag = context?.additionalParameters?.supplementInfo?.transferDate
        def enrollDate = auto.enrollDate ? _DATE_FORMAT3.format(auto.enrollDate) : context.historicalInfo.carInfo.registDate
        def isNeedPrice = getLocalDate(_DATE_FORMAT3.parse(enrollDate)) >= today().minusMonths(9)
        [
            taskId    : taskId,
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
            carOwner  : [
                name      : auto.owner,
                idcardType: '0',
                idcardNo  : auto.identity,
                phone     : auto.mobile,
            ],
            //险种信息
            insureInfo: toFormedParams(context, _KIND_CODE_CONVERTERS_CONFIG),

            //供应商
            providers : [
                context.provider.collect {
                    [prvId: it.prvId]
                }
            ]
        ]
    }

    @Override
    protected getResultFSRV(result, context) {
        if ('0' == result.code || '00' == result.respCode) {
            log.info '提交供应商和险种信息成功'
            getContinueFSRV result.code
        } else if ('请输入正确的证件号码' == result.errorMsg) {
            getValuableHintsFSRV context, [_VALUABLE_HINT_ID_TEMPLATE_QUOTING]
        } else {
            log.error '信息修改失败：{}', result.msg ?: result.errorMsg
            getFatalErrorFSRV result.msg ?: result.errorMsg
        }
    }

    @Override
    protected checkSupplementInfo(params) {
        if (!params.carInfo.registDate) {
            getSupplementInfoFSRV([_SUPPLEMENT_INFO_ENROLL_DATE_TEMPLATE_QUOTING])
        }
    }
}
