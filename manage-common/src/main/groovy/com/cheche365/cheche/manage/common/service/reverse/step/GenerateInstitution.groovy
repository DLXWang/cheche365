package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.core.model.Institution
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by yellow on 2017/11/30.
 */
@Service
@Slf4j
class GenerateInstitution implements TPlaceInsuranceStep {
    @Override
    @Transactional
    Object run(Object context) {
        log.debug("------生成出单机构------")
        OrderReverse model = context.model
        Institution institution = context.institutionRepository.findFirstByName(model.institutionName)
        if (institution == null) {
            institution = new Institution()
            institution.setName(model.institutionName)
            institution.setInstitutionType(model.institutionType)
            institution.setCreateTime(new Date())
            context.institutionRepository.save(institution)
        }
        getContinueFSRV true
    }
}
