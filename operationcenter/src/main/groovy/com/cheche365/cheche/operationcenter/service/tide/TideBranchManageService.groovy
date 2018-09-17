package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.model.tide.TideBranch
import com.cheche365.cheche.core.repository.tide.TideBranchRepository
import com.cheche365.cheche.core.repository.tide.TideContractRepository
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.operationcenter.web.model.tide.ContractViewModel
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
 * Created by yinJianBin on 2018/4/19.
 */
@Service
@Slf4j
class TideBranchManageService extends BaseService {

    @Autowired
    TideContractRepository tideContractRepository
    @Autowired
    TideBranchRepository tideBranchRepository
    @Autowired
    InternalUserManageService internalUserManageService
    @Autowired
    TidePreconditionSpecification preconditionSpecification

    Page<TideBranch> getPage(ContractViewModel paramModel) {
        Pageable pageable = buildPageable(paramModel.currentPage, paramModel.pageSize, Sort.Direction.DESC, "id")
        return tideBranchRepository.findAll(new Specification<TideBranch>() {
            @Override
            Predicate toPredicate(Root<TideBranch> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                def predicateList = []
                if (paramModel.platformId) {
                    predicateList << cb.equal(root.get('tidePlatform').get('id'), paramModel.platformId)
                } else {
                    predicateList << preconditionSpecification.cratePlatformFilter(cb, root.get('tidePlatform').get('id'))
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates)
                return query.where(predicates).getRestriction()
            }
        }, pageable)
    }

    def getByPlatform(Long platformId) {
        def param = new ContractViewModel(
                platformId: platformId,
                currentPage: 1,
                pageSize: 1000
        )
        getPage(param).getContent()
    }
}
