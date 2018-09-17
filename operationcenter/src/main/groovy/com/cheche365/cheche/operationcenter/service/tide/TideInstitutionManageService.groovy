package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.model.tide.TideBranch
import com.cheche365.cheche.core.model.tide.TideInstitution
import com.cheche365.cheche.core.repository.tide.TideInstitutionRepository
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.constants.TideConstants
import com.cheche365.cheche.operationcenter.web.model.tide.InstitutionViewModel
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
class TideInstitutionManageService extends BaseService {

    @Autowired
    TideInstitutionRepository tideInstitutionRepository
    @Autowired
    InternalUserManageService internalUserManageService
    @Autowired
    TidePreconditionSpecification preconditionSpecification

    Page<TideInstitution> getPage(InstitutionViewModel paramModel) {
        Pageable pageable = buildPageable(paramModel.currentPage, paramModel.pageSize, Sort.Direction.DESC, "id")
        return tideInstitutionRepository.findAll(new Specification<TideInstitution>() {
            @Override
            Predicate toPredicate(Root<TideInstitution> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                def predicateList = []
                if (paramModel.platformId) {
                    predicateList << cb.equal(root.get('tideBranch').get('tidePlatform').get('id'), paramModel.platformId)
                } else {
                    predicateList << preconditionSpecification.cratePlatformFilter(cb, root.get('tideBranch').get('tidePlatform').get('id'))
                }
                if (paramModel.branchId) {
                    predicateList << cb.equal(root.get('tideBranch').get('id'), paramModel.branchId)
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates)
                return query.where(predicates).getRestriction()
            }
        }, pageable)
    }

    def getByBranch(Long branchId, Long platformId) {
        def viewModel = new InstitutionViewModel(
                platformId: platformId,
                branchId: branchId,
                currentPage: 1,
                pageSize: 1000
        )
        getPage(viewModel).getContent()
    }

    def save(InstitutionViewModel viewModel) {
        TideInstitution tideInstitution = new TideInstitution(
                tideBranch: new TideBranch(id: viewModel.branchId as Long),
                institutionName: viewModel.institutionName,
                status: TideConstants.STATUS_EFFECTIVE_ING,
                description: viewModel.description,
                operator: internalUserManageService.getCurrentInternalUser()
        )
        tideInstitutionRepository.save(tideInstitution)
    }

    def getByInstitutionNameAndBranchId(String institutionName, Long branchId) {
        tideInstitutionRepository.findAllByInstitutionNameAndTideBranchId(institutionName, branchId)
    }
}
