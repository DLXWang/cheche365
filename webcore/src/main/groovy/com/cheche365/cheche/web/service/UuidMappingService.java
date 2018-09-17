package com.cheche365.cheche.web.service;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.UUIDMapping;
import com.cheche365.cheche.core.repository.UuidMappingRepository;
import com.cheche365.cheche.web.service.ChannelAgentService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


@Service
public class UuidMappingService {

    private Logger logger = LoggerFactory.getLogger(UuidMappingService.class);

    @Autowired
    private UuidMappingRepository uuidMappingRepository;

    @Autowired
    private ChannelAgentService channelAgentService;

    /**
     * 当app登入系统时，如果数据库中取出来的sessionid过期，'springSession中的PersistentSessionStrategy.onNewSession()' 是在请求结束才调用,
     * 所有本次请求结束之前还是用的老的sessionid,所以需要将user,channel,channel_agent(三级代理已注册用户),放入当前session中,保持当前请求中有用户信息。
     * 在本次请求结束会将新的sessionId的更新到UUIDMapping(即调用springSession中的PersistentSessionStrategy.onNewSession())
     */
    public void handlerAppSessionExpire(HttpServletRequest request, String uuid, Channel channel) {
        if(incorrectUuid(request,uuid)){
            logger.info("app update session fail channelId:{},uuid",channel.getId(),uuid);
            return;
        }
        UUIDMapping mapping = uuidMappingRepository.findFirstByUuidAndClientType(uuid, channel.getName());
        if (null != mapping && mapping.isLogged()) {
            CacheUtil.cacheUser(request.getSession(), mapping.getUser());
            ClientTypeUtil.cacheChannel(request, channel);
            channelAgentService.handleChannelAgentCache(request, mapping.getUser(), channel);
        }
    }

    public boolean incorrectUuid(HttpServletRequest request,String uuid) {
        return ClientTypeUtil.isMobileClientType(request) && (StringUtils.isBlank(uuid) || uuid.equals("null"));
    }
}
