package com.cheche365.cheche.scheduletask.service.insurance

import com.cheche365.cheche.common.math.NumberUtils
import com.cheche365.cheche.core.repository.InsurancePurchaseOrderRebateRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.manage.common.model.OfflineInsuranceCompanyImportData
import com.cheche365.cheche.manage.common.repository.OfflineInsuranceCompanyImportDataRepository
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import javax.transaction.Transactional

import static com.cheche365.cheche.core.util.BigDecimalUtil.add
import static com.cheche365.cheche.core.util.BigDecimalUtil.bigDecimalValue
import static com.cheche365.cheche.manage.common.model.OfflineInsuranceCompanyImportData.Enum.*

/**
 * #
 * Created by yinJianBin on 2017/12/14.
 */
@Service
@Slf4j
class OfflineDataMatchHandler {

    OfflineInsuranceCompanyImportDataRepository repository
    PurchaseOrderRepository purchaseOrderRepository
    InsurancePurchaseOrderRebateRepository insurancePurchaseOrderRebateRepository

    OfflineDataMatchHandler(OfflineInsuranceCompanyImportDataRepository repository, PurchaseOrderRepository purchaseOrderRepository, InsurancePurchaseOrderRebateRepository insurancePurchaseOrderRebateRepository) {
        this.repository = repository
        this.purchaseOrderRepository = purchaseOrderRepository
        this.insurancePurchaseOrderRebateRepository = insurancePurchaseOrderRebateRepository
    }

    @Transactional
    def matchByPolicyNo(String policyNo, List<OfflineInsuranceCompanyImportData> companyImportDatas) {
        def currentDate = new Date()
        def purchaseOrder = purchaseOrderRepository.findByQuoteRecordId(NumberUtils.toInt(companyImportDatas[0].description))
        def insurancePurchaseOrderRebate = insurancePurchaseOrderRebateRepository.findFirstByPurchaseOrder(purchaseOrder)
        //校验数据与泛华数据是否一致
        def errorDataList = []
        def sumAmount = companyImportDatas.sum { it.rebateAmount }
        insurancePurchaseOrderRebate.companyAmount = sumAmount as Double
        companyImportDatas.each {
            if (!purchaseOrder.auto.licensePlateNo.equals(it.licensePlateNo)) {
                it.errorMessage = '车牌号不一致,泛华数据车牌号:(' + purchaseOrder.auto.licensePlateNo + '),保险公司数据车牌号:(' + it.licensePlateNo + ')'
                log.info('车牌号不一致,保单号:({}),泛华数据车牌号:({}),保险公司数据车牌号:({})', policyNo, purchaseOrder.auto.licensePlateNo, it.licensePlateNo)
                it.status = STATUS_CHECK_FAILD
                errorDataList << it
            } else if (bigDecimalValue(insurancePurchaseOrderRebate.companyAmount).equals(add(insurancePurchaseOrderRebate.downCompulsoryAmount, insurancePurchaseOrderRebate.downCommercialAmount))) {
                it.status = STATUS_MATCH_FINISHED
            } else {
                it.status = STATUS_MATCH_SUCCESS
            }
            it.matchNum = it.matchNum + 1
            it.updateTime = currentDate
            it.purchaseOrder = purchaseOrder
            it.description = null

        }
        insurancePurchaseOrderRebateRepository.save(insurancePurchaseOrderRebate)
        repository.save(companyImportDatas)
        errorDataList
    }
}
