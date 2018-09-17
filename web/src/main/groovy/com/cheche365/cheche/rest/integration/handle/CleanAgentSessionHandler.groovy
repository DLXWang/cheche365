package com.cheche365.cheche.rest.integration.handle

import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.service.UserSessionService
import com.cheche365.cheche.web.integration.IIntegrationHandler
import com.cheche365.cheche.web.model.Message
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.session.data.redis.RedisOperationsSessionRepository
import org.springframework.stereotype.Service

/**
 * Created by liheng on 2018/6/26 0026.
 */
@Service
@Slf4j
class CleanAgentSessionHandler implements IIntegrationHandler<Message<ChannelAgent>> {

    @Autowired
    RedisOperationsSessionRepository redisOperationsSessionRepository

    @Autowired
    UserSessionService userSessionService

    @Override
    Message<ChannelAgent> handle(Message<ChannelAgent> message) {
        def channelAgent = message.payload
        log.info(" set blacklist channelAgent:{}", channelAgent.id)
        userSessionService.getUserSession(channelAgent)
            .each {
            redisOperationsSessionRepository.delete(it)
        }?.with {
            userSessionService.removeUser(channelAgent)
        }
        message
    }
}
