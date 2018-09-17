package com.cheche365.cheche.web.service.shareInfo

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.web.service.system.ChannelAgentInviteURL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Author:   shanxf
 * Date:     2018/6/25 17:50
 */
@Service
class ChannelAgentShareHandler extends ShareAbstract {

    private static String agent_default_img_path = "iosmodule/agent/cby_new.jpg"
    private static String agent_kunlun_img_paht  ='iosmodule/agent/kunlun.png'

    @Autowired
    private ChannelAgentInviteURL channelAgentInviteURL

    String  title(String title){
        "${title}邀请注册"
    }

    String  desc(String desc){
        "${desc}邀请您一起去赚钱，车险出单快，推广费高。"
    }

    Map shareInfo(ChannelAgent channelAgent) {
        if (!channelAgent.agentLevel.isLeaf) {
            channelAgent.setInviteQrUrl(channelAgent.shareLink())
            return [
                title : title(channelAgent.channel == Channel.Enum.PARTNER_CHEBAOYI_67 ? '车保易':''),
                desc  : desc(channelAgent.user.name),
                link  : channelAgentInviteURL.toClientPage(channelAgent),
                imgUrl: imgAbsolutePath(findChannelImg(channelAgent.channel)),
                img   : null
            ]

        }
    }

    String imgAbsolutePath(String path){
        resourceService.absoluteUrl(resourceService.getResourceUrl(resourceService.getResourceAbsolutePath(path)))
    }

    String findChannelImg(Channel channel){
        if(channel?.apiPartner && ['kunlun','kunlunbz'].contains(channel.apiPartner.code)){
            return agent_kunlun_img_paht
        }
        return agent_default_img_path
    }

}
