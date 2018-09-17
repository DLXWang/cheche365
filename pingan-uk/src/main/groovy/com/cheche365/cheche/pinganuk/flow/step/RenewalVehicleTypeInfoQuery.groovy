package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.core.model.Auto
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.Constants._DATE_FORMAT2
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.getEnrollDate
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.resolveAutoLicensePlate

/**
 * 续保车型查询
 * Created by wangxiaofei on 2016.8.29
 */
@Component
@Slf4j
class RenewalVehicleTypeInfoQuery extends AVehicleTypeInfoQuery {

    @Override
    protected getRequestParams(context) {
        Auto auto = context.auto
        def vehicleTarget = context.voucher.vehicleTarget

        def requestParams = [
            voucher            : [
                c51BaseInfo  : context.voucher.c51BaseInfo,
                saleInfo     : context.voucher.saleInfo,
                vehicleTarget: context.voucher.vehicleTarget + [
                    firstRegisterDate: getEnrollDate(context)
                ]
            ],
            circVehicleTypeInfo: [
                departmentCode    : context.baseInfo.departmentCode,
                firstRegisterDate : getEnrollDate(context, _DATE_FORMAT2),
                vehicleLicenceCode: resolveAutoLicensePlate(auto.licensePlateNo),
                vehicleFrameNo    : auto.vinNo ?: vehicleTarget.vehicleFrameNo,
                engineNo          : auto.engineNo ?: vehicleTarget.engineNo,
                licenceTypeCode   : vehicleTarget.licenceTypeCode
            ]
        ]

        new JsonBuilder(requestParams).toString()
    }

}
