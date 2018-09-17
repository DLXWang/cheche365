package com.cheche365.cheche.rest.service.vl.formatter

import com.cheche365.cheche.core.serializer.SerializerUtil
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
@Order(7)
@Slf4j
class OldVLFormatter extends VLFormatter {

    @Override
    def support(context) {
        Boolean.TRUE
    }

    @Override
    def needFillAutoModels(Object context) {
        false
    }

    @Override
    def insuranceInfoToMap(context) {
        log.debug("不支持简化报价流程 VL使用VehicleLicense结构}")
        SerializerUtil.formatVehicleLicense(context.iInfo?.vehicleLicense)
    }

}
