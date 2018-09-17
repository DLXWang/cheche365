package com.cheche365.cheche.web.service

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.core.service.WebPurchaseOrderService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.core.util.URLUtils
import com.cheche365.cheche.web.model.UserCallbackInfo
import com.cheche365.cheche.web.util.ClientTypeUtil
import groovy.util.logging.Slf4j
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest

/**
 * Created by taichangwei on 2018/4/13.
 */
@Service
@Slf4j
class UserCallbackService {

    @Autowired
    private ChannelAgentRepository channelAgentRepository

    @Autowired
    private WebPurchaseOrderService webOrderService


    void cacheUserCallbackInfo(HttpServletRequest request, String uuid, Object objId, User user, Channel channel) {

        UserCallbackInfo  info = new UserCallbackInfo([uuid : uuid, objId: objId, user: user])

        if (user && channel.isLevelAgent()) {
            Channel aChannel = Channel.findAgentChannel(channel)
            ChannelAgent channelAgent = channelAgentRepository.findByUserAndChannel(user, aChannel)
            ClientTypeUtil.cacheChannel(request, aChannel)
            !channelAgent ?: info.setChannelAgent(channelAgent)
        }
        CacheUtil.cacheUserCallback(request.session, info)
    }

    ChannelAgent hasLoginByChannelAgent(ChannelAgent channelAgent, UserCallbackInfo userCallbackInfo, HttpServletRequest request){
        !channelAgent && isSmsUrl(userCallbackInfo, request) ? userCallbackInfo?.channelAgent : channelAgent
    }

    User hasLoginByUser(User user, UserCallbackInfo userCallbackInfo, HttpServletRequest request){
        !user && isSmsUrl(userCallbackInfo, request) ? userCallbackInfo?.user : user
    }

    private boolean isSmsUrl(UserCallbackInfo userCallbackInfo, HttpServletRequest request) {
        boolean flag = false
        try {
            URI refererUri = request.getHeader('Referer')?.toURI()
            if (userCallbackInfo && refererUri) {
                Map params = URLUtils.splitQuery(refererUri.query)
                flag = (userCallbackInfo.uuid == params.uuid)
            }

        } catch (Exception e) {
            log.info("parse user call back info Exception:{}", ExceptionUtils.getStackTrace(e))
        }
        return flag
    }

}
