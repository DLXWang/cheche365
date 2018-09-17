package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT1
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.picc.util.BusinessUtils.getNextDays4Commercial
import static com.cheche365.cheche.picc.util.BusinessUtils.isTextWithAsteriskSame
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取续保信息
 */
@Component
@Slf4j
class GetRenewalInfo implements IStep {


    @Override
    run(context) {

        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : "/newecar${context.trafficInfo.resultUrl}",
            body              : [
                uniqueID: context.uniqueID
            ]
        ]

        def inputs = client.post args, { resp, text ->
            htmlParser.parse(text).depthFirst().INPUT
        }

        def (vehicleInfo, insuredInfo) = [
            ['frameNo', 'engineNo', 'enrollDate', 'startDateBI', 'endDateBI', 'VEHICLE_MODELSH', 'SeatCount', 'haveOwnerChange', 'carModelDetail'],
            ['insuredName', 'insuredIDNumber', 'insuredIdentifyAddr', 'insuredSex', 'insuredBirthday', 'insuredMobile', 'insuredEmail']]*.collectEntries { id ->
            [(id): inputs.find { input -> input.@(id) == id }?.@value]
        }

        context.renewalVehicleInfo = vehicleInfo
        context.vehicleInfo = vehicleInfo
        context.insuredInfo = insuredInfo
        Auto auto = context.auto

        def renewalVehicleInfo = context.renewalVehicleInfo

        if (context.quoting) {
            setCommercialInsurancePeriodTexts context, renewalVehicleInfo?.startDateBI, _DATETIME_FORMAT1, getNextDays4Commercial(context)
            getContinueFSRV renewalVehicleInfo
        } else {
            if (isTextWithAsteriskSame(auto.vinNo, renewalVehicleInfo?.frameNo)
                && isTextWithAsteriskSame(auto.engineNo, renewalVehicleInfo?.engineNo)) {
                setCommercialInsurancePeriodTexts context, renewalVehicleInfo?.startDateBI, _DATETIME_FORMAT1, getNextDays4Commercial(context)
                getContinueFSRV renewalVehicleInfo
            } else {
                getFatalErrorFSRV '保险公司返回车架与发动机号与用户所填不一致,核保失败'
            }
        }
    }

}
