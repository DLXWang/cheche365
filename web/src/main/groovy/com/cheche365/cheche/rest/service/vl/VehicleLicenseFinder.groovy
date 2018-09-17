package com.cheche365.cheche.rest.service.vl

import com.cheche365.cheche.core.model.InsuranceInfo
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.repository.InsuranceInfoRepository
import com.cheche365.cheche.core.repository.VehicleLicenseRepository
import com.cheche365.cheche.core.service.spi.IVehicleLicenseFinder
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

/**
 * Created by wenling on 2017/11/30.
 */

@Service
@Order(3)
@Slf4j
class VehicleLicenseFinder implements IVehicleLicenseFinder {

    @Autowired
    VehicleLicenseRepository vlRepo
    @Autowired
    InsuranceInfoRepository insuranceInfoRepo

    @Override
    InsuranceInfo find(String licensePlateNo, String owner) {
        VehicleLicense internalVehicleLicense = vlRepo.findFirstByLicensePlateNoOrderByIdDesc(licensePlateNo)
        log.debug 'internalVehicleLicense === {}', internalVehicleLicense

        def insuranceInfo
        if (internalVehicleLicense) {
            insuranceInfo = insuranceInfoRepo.findFirstByVehicleLicense internalVehicleLicense
            if (!insuranceInfo) {
                insuranceInfo = new InsuranceInfo()
                insuranceInfo.vehicleLicense = internalVehicleLicense
            }
        } else {
            insuranceInfo = new InsuranceInfo()
        }
        insuranceInfo
    }

    @Override
    String name() {
        return '本地查询行驶证服务'
    }
}
