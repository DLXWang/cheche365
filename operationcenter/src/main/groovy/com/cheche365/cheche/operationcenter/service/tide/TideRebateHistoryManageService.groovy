package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.model.tide.TideContractRebate
import com.cheche365.cheche.core.model.tide.TideContractRebateHistory
import com.cheche365.cheche.core.repository.tide.TideContractRebateHistoryRepository
import com.cheche365.cheche.core.repository.tide.TideContractRebateRepository
import com.cheche365.cheche.manage.common.model.PublicQuery
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.annotation.TideLogAnnotation
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Service
@Slf4j
class TideRebateHistoryManageService extends BaseService {

    @Autowired
    private TideContractRebateHistoryRepository rebateHistoryRepository
    @Autowired
    private TideContractRebateRepository rebateRepository
    @Autowired
    private InternalUserManageService internalUserManageService

    Page<TideContractRebateHistory> getRebateHistoryByPage(PublicQuery paramModel) {
        Pageable pageable = buildPageable(paramModel.currentPage, paramModel.pageSize, Sort.Direction.DESC, "id")

        return rebateHistoryRepository.findAll(new Specification<TideContractRebateHistory>() {
            @Override
            Predicate toPredicate(Root<TideContractRebateHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                def predicate = cb.conjunction()
                def expressions = predicate.getExpressions()
                if (paramModel.keyword) {
                    expressions << cb.equal(root.get('contractRebate'), paramModel.keyword as Long)
                }
                return predicate
            }
        }, pageable)
    }

    @TideLogAnnotation(table = "tide_contract_rebate_history", description = "记录点位历史")
    def add(TideContractRebate rebate) {
        def rebateHis = new TideContractRebateHistory()

        rebateHis.contractRebate = rebate.id
        rebateHis.contractRebateCode = rebate.contractRebateCode
        rebateHis.tideContract = rebate.tideContract
        rebateHis.supportArea = rebate.supportArea
        rebateHis.insuranceType = rebate.insuranceType
        rebateHis.carType = rebate.carType
        rebateHis.chooseCondition = rebate.chooseCondition
        rebateHis.effectiveDate = rebate.effectiveDate
        rebateHis.expireDate = rebate.expireDate
        rebateHis.originalCommecialRate = rebate.originalCommecialRate
        rebateHis.originalCompulsoryRate = rebate.originalCompulsoryRate
        rebateHis.autoTaxReturnType = rebate.autoTaxReturnType
        rebateHis.autoTaxReturnValue = rebate.autoTaxReturnValue
        rebateHis.marketCommercialRate = rebate.marketCommercialRate
        rebateHis.marketCompulsoryRate = rebate.marketCompulsoryRate
        rebateHis.marketAutoTaxReturnType = rebate.marketAutoTaxReturnType
        rebateHis.marketAutoTaxReturnValue = rebate.marketAutoTaxReturnValue
        rebateHis.operator = rebate.operator
        rebateHis.description = rebate.description
        rebateHis.modifyer = internalUserManageService.getCurrentInternalUser()

        rebateHistoryRepository.save(rebateHis)
    }

}
