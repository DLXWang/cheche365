package com.cheche365.cheche.rest.web.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.counter.annotation.NonProduction
import com.cheche365.cheche.web.service.order.ModifyOrderService
import com.cheche365.cheche.web.util.ClientTypeUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.model.Channel.Enum.WAP_8

/**
 * Created by zhengwei on 4/10/15.
 */

@RestController
@RequestMapping("/token")
@Slf4j
class SessionTokenResource extends ContextResource{

    @Autowired
    private UserRepository userRepository
    @Autowired
    private PaymentRepository paymentRepository
    @Autowired
    private OrderRelatedService orService;
    @Autowired
    private ChannelAgentRepository channelAgentRepository
    @Autowired
    private ModifyOrderService modifyOrderService

    @NonProduction
    @RequestMapping(value="", method= RequestMethod.GET)
    Map<String, String> token(@RequestParam(value = "id", required = false) long id, @RequestParam(value="client", required = false) String client, @RequestParam(value="pref", required = false) String pref, HttpServletRequest request){

        Channel clientType = Channel.allChannels().find {it.name.toLowerCase() == client} ?: WAP_8
        if(id){
            User user = userRepository.findOne(id);
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
        pref?.split(',')?.each {
            request.getSession().setAttribute(it.replace('-',''), !it.startsWith('-'))
        }

        [token : request.getSession().getId()]
    }

    @NonProduction
    @RequestMapping(value="/attrs", method= RequestMethod.GET, produces = "application/json")
    def attrs(HttpServletRequest request){

        def attrs = [:]
        request.getSession().attributeNames.each {attrs[it] = request.getSession().getAttribute(it) }
        return attrs
    }

    @NonProduction
    @GetMapping('/payment')
    Map payOrder(@RequestParam("orderNo") String orderNo){

        modifyOrderService.modifyPayAmount(orderNo)

    }

}
