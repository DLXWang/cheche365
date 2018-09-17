package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.core.service.spi.IHTTPContext
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.web.counter.annotation.NonProduction
import com.cheche365.cheche.web.util.ClientTypeUtil
import com.cheche365.cheche.web.version.VersionedResource
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.web.service.http.SessionUtils.toTree

/**
 * Created by zhengwei on 7/18/17.
 */

@RestController
@RequestMapping("/v1.6/mock/token")
@VersionedResource(from = "1.5")
class MockTokenResource {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IHTTPContext context
    @Autowired
    private ChannelAgentRepository channelAgentRepository

    @NonProduction
    @RequestMapping(value="", method= RequestMethod.POST)
    Map<String, String> token(@RequestBody Map attrs, HttpServletRequest request){

        Channel clientType = Channel.allChannels().find {it.name.equalsIgnoreCase(attrs.client)} ?: Channel.Enum.WAP_8

        if(!(attrs.noClearSession)){
            clearSessionAttr(request)
        }

        if(attrs.id){
            User user = userRepository.findOne(attrs.id as Long);
            if (!user){
                throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "用户ID不存在")
            }
            CacheUtil.cacheUser(request.getSession(), user)
            ChannelAgent channelAgent = channelAgentRepository.findByUserAndChannel(user, clientType)
            if (channelAgent){
                CacheUtil.cacheChannelAgent(request.getSession(), channelAgent)
            }
        }

        ClientTypeUtil.cacheChannel(request, clientType)

        attrs.findAll {
            !['id', 'client'].contains(it.key)
        }.each{
            request.getSession().setAttribute(it.key, it.value)
            context.copySession()
        }

        [token : request.getSession().getId()]
    }

    @NonProduction
    @RequestMapping(value="client", method= RequestMethod.GET)
    Map client(@RequestParam(value="client") String client, HttpServletRequest request){
        def clientObj = Channel.allChannels().find {it.name.toLowerCase() == client} ?: Channel.Enum.WAP_8
        ClientTypeUtil.cacheChannel(request, clientObj)
        [client: clientObj]
    }

    @NonProduction
    @RequestMapping(value="attrs", method= RequestMethod.GET)
    attrs(HttpServletRequest request, @RequestParam(name="display", required=false) String display){
        def attrs = [:]
        request.getSession().attributeNames.each {
            def value = request.getSession().getAttribute(it)
            attrs[it] = value?.toString()?.startsWith('{') ? new JsonSlurper().parseText(value) : value
        }
        attrs.put("sessionId",request.getSession().id)
        return 'tree' == display ? toTree(attrs) : attrs
    }

    @NonProduction
    @RequestMapping(value = "attrs", method= RequestMethod.DELETE)
    Map clearSessionAttr(HttpServletRequest request){
        context.removeSession(request.getSession().id)
        request.session.attributeNames.toList().each { request.session.removeAttribute(it) }
        ['session size': context.sessionSize()]
    }

}
