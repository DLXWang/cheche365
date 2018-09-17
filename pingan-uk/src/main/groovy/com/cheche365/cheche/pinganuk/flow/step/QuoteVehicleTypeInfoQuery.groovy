package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.core.model.Auto
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.Constants._DATE_FORMAT2
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.getDefaultInsurancePeriodTexts
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.getEnrollDate
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.resolveAutoLicensePlate

/**
 * 转保车型查询
 * Created by wangxiaofei on 2016.8.29
 */
@Component
@Slf4j
class QuoteVehicleTypeInfoQuery extends AVehicleTypeInfoQuery {

    @Override
    protected getRequestParams(context) {
        Auto auto = context.auto
        def baseInfo = context.baseInfo
        def vehicleData = context.vehicleDataList[0]// TODO 认为车辆查询只返回一辆

        def (startDateText, endDateText) = getDefaultInsurancePeriodTexts()
        def requestParams = [
            voucher            : [
                c51BaseInfo  : [
                    calculateResult   : [
                        beginTime: startDateText + ' 00:00:00',
                        endTime  : endDateText + ' 23:59:59'
                    ],
                    insuranceBeginTime: startDateText,
                    insuranceEndTime  : endDateText
                ],
                saleInfo     : [
                    departmentCode          : baseInfo.departmentCode,
                    businessSourceCode      : baseInfo.businessSourceCode,
                    businessSourceDetailCode: baseInfo.businessSourceDetailCode,
                    channelSourceCode       : baseInfo.channelSourceCode,
                    channelSourceDetailCode : baseInfo.channelSourceDetailCode,
                    saleAgentCode           : baseInfo.saleAgentCode
                ],
                vehicleTarget: [
                    firstRegisterDate     : getEnrollDate(context),
                    vehicleLicenceCode    : resolveAutoLicensePlate(auto.licensePlateNo),
                    engineNo              : auto.engineNo ?: vehicleData.engineNo,
                    vehicleFrameNo        : auto.vinNo ?: vehicleData.vehicleFrameNo,
                    autoModelCode         : context.selectedCarModel.autoModelCode,
                    vehicleTypeCode       : context.selectedCarModel.vehicleTypeNew,
                    ownerVehicleTypeCode  : vehicleData.ownerVehicleTypeCode,
                    usageAttributeCode    : '02',
                    ownershipAttributeCode: '03'
                ]
            ],
            circVehicleTypeInfo: [
                departmentCode    : baseInfo.departmentCode,
                vehicleFrameNo    : auto.vinNo ?: vehicleData.vehicleFrameNo,
                vehicleLicenceCode: resolveAutoLicensePlate(auto.licensePlateNo) ?: vehicleData.vehicleLicenceCode,
                engineNo          : auto.engineNo ?: vehicleData.engineNo,
                licenceTypeCode   : vehicleData.licenceTypeCode,
                firstRegisterDate : getEnrollDate(context, _DATE_FORMAT2)
            ]
        ]
        new JsonBuilder(requestParams).toString()
    }
}
