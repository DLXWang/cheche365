package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.core.model.Auto
import org.springframework.stereotype.Component

/**
 * 北京地区部分车需要‘车架号’，‘发动机号‘两个参数，目前发现包括转保车
 * Created by wangxiaofei on 2016/9/28.
 */
@Component
class SubmitVehicleBaseInfoNonRenewal extends ASubmitVehicleBaseInfo {

    @Override
    protected getSpecialRequestParameters(context) {
        Auto auto = context.auto

        [
            VehicleInfo        : [
                vin      : auto.vinNo,
                engineNo : auto.engineNo
            ]
        ]
    }

}
