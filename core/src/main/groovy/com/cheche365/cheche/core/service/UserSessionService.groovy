package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.repository.UuidMappingRepository
import com.cheche365.cheche.core.util.CacheUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import javax.servlet.http.HttpSession

/**
 * Author:   shanxf
 * Date:     2018/6/26 14:35
 */
@Slf4j
@Service
class UserSessionService {

    private final String USER_SESSION_PREFIX = 'user_session_pre:'

    @Autowired
    StringRedisTemplate stringRedisTemplate

    @Autowired
    UuidMappingRepository uuidMappingRepository;

    void cacheUserSession(ChannelAgent channelAgent, HttpSession httpSession) {
        if (channelAgent?.user) {
            CacheUtil.putToSetWithDayExpire(stringRedisTemplate,getKey(channelAgent), httpSession.id,1)
        }
    }

    Set<String> getUserSession(ChannelAgent channelAgent) {
        if (channelAgent?.user) {
            Set<String> sessionIds = stringRedisTemplate.opsForSet().members(getKey(channelAgent))
            sessionIds.each {
                log.info("by user :{},find sessionId:{}",channelAgent.user.id,it)
            }
            sessionIds
        }
    }

    void removeUser(ChannelAgent channelAgent) {
        if (channelAgent.user) {
            uuidMappingRepository.findByUser(channelAgent.user)?.
                findAll{
                it.clientType.contains(channelAgent.channel.name)
                }?.
                each {
                log.info("channel agent app uuid:{} set logged is false",it.uuid)
                it.logged = false
                uuidMappingRepository.save(it)
                }
            log.info("delete user:{} session info",channelAgent.user.id)
            stringRedisTemplate.delete(getKey(channelAgent))
        }
    }

    private String getKey(ChannelAgent channelAgent) {
        USER_SESSION_PREFIX +channelAgent.channel.id +'_'+ channelAgent.user.id
    }


}
