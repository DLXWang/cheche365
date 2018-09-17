package com.cheche365.cheche.rest.service.vl

import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.core.service.spi.IVehicleLicenseFinder
import com.unionpay.acp.gwj.util.DateUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import javax.servlet.http.HttpSession

/**
 * Created by wenling on 2017/11/30.
 */

@Service
@Order(2)
@Slf4j
class InsuranceVLFinder implements IVehicleLicenseFinder {

    @Autowired
    PurchaseOrderRepository poRepo;
    @Autowired
    InsuranceRepository iRepo;
    @Autowired
    QuoteRecordRepository qrRepo
    @Autowired
    CompulsoryInsuranceRepository ciRepo;
    @Autowired
    HttpSession session;

    @Override
    InsuranceInfo find(String licensePlateNo, String owner) {


        List<PurchaseOrder> purchaseOrders=poRepo.findByLicensePlateNo(licensePlateNo)

        if(!purchaseOrders){
            log.debug '保单未命中行驶证'
            return
        }


        def orderIds=[]
        purchaseOrders.each {
            orderIds << it.id
        }

        Insurance  insurance=iRepo.findInsurancesByOrder(orderIds)
        CompulsoryInsurance  compulsoryInsurance=ciRepo.findCompulsoryInsurancesByOrder(orderIds)
        InsuranceInfo info

        if (insurance || compulsoryInsurance) {
            Auto auto = compulsoryInsurance ? compulsoryInsurance.auto : insurance.auto

            VehicleLicense vl = VehicleLicense.createVLByAuto(auto)

            InsuranceBasicInfo insuranceBasicInfo = new InsuranceBasicInfo(
                commercialStartDate: (insurance?.expireDate ? DateUtil.addDate(insurance.expireDate, 1) : null),
                compulsoryStartDate: (compulsoryInsurance?.expireDate ? DateUtil.addDate(compulsoryInsurance.expireDate, 1) : null)
            )

            info = new InsuranceInfo(
                vehicleLicense: vl,
                insuranceBasicInfo: insuranceBasicInfo
            )
        }

        log.debug 'internalVehicleLicense === {}', info
        info
    }

    @Override
    String name() {
        return '保单查询行驶证服务'
    }
}
