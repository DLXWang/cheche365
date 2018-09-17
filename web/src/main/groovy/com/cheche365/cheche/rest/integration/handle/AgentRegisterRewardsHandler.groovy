package com.cheche365.cheche.rest.integration.handle

import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.ProfessionApprove
import com.cheche365.cheche.core.repository.ProfessionApproveRepository
import com.cheche365.cheche.wallet.service.WalletTradeService
import com.cheche365.cheche.web.integration.IIntegrationHandler
import com.cheche365.cheche.web.model.Message
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.agent.ApproveStatus.Enum.NOT_APPROVE_1

/**
 * Created by liheng on 2018/8/31 0031.
 */
@Service
@Slf4j
class AgentRegisterRewardsHandler implements IIntegrationHandler<Message<ChannelAgent>> {

    @Autowired
    private WalletTradeService walletTradeService

    @Autowired
    ProfessionApproveRepository professionApproveRepository

    @Override
    Message<ChannelAgent> handle(Message<ChannelAgent> message) {

        log.info("开始结算channelAgent:{},注册成功奖励金", message.payload.id)
        walletTradeService.registerReward(message.payload)

        professionApproveRepository.save(new ProfessionApprove().with {
            it.channelAgent = message.payload
            it.approveStatus = NOT_APPROVE_1
            it.createTime = new Date()
            it
        })

        message
    }
}
