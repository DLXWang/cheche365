package com.cheche365.cheche.sinosig.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_ENROLL_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_TRANSFER_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialSupplementInfoPeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsorySupplementInfoPeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils.getEnrollDate
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils.getExhaust
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils.getSeats
import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.URLENC

/**
 * 验证车型信息
 */
@Component
@Slf4j
@Newify([JsonSlurper])
class StepZeroOne implements IStep {

    private static final _API_PATH_STEP_ZERO_ONE = 'Net/netCarInfoControl!stepZeroOne.action'

    @Override
    run(context) {
        def transferDate = context.extendedAttributes?.transferDate
        def transferFlag = context.extendedAttributes?.transferFlag
        if (transferFlag && !transferDate) {
            return getSupplementInfoFSRV([_SUPPLEMENT_INFO_TRANSFER_DATE_TEMPLATE_QUOTING])
        }
        def enrollDate = getEnrollDate(context)
        if (!enrollDate) {
            return getSupplementInfoFSRV([_SUPPLEMENT_INFO_ENROLL_DATE_TEMPLATE_QUOTING])
        }

        def (commercialStartDateText) = getCommercialSupplementInfoPeriodTexts(context)
        def (compulsoryStartDateText) = getCompulsorySupplementInfoPeriodTexts(context)
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : ANY,
            path              : _API_PATH_STEP_ZERO_ONE,
            body              : [
                'paraMap.id'            : context.token,
                'paraMap.orgID'         : context.orgId,
                'paraMap.purgeCode'     : 'WB-OB-NR-FQ', // TODO gcc 该参数必须要,但是不知道从那获取值,暂时写死
                'paraMap.licence'       : context.auto.licensePlateNo,
                'paraMap.ownerName'     : context.auto.owner,
                'paraMap.engineNo'      : context.auto.engineNo,
                'paraMap.frameNo'       : context.auto.vinNo,
                'paraMap.idNo'          : context.auto.identity,
                'paraMap.transfer'      : transferDate ? '1' : '0',
                'paraMap.transferDate'  : transferDate ? _DATE_FORMAT3.format(transferDate) : '',
                'paraMap.isNew'         : '0',
                'paraMap.isLoanCar'     : '0',
                'paraMap.modelName'     : context.selectedCarModel.standardName.trim(),    //'东风日产DFL6460VECF多用途乘用车',
                'paraMap.vehicleFgwCode': context.selectedCarModel.vehicleFgwCode,  //'DFL6460VECF'
                'paraMap.exhaust'       : getExhaust(context),                  //'2.488'
                'paraMap.seat'          : getSeats(context),                    //'5'
                'paraMap.modelCode'     : context.selectedCarModel.rbCode,          //'RCAAID0014'
                'paraMap.enroll'        : enrollDate,
                'paraMap.insuApp'       : commercialStartDateText,
                'paraMap.insuAppTra'    : compulsoryStartDateText
            ] + getVehicleInfo(context)
        ]

        def result = client.post args, { resp, json ->
            json
        }


        if ('1' == result.paraMap.suc) {
            log.info '车辆信息校验成功'
            getContinueFSRV false
        } else if ('200' == result.paraMap.suc) {
            def vehiclesText = result.paraMap?.vehicles as String
            def carModeList = JsonSlurper().parseText(vehiclesText)
            context.selectCar = carModeList[0] // TODO 目前取第一辆,没见到有多辆的
            log.info '确认车辆信息，需要选择车型：{}', context.selectCar
            getContinueFSRV true
        } else {
            def message = result.paraMap?.result ?: result.paraMap?.message
            log.error '车辆信息校验失败：{}', message
            getFatalErrorFSRV message
        }
    }

    private getVehicleInfo(context) {
        context.selectedCarModel ? [
            'paraMap.agentCode'         : context.agentCode,
            'paraMap.queryModel'        : context.selectedCarModel?.queryModel,
            'paraMap.engineNo_'         : context.renewable ? context.auto.engineNo : '',
            'paraMap.frameNo_'          : context.renewable ? context.auto.vinNo : '',
            'paraMap.carCertificateType': '',
            'paraMap.carCertificateNo'  : '',
            'paraMap.carCertificateDate': '',
            'paraMap.firstBenefitPeople': '',
            'paraMap.phone'             : randomMobile
        ] : []
    }

}
