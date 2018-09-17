package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.agent.AgentLevel;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.cheche365.cheche.core.repository.Page;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.service.ChannelAgentInfoService;
import com.cheche365.cheche.web.version.VersionedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:   shanxf
 * Date:     2018/3/10 14:12
 */
@RestController
@RequestMapping(value = ContextResource.VERSION_NO + "/agent")
public class AgentsResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(AgentsResource.class);

    @Autowired
    private ChannelAgentInfoService channelAgentInfoService;


    @RequestMapping(value = "team/info", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> agentInfo() {

        Channel channel = getChannel();
        ChannelAgent channelAgent = getCurrentChannelAgent();

        if (!channel.isLevelAgent() || channelAgent.getAgentLevel().getIsLeaf()) {
            logger.info("current user id :{},channel id:{}", currentUser().getId(), channel.getId());
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "当前用户无权限配置下级");
        }

        Map map = channelAgentInfoService.agentInfo(channelAgent);
        logger.info("current channelAgent id:{},team info:{}", channelAgent.getId(), CacheUtil.doJacksonSerialize(map));
        return getResponseEntity(map);
    }

    @RequestMapping(value = "team/detail", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> nextAgent(@RequestParam(value = "size", required = false) Integer size,
                                                      @RequestParam(value = "page", required = false) Integer page,
                                                      @RequestParam(value = "type", required = true) Long type,
                                                      @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {

        ChannelAgent channelAgent = getCurrentChannelAgent();
        AgentLevel agentLevel = AgentLevel.Enum.byIdFindAgentLevel(type);
        Pageable pageable = new PageRequest(page, size);
        Page<List> agentList = channelAgentInfoService.agentList(pageable, channelAgent, keyword, agentLevel);
        HashMap hashMap = CacheUtil.doJacksonDeserialize(CacheUtil.doJacksonSerialize(agentList), HashMap.class);
        hashMap.put("headLevel", agentLevel.getDescription());
        return getResponseEntity(hashMap);

    }

    @RequestMapping(value = "/rebate", method = RequestMethod.GET)
    @VersionedResource(from = "1.9")
    public HttpEntity<RestResponseEnvelope> agentRebates(@RequestParam(value = "agentId") Long agentId,
                                                         @RequestParam(value = "areaId") Long areaId) {

        ChannelAgent channelAgent = getCurrentChannelAgent();
        List<Map> channelAgentRebates = channelAgentInfoService.channelAgentRebateList(channelAgent, agentId, areaId);
        return getResponseEntity(channelAgentRebates);
    }

    @RequestMapping(value = "/rebate/config", method = RequestMethod.POST)
    @VersionedResource(from = "1.9")
    public HttpEntity<RestResponseEnvelope> agentConfigs(@RequestBody Map rebateConfigs) {

        ChannelAgent channelAgent = getCurrentChannelAgent();
        logger.info("modify rebate rebateConfig :{}", CacheUtil.doJacksonSerialize(rebateConfigs));
        channelAgentInfoService.modifyRebates(channelAgent, rebateConfigs);
        return getResponseEntity("success");
    }

    @RequestMapping(value = "rebate/rate", method = RequestMethod.GET)
    @VersionedResource(from = "1.9")
    public HttpEntity<RestResponseEnvelope> rebateRateNew(@RequestParam(value = "areaId") Long areaId) {
        ChannelAgent channelAgent = getCurrentChannelAgent();
        return getResponseEntity(channelAgentInfoService.rebateRate(channelAgent, areaId));
    }
}
