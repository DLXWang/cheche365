package com.cheche365.cheche.web.service.system

import com.cheche365.cheche.core.model.agent.ChannelAgent
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 15/06/2018.
 * 车保易邀请注册链接生成器
 */

@Service
class ChannelAgentInviteURL extends SystemURL{


    String toClientPage(ChannelAgent channelAgent) {
        super.generate(
            host: inviteRoot(),
            path: 'index.html',
            qs: [
                src: channelAgent.channel.isThirdPartnerChannel() ? channelAgent.channel.apiPartner.code : null,
                inviteCode: channelAgent.inviteCode,
                name:channelAgent.user.name,
                channel:channelAgent.channel.id
            ],
            fragment: '/invite'
        ).replace("http://","https://")
    }

    @Override
    String desc() {
        '车保易邀请注册链接生成器'
    }

    @Override
    String cacheKeyPrefix() {
        return null
    }
}
