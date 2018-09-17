package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode
import com.cheche365.cheche.core.repository.CcAgentInviteCodeRepository
import com.cheche365.cheche.core.service.PurchaseOrderIdService
import com.cheche365.cheche.web.counter.annotation.NonProduction
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1.6/mock/agent")
@Slf4j
class MockChannelAgentResource {

    @Autowired
    PurchaseOrderIdService purchaseOrderIdService

    @Autowired
    CcAgentInviteCodeRepository ccAgentInviteCodeRepository

    @NonProduction
    @RequestMapping(value="/inviteCode", method= RequestMethod.GET)
    generateChecheInviteCode(@RequestParam(value = 'channelId') Long channelId) {
        Channel channel = Channel.allChannels().find { it.id == channelId }
        ChecheAgentInviteCode checheCode = new ChecheAgentInviteCode()
        checheCode.setChannel(channel)
        checheCode.setInviteCode(purchaseOrderIdService.getInviteCode())
        checheCode.setEnable(true)
        checheCode.setCreateTime(Calendar.getInstance().getTime())
        ccAgentInviteCodeRepository.save(checheCode)
        checheCode
    }

}
