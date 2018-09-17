package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.User
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
class GenerateUser implements TPlaceInsuranceStep {

    @Override
    @Transactional
    Object run(Object context) {
        log.debug("------生成用户------")
        OrderReverse model = context.model
        if (model.applicantDate == null) model.applicantDate = new Date()
        User user = new User()
        Area area = context.areaRepository.findOne(model.getArea())
        user.setArea(area)
        user.setIdentityType(model.identityType)
        user.setCreateTime(model.applicantDate)
        user.setName(model.agentName)
        user.setUserType(model.userType)
        user.setSourceType(model.orderSourceType)
        context.userRepository.save(user)
        context.user = user
        getContinueFSRV true
    }
}
