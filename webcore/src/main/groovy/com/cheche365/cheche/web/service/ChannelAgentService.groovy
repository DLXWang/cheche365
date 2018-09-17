package com.cheche365.cheche.web.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode
import com.cheche365.cheche.core.repository.CcAgentInviteCodeRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.core.service.UserSessionService
import com.cheche365.cheche.core.service.qrcode.QRCodeService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.web.service.system.ChannelAgentInviteURL
import groovy.util.logging.Slf4j
import org.apache.commons.lang.StringUtils
import org.springframework.beans.BeanWrapper
import org.springframework.beans.PropertyAccessorFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.constants.WebConstants.SESSION_KEY_CHANNEL_AGENT
import static com.cheche365.cheche.core.exception.BusinessException.Code.INPUT_FIELD_NOT_VALID
import static com.cheche365.cheche.core.exception.BusinessException.Code.OPERATION_NOT_ALLOWED
import static com.cheche365.cheche.core.service.PurchaseOrderIdService.AGENT_INVITATION_CODE

@Service
@Slf4j
class ChannelAgentService {

    @Autowired
    StringRedisTemplate stringRedisTemplate

    @Autowired
    CcAgentInviteCodeRepository ccAgentInviteCodeRepository

    @Autowired
    ChannelAgentRepository channelAgentRepository

    @Autowired
    UserSessionService userSessionService

    @Autowired
    ChannelAgentInviteURL inviteURL

    @Autowired
    QRCodeService qrCodeService

    void verifyAgentInviteCode(String inviteCode, Channel channel) {
        if (!inviteCode || !stringRedisTemplate.opsForSet().isMember(AGENT_INVITATION_CODE, inviteCode)) {
            log.info("redis not find inviteCode:{}",inviteCode)
            throw new BusinessException(INPUT_FIELD_NOT_VALID, "请输入正确的邀请码")
        }
        ChecheAgentInviteCode checheCode = ccAgentInviteCodeRepository.findByInviteCode(inviteCode)
        if (checheCode && !checheCode.enable){
            throw new BusinessException(OPERATION_NOT_ALLOWED, "邀请码已失效")
        }
        ChannelAgent channelAgent = channelAgentRepository.findByInviteCodeAndChannel(inviteCode, Channel.findAgentChannel(channel))
        if (!checheCode && !channelAgent) {
            log.info("invite code invalid current input invite code:{},current channel id:{}",inviteCode,channel.id)
            throw new BusinessException(OPERATION_NOT_ALLOWED, "邀请码无效,输入有效邀请码")
        }
    }


    ChannelAgent getCurrentChannelAgent(QuoteRecord quoteRecord){

        log.info("by quoteRecord id:{},user id:{},channel id:{} find channel_agent",quoteRecord.id,quoteRecord.applicant.id,quoteRecord.channel.id)

        User user = quoteRecord.applicant
        Channel channel = Channel.findAgentChannel(quoteRecord.channel).parent

        return channelAgentRepository.findByUserAndChannel(user,channel)
    }

    ChannelAgent modifyChannelAgent(ChannelAgent existedChannelAgent, ChannelAgent channelAgent) {
        BeanWrapper sourceChannelAgent = PropertyAccessorFactory.forBeanPropertyAccess(channelAgent)
        BeanWrapper targetChannelAgent = PropertyAccessorFactory.forBeanPropertyAccess(existedChannelAgent)
        ["shopType", "shop","ethnic"].each {
            if(sourceChannelAgent.getPropertyValue(it)) {
                targetChannelAgent.setPropertyValue(it, sourceChannelAgent.getPropertyValue(it))
            }
        }
        existedChannelAgent.updateTime = new Date() //TODO 为了触发同步 临时解决方案
        return  channelAgentRepository.save(existedChannelAgent)
    }

    void modifyQRCode(User user){
        channelAgentRepository.findByUser(user).each {
            createQRCode(it)
        }
    }

    private createQRCode(ChannelAgent channelAgent){
        String inviteUrl = inviteURL.toClientPage(channelAgent)
        log.debug('原始邀请链接 : {}', inviteURL)

        Map urls = qrCodeService.agentInviteQRCode(inviteUrl, channelAgent.inviteCode + '.png', channelAgent.channel)
        log.debug('邀请二维码图片绝对路径: {}, 相对路径: {}', urls.absUrl, urls.relUrl)

        channelAgent.inviteQrCode  = urls.relUrl
    }

    void handleChannelAgentCache(HttpServletRequest request, User user, Channel channel){
        if(channel.isLevelAgent()){
            ChannelAgent channelAgent = channelAgentRepository.findByUserAndChannel(user,Channel.findAgentChannel(channel))
            CacheUtil.cacheChannelAgent(request.getSession(),channelAgent)
            userSessionService.cacheUserSession(channelAgent,request.getSession())
        }
    }
}
