package com.cheche365.cheche.rest.service.vl.formatter

import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.util.ValidationUtil
import com.cheche365.cheche.rest.service.auto.AutoModelService
import org.springframework.beans.factory.annotation.Autowired

abstract class VLFormatter {

    @Autowired
    AutoService autoService
    @Autowired
    AutoModelService autoModelService

    abstract support(context)

    abstract insuranceInfoToMap(context)

    def format(context) {

        if (needFilterVLMismatch(context)) {
            filterVLMismatch(context)
        }

        if (needFilterPublicAuto(context)) {
            filterPublicAuto(context)
        }

        if (needEncryptVL(context)) {
            encryptVL(context)
        }

        if (needFillAutoModels(context)) {
            fillAutoModels(context)
        }

        insuranceInfoToMap(context)
    }

    def needFilterVLMismatch(context) {
        true
    }

    def needFilterPublicAuto(context) {
        true
    }

    def needEncryptVL(context) {
        true
    }

    def needFillAutoModels(context) {
        true
    }

    def filterVLMismatch(context) {
        VehicleLicense vehicleLicense = context.iInfo?.vehicleLicense
        if (!context.user && vehicleLicense && vehicleLicense.owner != context.owner) {
            log.debug("输入的车主${context.owner}与数据库车主${vehicleLicense.owner}不一致")
            context.iInfo = null
        }
    }

    def filterPublicAuto(context) {
        VehicleLicense vehicleLicense = context.iInfo?.vehicleLicense
        if (vehicleLicense?.identity && !ValidationUtil.validIdentity(vehicleLicense.identity)) {
            log.debug('过滤掉查询到的公户车，证件号：{}', vehicleLicense.identity)
            context.iInfo = null
        }
    }

    def encryptVL(context) {
        VehicleLicense vehicleLicense = context.iInfo?.vehicleLicense
        if (vehicleLicense) {
            autoService.encryptVehicleLicense(context.request.session.id, vehicleLicense)
        }
    }

    def fillAutoModels(context) {
        def iInfo = context.iInfo
        if (iInfo?.vehicleLicense?.licensePlateNo?.trim() && iInfo?.vehicleLicense?.brandCode?.trim()) {
            log.debug("根据品牌型号${iInfo.vehicleLicense.brandCode}查找车型列表开始")

            long startTime = System.currentTimeMillis()
            def models = autoModelService.getAutoModels(iInfo.vehicleLicense, context.insuranceAreaId)
            long stopTime = System.currentTimeMillis()

            log.debug("根据品牌型号${iInfo.vehicleLicense.brandCode}查找车型列表耗时${(stopTime - startTime) / 1000}s,车型列表查询结果:${models}")
            iInfo.metaInfo = [
                "autoModel": models
            ]
        } else {
            log.debug('车牌号或品牌型号为空，跳过查找车型列表步骤')
        }
    }

}
