package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.tide.TideContract
import com.cheche365.cheche.core.model.tide.TideContractHistory
import com.cheche365.cheche.core.repository.tide.TideContractHistoryRepository
import com.cheche365.cheche.manage.common.constants.TideConstants
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.operationcenter.web.model.tide.ContractHistoryViewModel
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

/**
 * Created by yinJianBin on 2018/5/5.
 */
@Service
@Slf4j
class TideContractHistoryManageService extends BaseService {

    @Autowired
    TideContractHistoryRepository historyRepository
    @Autowired
    TidePreconditionSpecification preconditionSpecification


    Page<TideContractHistory> getPage(ContractHistoryViewModel paramModel) {
        Pageable pageable = buildPageable(paramModel.currentPage, paramModel.pageSize, Sort.Direction.DESC, "id")
        return historyRepository.findAll(new Specification<TideContractHistory>() {
            @Override
            Predicate toPredicate(Root<TideContractHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                def predicateList = []
                if (paramModel.platformId) {
                    predicateList << cb.equal(root.get('tideContract').get('tideBranch').get('tidePlatform').get('id'), paramModel.platformId)
                } else {
                    predicateList << preconditionSpecification.cratePlatformFilter(cb, root.get('tideContract').get('tideBranch').get('tidePlatform').get('id'))
                }
                if (paramModel.contractId) {
                    predicateList << cb.equal(root.get('tideContract').get('id'), paramModel.contractId)
                }
                if (paramModel.operationType) {
                    predicateList << cb.equal(root.get('operationType'), paramModel.operationType)
                }
                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return query.where(predicates).getRestriction()
            }
        }, pageable)
    }

    def add(TideContract tideContract, InternalUser operator, Integer operationType) {
        TideContractHistory history = TideContractHistory.copyFromContract(tideContract)
        history.setOperator(operator)
        history.setOperationType(operationType)
        history.setUpdateTime(new Date())
        historyRepository.save(history)
    }

    def renewal(TideContract tideContract, Date effectiveDate, Date expireDate, InternalUser operator) {
        TideContractHistory history = TideContractHistory.copyFromContract(tideContract)
        history.setOperator(operator)
        history.setOperationType(TideConstants.OPERATION_TYPE_RENEWAL)
        history.setUpdateTime(new Date())
        historyRepository.save(history)
    }


    def getViewModelByContractId(Long contractId, Integer operationType) {
        def paramModel = new ContractHistoryViewModel(
                contractId: contractId,
                operationType: operationType,
                pageSize: 1000,
                currentPage: 1
        )
        def list = getPage(paramModel).getContent()
        list.collect() {
            ContractHistoryViewModel.buildViewData(it)
        }
    }

}
