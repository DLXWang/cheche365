package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.model.tide.TideRebateRecord
import com.cheche365.cheche.core.repository.tide.TideRebateRecordRepository
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.operationcenter.web.model.tide.RebateViewModel
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
class TideRebateRecordService extends BaseService {
    @Autowired
    private TidePreconditionSpecification preconditionSpecification
    @Autowired
    private TideRebateRecordRepository rebateRecordRepository

    Page<TideRebateRecord> getRebateByPage(RebateViewModel paramModel) {
        Pageable pageable = buildPageable(paramModel.currentPage, paramModel.pageSize, Sort.Direction.DESC, "id")

        return rebateRecordRepository.findAll(new Specification<TideRebateRecord>() {
            @Override
            Predicate toPredicate(Root<TideRebateRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                def predicate = cb.conjunction()
                def expressions = predicate.getExpressions()

                expressions << preconditionSpecification.cratePlatformFilter(cb, root.get('tideContract').get('tideBranch').get('tidePlatform').get('id'))

                if (paramModel.contractId) {
                    expressions << cb.equal(root.get('tideContract').get('id'), paramModel.contractId)
                }
                if (paramModel.supportAreaId) {
                    expressions << cb.equal(root.get('supportArea').get('id'), paramModel.supportAreaId)
                }
                if (paramModel.status != null) {
                    expressions << cb.equal(root.get('status'), paramModel.status)
                }
                if (paramModel.insuranceType) {
                    expressions << cb.equal(root.get('insuranceType'), paramModel.insuranceType as Long)
                }
                if (paramModel.carType) {
                    expressions << cb.equal(root.get('carType'), paramModel.carType as Long)
                }

                if (paramModel.createTime) {
                    expressions << cb.equal(root.get('createTime'), paramModel.createTime)
                }

                return predicate
            }
        }, pageable)
    }

}
