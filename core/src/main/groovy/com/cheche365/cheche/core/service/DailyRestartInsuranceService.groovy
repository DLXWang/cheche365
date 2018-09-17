package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.DailyRestartInsurance
import com.cheche365.cheche.core.model.DailyRestartInsuranceDetail
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.DailyRestartInsuranceDetailRepository
import com.cheche365.cheche.core.repository.DailyRestartInsuranceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by mahong on 2016/12/24.
 */
@Service
class DailyRestartInsuranceService {

    @Autowired
    private DailyRestartInsuranceRepository restartInsuranceRepository
    @Autowired
    private DailyRestartInsuranceDetailRepository restartInsuranceDetailRepository

    @Transactional
    @CacheEvict(value = 'dailyInsuranceData', allEntries = true, beforeInvocation = true, condition = '#restartInsurance.status.id==5')
    DailyRestartInsurance saveDailyRestartInsurance(DailyRestartInsurance restartInsurance) {
        restartInsuranceRepository.save(restartInsurance)
    }

    @Transactional
    @CacheEvict(value = 'dailyInsuranceData', allEntries = true, beforeInvocation = true, condition = '#restartInsurance.status.id==5')
    DailyRestartInsurance saveDailyRestartInsuranceCascade(DailyRestartInsurance restartInsurance, List<DailyRestartInsuranceDetail> details) {
        saveDailyRestartInsurance(restartInsurance)

        restartInsuranceDetailRepository.deleteByDailyRestartInsurance(restartInsurance)
        restartInsurance.restartInsuranceDetails = null
        details.each { it.dailyRestartInsurance = restartInsurance }
        restartInsurance.restartInsuranceDetails = restartInsuranceDetailRepository.save(details)
        restartInsurance
    }

    DailyRestartInsurance findByPayment(Payment payment) {
        restartInsuranceRepository.findFirstByPayment(payment)
    }

    DailyRestartInsurance findLastByPurchaseOrder(PurchaseOrder purchaseOrder) {
        restartInsuranceRepository.findLastByPurchaseOrder(purchaseOrder)
    }

}
