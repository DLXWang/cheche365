package com.cheche365.cheche.developer.service

import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.mongodb.repository.MoHttpClientLogRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.developer.util.LogMessageUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.developer.util.LogMessageUtils.formatMessage



/**
 * Created by liushijie on 2018/7/19.
 */

@Service
class UserInfoService {
    @Autowired
    UserRepository userRepository

    @Autowired
    ChannelAgentRepository channelAgentRepository

    @Autowired
    MoHttpClientLogRepository moHttpClientLogRepository

    List<Map>findSyncHistory(mobile){

        def user = userRepository.findByMobile(mobile)
        def agentList = channelAgentRepository.findByUser(user)
        agentList.collect {agent->

            def resultList = moHttpClientLogRepository.findByObjIdOrderByCreateTime(String.valueOf(agent.id))?.collect {
                ["message": formatMessage(it.logMessage), "create_time": it.createTime]
            }
            [(agent.channel.description):resultList]
        }
    }



}
