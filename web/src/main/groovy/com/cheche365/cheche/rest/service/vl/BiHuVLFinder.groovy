package com.cheche365.cheche.rest.service.vl

import com.cheche365.cheche.bihu.service.BihuInsuranceInfoService
import com.cheche365.cheche.core.exception.Constants
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.InsuranceBasicInfo
import com.cheche365.cheche.core.model.InsuranceInfo
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.repository.InsuranceBasicInfoRepository
import com.cheche365.cheche.core.repository.InsuranceInfoRepository
import com.cheche365.cheche.core.repository.VehicleLicenseRepository
import com.cheche365.cheche.core.service.InsurancePackageService
import com.cheche365.cheche.core.service.spi.IVehicleLicenseFinder
import com.cheche365.cheche.web.service.http.SessionScopeLogger
import groovy.util.logging.Slf4j
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.beans.BeanUtils
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.util.AutoUtils.getAreaOfAuto
import static com.cheche365.cheche.core.util.CacheUtil.toJSONPretty

/**
 * Created by zhengwei on 29/11/2017.
 */

@Service
@Order(1)
@Slf4j
class BiHuVLFinder implements IVehicleLicenseFinder {

    VehicleLicenseRepository vehicleLicenseRepo
    InsuranceInfoRepository insuranceInfoRepo
    InsuranceBasicInfoRepository insuranceBasicInfoRepo
    InsurancePackageService insurancePackageService
    BihuInsuranceInfoService biHuInsuranceInfoService
    SessionScopeLogger logger

    BiHuVLFinder(VehicleLicenseRepository vehicleLicenseRepo,
                 InsuranceInfoRepository insuranceInfoRepo,
                 InsuranceBasicInfoRepository insuranceBasicInfoRepo,
                 InsurancePackageService insurancePackageService,
                 BihuInsuranceInfoService biHuInsuranceInfoService,
                 SessionScopeLogger logger) {
        this.vehicleLicenseRepo = vehicleLicenseRepo
        this.insuranceInfoRepo = insuranceInfoRepo
        this.insuranceBasicInfoRepo = insuranceBasicInfoRepo
        this.insurancePackageService = insurancePackageService
        this.biHuInsuranceInfoService = biHuInsuranceInfoService
        this.logger = logger
    }

    @Override
    InsuranceInfo find(String licensePlateNo, String owner) {

        def auto = new Auto(licensePlateNo: licensePlateNo, owner: owner)
        InsuranceInfo externalInsuranceInfo = null
        try {
            externalInsuranceInfo = biHuInsuranceInfoService.getInsuranceInfo(getAreaOfAuto(licensePlateNo), auto, [:])

            log.debug("使用壁虎服务拉取回来的VL{}", externalInsuranceInfo)

            if (externalInsuranceInfo?.vehicleLicense) {

                VehicleLicense ccVehicleLicense = persistVL(externalInsuranceInfo, licensePlateNo)
                log.debug("保存行驶本信息:{}", toJSONPretty(ccVehicleLicense))

                if (externalInsuranceInfo?.insuranceBasicInfo) {
                    InsuranceInfo ccInsuranceInfo = persistInsuranceInfo(externalInsuranceInfo, ccVehicleLicense)
                    log.debug("保存行驶本关联的保单信息:{}", toJSONPretty(ccInsuranceInfo))
                }
            }
        } catch (Exception e) {
            log.debug("处理壁虎服务拉取回来的VL出现异常: {}，exception: {}", externalInsuranceInfo, ExceptionUtils.getFullStackTrace(e))
        }

        externalInsuranceInfo
    }

    private InsuranceInfo persistInsuranceInfo(InsuranceInfo externalInsuranceInfo, VehicleLicense ccVehicleLicense) {
        def ccInsuranceInfo = insuranceInfoRepo.findFirstByVehicleLicense ccVehicleLicense
        if (!ccInsuranceInfo) {
            ccInsuranceInfo = new InsuranceInfo()
            ccInsuranceInfo.vehicleLicense = ccVehicleLicense
        }

        def ccInsuranceBasicInfo = ccInsuranceInfo.insuranceBasicInfo
        if (!ccInsuranceBasicInfo) {
            ccInsuranceBasicInfo = new InsuranceBasicInfo()
        }

        def insuranceBasicInfo = externalInsuranceInfo?.insuranceBasicInfo
        insuranceBasicInfo.id = null
        InsuranceBasicInfo.PROPERTIES.findAll { !Constants.EXCEPT_FIELDS.contains(it.name) }.each {
            if (insuranceBasicInfo."$it.name") {
                ccInsuranceBasicInfo."$it.name" = insuranceBasicInfo."$it.name"
            }
        }

        if (ccInsuranceBasicInfo.insurancePackage) {
            ccInsuranceBasicInfo.insurancePackage = insurancePackageService.mergeInsurancePackage(ccInsuranceBasicInfo.insurancePackage)
        }
        insuranceBasicInfoRepo.save(ccInsuranceBasicInfo)

        ccInsuranceInfo.insuranceBasicInfo = ccInsuranceBasicInfo
        insuranceInfoRepo.save(ccInsuranceInfo)
    }

    private VehicleLicense persistVL(InsuranceInfo externalInsuranceInfo, String licensePlateNo) {
        def vehicleLicense = externalInsuranceInfo.vehicleLicense
        boolean isFromCache = externalInsuranceInfo.metaInfo?.isFromCache
        int hours = externalInsuranceInfo.metaInfo?.expireTTLInSeconds / 60 / 60 as int
        log.debug("使用壁虎服务命中行驶证,${!isFromCache ? "未" : ""}命中缓存,${hours}小时后缓存到期", toJSONPretty(externalInsuranceInfo.metaInfo.rawResult))

        VehicleLicense ccVehicleLicense = this.vehicleLicenseRepo.findFirstByLicensePlateNoOrderByIdDesc(licensePlateNo)
        if (ccVehicleLicense) {
            vehicleLicense.id = null
            VehicleLicense.PROPERTIES.findAll { !Constants.EXCEPT_FIELDS.contains(it.name) }.each {
                if (vehicleLicense."$it.name") {
                    ccVehicleLicense."$it.name" = vehicleLicense."$it.name"
                }
            }
        } else {
            ccVehicleLicense = new VehicleLicense()
            BeanUtils.copyProperties(vehicleLicense, ccVehicleLicense)
        }
        vehicleLicenseRepo.save(ccVehicleLicense)
    }

    @Override
    String name() {
        return '壁虎行驶证服务'
    }
}
