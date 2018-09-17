package com.cheche365.cheche.pingan.flow.step.m

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3

/**
 * 续保保存车辆信息
 * Created by wangxin on 2015/11/4.
 */
@Component
@Slf4j
class RenewalSaveQuoteInfo extends ASaveQuoteInfo {
    @Override
    generateParams(context) {
        def vehicleInfo = context.renewalVehicleInfo
        def registerInfo = context.renewalRegisterInfo
        def transferDate = context.extendedAttributes?.transferDate
        log.info '初登日期：{}', vehicleInfo.'vehicle.registerDate'
        [
            'vehicle.registerDate'  : vehicleInfo.'vehicle.registerDate',
            'vehicle.model'         : vehicleInfo.'vehicle.model',
            'vehicle.modelName'     : vehicleInfo.'vehicle.modelName',
            'vehicle.vehicleId'     : vehicleInfo.'vehicle.vehicleId',
            'register.name'         : registerInfo.'register.name',
            'register.idType'       : '01',//默认是身份证
            'register.birthday'     : registerInfo.'register.birthday',
            'register.gender'       : registerInfo.'register.gender',
            'bizInfo.specialCarFlag': transferDate ? 1 : 0,
            'bizInfo.specialCarDate': transferDate ? _DATE_FORMAT3.format(transferDate) : '',
            'flowId'                : context.flowId,
            'bizConfig.pkgName'     : 'renewal',//续保默认是renewal
            '__xrc'                 : context.__xrc
        ]

    }

}
