package com.cheche365.cheche.manage.common.service.offlinedata

import com.cheche365.cheche.core.model.InsuranceInfo
import com.cheche365.cheche.core.repository.InsuranceBasicInfoRepository
import com.cheche365.cheche.core.repository.InsuranceInfoRepository
import com.cheche365.cheche.core.repository.VehicleLicenseRepository
import com.cheche365.cheche.core.service.InsurancePackageService
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW

/**
 * 线下数据保险信息服务
 * Created by suyaqiang on 2017/10/23.
 */
@Slf4j
@Service
class OfflineDataInsuranceInfoService implements IOfflineDataInsuranceInfoService {

    private VehicleLicenseRepository vehicleLicenseRepo
    private InsuranceInfoRepository insuranceInfoRepository
    private InsuranceBasicInfoRepository insuranceBasicInfoRepository
    private InsurancePackageService insurancePackageService

    OfflineDataInsuranceInfoService(VehicleLicenseRepository vehicleLicenseRepo,
                                    InsuranceInfoRepository insuranceInfoRepository,
                                    InsuranceBasicInfoRepository insuranceBasicInfoRepository,
                                    InsurancePackageService insurancePackageService) {
        this.vehicleLicenseRepo = vehicleLicenseRepo
        this.insuranceInfoRepository = insuranceInfoRepository
        this.insuranceBasicInfoRepository = insuranceBasicInfoRepository
        this.insurancePackageService = insurancePackageService
    }

    @Transactional(propagation = REQUIRES_NEW)
    InsuranceInfo saveInsuranceInfo(insuranceInfo) {
        vehicleLicenseRepo.save insuranceInfo.vehicleLicense
        log.debug '保存行驶本信息，{}，', insuranceInfo.vehicleLicense

        if (insuranceInfo.insuranceBasicInfo) {
            if (insuranceInfo.insuranceBasicInfo?.insurancePackage) {
                log.debug '保存获取的续保套餐'
                insuranceInfo.insuranceBasicInfo.insurancePackage = insurancePackageService.mergeInsurancePackage(insuranceInfo.insuranceBasicInfo.insurancePackage)
            }
            log.debug '保存获取的续保套餐、起保时间等信息, {}', insuranceInfo.insuranceBasicInfo
            insuranceBasicInfoRepository.save insuranceInfo.insuranceBasicInfo
        }
        insuranceInfoRepository.save insuranceInfo
    }

}

