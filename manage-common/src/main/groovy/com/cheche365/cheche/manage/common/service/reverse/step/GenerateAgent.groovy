package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.core.model.Agent
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
class GenerateAgent implements TPlaceInsuranceStep {
    @Transactional
    @Override
    Object run(Object context) {
        log.debug("------生成代理人------")
        OrderReverse model = context.model
        Agent agent = model.agentIdentity != null ? context.agentRepository.findByIdentity(model.agentIdentity) : null
        if (agent == null) {
            User user = context.user
            agent = new Agent()
            agent.setAgentType(model.agentType)
            agent.setEnable(true)
            agent.setCreateTime(model.applicantDate)
            agent.setName(user.name)
            agent.setMobile(user.mobile)
            agent.setUser(user)
            agent.setComment(model.comment)
            agent.setIdentityType(model.identityType)
            agent.setIdentity(model.getAgentIdentity())
            agent.setOperator(model.operator)
            agent.setRebate(0)
            agent = context.agentRepository.save(agent)
        }
        model.recommender = model.recommender == null ? agent.id : model.recommender
        getContinueFSRV true
    }
}
