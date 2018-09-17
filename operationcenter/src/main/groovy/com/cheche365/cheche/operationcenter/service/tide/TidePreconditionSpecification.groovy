package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.repository.tide.TidePlatformInternalUserRepository
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Predicate
import javax.servlet.http.HttpSession

@Service
@Slf4j
class TidePreconditionSpecification {
    @Autowired
    private TidePlatformInternalUserRepository platformInternalUserRepository
    @Autowired
    private InternalUserManageService internalUserManageService
    @Autowired
    private HttpSession session

    Predicate cratePlatformFilter(CriteriaBuilder cb, Expression platFormPath) {
        def platformList = (Iterable) session.getAttribute('platformList')
        if (!platformList) {
            platformList = platformInternalUserRepository.findAllByInternalUserId(internalUserManageService.currentInternalUser.id).collect {
                it.tidePlatform?.id
            }
            session.setAttribute('platformList', platformList)
        }

        if (platformList && platformList.size() > 0) {
            CriteriaBuilder.In platformIdIn = cb.in(platFormPath)
            platformList.each {
                platformIdIn.value(it)
            }
            return platformIdIn
        } else {
            return cb.isTrue(cb.literal(false))
        }
    }
}
