package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.ChannelRebate
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode

/**
 * Author:   shanxf
 * Date:     2018/5/31 16:44
 */
interface IChannelAgentInfoService {

    Map calculateRebate(ChannelRebate channelRebate, ChannelAgent channelAgent)

    ChecheAgentInviteCode findChecheInviteCode(ChannelAgent channelAgent)
}
