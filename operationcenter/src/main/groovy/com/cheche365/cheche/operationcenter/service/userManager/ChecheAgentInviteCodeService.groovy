package com.cheche365.cheche.operationcenter.service.userManager

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.agent.AgentInviteCodeArea
import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.repository.ChecheAgentInviteCodeRepository
import com.cheche365.cheche.core.repository.agent.AgentInviteCodeAreaRepository
import com.cheche365.cheche.core.service.PurchaseOrderIdService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChecheAgentInviteCodeService {

    @Autowired
    private ChecheAgentInviteCodeRepository checheAgentInviteCodeRepository
    @Autowired
    private PurchaseOrderIdService purchaseOrderIdService
    @Autowired
    private AgentInviteCodeAreaRepository agentInviteCodeAreaRepository
    @Autowired
    private ChannelRepository channelRepository

    List<String[]> applyInviteCodeBatch(Integer number, String applicantName, channelId, List<String> areaList) {
        def list = []
        for (int i = 0; i < number; i++) {
            list << ["applicantName": applicantName, "inviteCode": createInvitationCode(applicantName, channelId, areaList)]
        }
        list
    }

    String createInvitationCode(String applicantName, channelId, List<String> areaList) {
        String inviteCode = purchaseOrderIdService.getInviteCode()
        saveInfo(inviteCode, applicantName, channelId, areaList)
        return inviteCode
    }

    void saveInfo(String inviteCode, String applicantName, channelId, List<String> areaList) {
        ChecheAgentInviteCode agentInviteCode = new ChecheAgentInviteCode()
        agentInviteCode.setInviteCode(inviteCode)
        agentInviteCode.setEnable(true)
        agentInviteCode.setCreateTime(new Date())
        applicantName && agentInviteCode.setApplicantName(applicantName)
        channelId && agentInviteCode.setChannel(channelRepository.findOne(channelId as Long))
        checheAgentInviteCodeRepository.save(agentInviteCode)

        if (!areaList) areaList = [null]
        agentInviteCodeAreaRepository.save(areaList.collect {
            new AgentInviteCodeArea(checheAgentInviteCode: agentInviteCode, area: it ? new Area(id: it.toLong()) : null)
        })
    }

    ChecheAgentInviteCode findByChannelAgentId(Long channelAgentId) {
        checheAgentInviteCodeRepository.findByChannelAgentId(channelAgentId)
    }
}
