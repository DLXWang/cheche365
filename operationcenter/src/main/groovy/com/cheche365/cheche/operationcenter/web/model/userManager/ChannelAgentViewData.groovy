package com.cheche365.cheche.operationcenter.web.model.userManager

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode
import com.cheche365.cheche.core.model.agent.ProfessionApprove
import com.fasterxml.jackson.annotation.JsonFormat
import org.apache.commons.lang3.StringUtils

class ChannelAgentViewData {

    Long id
    String name
    String mobile
    String identity
    String shopDesc
    String channelDesc
    String inviteCode
    String invitePerson
    String orderCount
    String totalAmount
    String agentLevel
    String topInvitePerson
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date topInviteTime
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date registerTime
    boolean defaultArea
    Long channelId
    Boolean status
    List<Area> areaName
    String approveStatus

    static ChannelAgentViewData createViewModel(ChecheAgentInviteCode inviteCode, String invitePerson, String orderCount,
                                                String totalAmount, List<Area> areaName, ProfessionApprove professionApprove) {
        ChannelAgent channelAgent = inviteCode.channelAgent
        ChannelAgentViewData channelAgentViewData = new ChannelAgentViewData()
        channelAgentViewData.setId(channelAgent.getId())
        channelAgentViewData.setName(channelAgent.getUser().getName())
        channelAgentViewData.setMobile(channelAgent.getUser().getMobile())
        channelAgentViewData.setIdentity(channelAgent.getUser().getIdentity())
        channelAgentViewData.setAgentLevel(channelAgent.getAgentLevel().getDescription())
        channelAgentViewData.setShopDesc(channelAgent.getShop())
        if (channelAgent.shopType != null) {
            channelAgentViewData.setChannelDesc(channelAgent.shopType.description)
        }
        channelAgentViewData.setInviteCode(channelAgent.getInviteCode())
        channelAgentViewData.setRegisterTime(channelAgent.createTime)
        orderCount && channelAgentViewData.setOrderCount(orderCount)
        totalAmount && channelAgentViewData.setTotalAmount(totalAmount)
        invitePerson && channelAgentViewData.setInvitePerson(invitePerson)
        channelAgentViewData.setTopInvitePerson(inviteCode.applicantName)
        channelAgentViewData.setTopInviteTime(inviteCode.createTime)
        channelAgentViewData.setChannelId(channelAgent.getChannel().getId())
        channelAgentViewData.setStatus(channelAgent.getDisable())
        channelAgentViewData.setAreaName(areaName)
        if (professionApprove != null) {
            channelAgentViewData.setApproveStatus(professionApprove.getApproveStatus().getName())
        }
        return channelAgentViewData
    }

}
