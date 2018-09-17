package com.cheche365.cheche.core.service.agent

import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.ApproveStatus
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.Ethnic
import com.cheche365.cheche.core.model.agent.ProfessionApprove
import com.cheche365.cheche.core.repository.ProfessionApproveRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.model.agent.ApproveStatus.Enum.*

/**
 * Author:   shanxf
 * Date:     2018/9/10 15:06
 */
@Service
@Slf4j
class ApproveService {

    @Autowired
    private ProfessionApproveRepository professionApproveRepository
    @Autowired
    private ChannelAgentRepository channelAgentRepository
    @Autowired
    private UserRepository userRepository

    @Transactional
    ChannelAgent approve(ChannelAgent existChannelAgent, String name, String identity, Ethnic ethnic) {

        log.info("代理人:{}资格认证姓名：{}，身份证：{}，民族：{}", existChannelAgent.id, name, identity, ethnic.name)
        if (ethnic != existChannelAgent.ethnic) {
            existChannelAgent.setEthnic(ethnic)
            existChannelAgent.setUpdateTime(new Date())
        }
        User user = existChannelAgent.getUser()
        if (name != user.name) {
            user.setName(name)
            user.setUpdateTime(new Date())
        }
        if (identity != user.identity) {
            user.setIdentity(identity)
            user.setUpdateTime(new Date())
        }
        existChannelAgent.setUser(user)
        userRepository.save(user)
        channelAgentRepository.save(existChannelAgent)

        ProfessionApprove professionApprove = professionApproveRepository.findByChannelAgent(existChannelAgent)
        if (!professionApprove) {
            professionApprove = new ProfessionApprove().with {
                it.channelAgent = existChannelAgent
                it
            }
        }
        professionApprove.setApproveStatus(TO_BE_APPROVE_2)
        professionApproveRepository.save(professionApprove)
        existChannelAgent
    }

    ApproveStatus caApproveStatus(ChannelAgent channelAgent){
        ProfessionApprove professionApprove = professionApproveRepository.findByChannelAgent(channelAgent)
        return professionApprove != null ? professionApprove.getApproveStatus() : NOT_APPROVE_1
    }
}
