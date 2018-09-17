package com.cheche365.cheche.rest.processor.login

import com.cheche365.cheche.common.util.ContactUtils
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Gender
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.AgentLevel
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode
import com.cheche365.cheche.core.model.agent.ShopType
import com.cheche365.cheche.core.repository.CcAgentInviteCodeRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.core.service.InviteCodeService
import com.cheche365.cheche.core.service.PurchaseOrderIdService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.web.service.ChannelAgentService
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.exception.BusinessException.Code.INPUT_FIELD_NOT_VALID
import static com.cheche365.cheche.core.exception.BusinessException.Code.OPERATION_NOT_ALLOWED
import static com.cheche365.cheche.core.model.agent.AgentLevel.Enum.nextLevel

@Component
@Order(6)
@Slf4j
class ChannelAgentLoginProcessor extends LoginProcessor {

    @Autowired
    ChannelAgentRepository channelAgentRepository

    @Autowired
    CcAgentInviteCodeRepository ccAgentInviteCodeRepository

    @Autowired
    PurchaseOrderIdService purchaseOrderIdService

    @Autowired
    UserRepository userRepository

    @Autowired
    ChannelAgentService channelAgentService

    @Autowired
    InviteCodeService inviteCodeService

    @Override
    List<Channel> getSupportClientType() {
        Channel.levelAgents()
    }

    /**
     * 预留appstore审核使用后门
     */
    @Override
    void validValidationCode(LoginInfo loginInfo) {
        Boolean isIOSTesting = "13612345678".equals(loginInfo.getMobile()) && "999999".equals(loginInfo.getVerificationCode())
        if (!isIOSTesting) {
            super.validValidationCode(loginInfo)
        }
    }

    @Override
    ChannelAgent hasLogin() {
        User user = safeGetCurrentUser()
        ChannelAgent channelAgent = safeGetCurrentAgent()
        if (channelAgent && ! user){
            CacheUtil.cacheUser(request.getSession(), channelAgent.user)
            user =  channelAgent.user
        }
        if(channelAgent && user){
            setUserFlag(user)
            setSubscribed(user)
            channelAgent.setUser(user)
        }
        channelAgent
    }

    @Transactional
    @Override
    ChannelAgent login(LoginInfo loginInfo) {
        User user
        if (isOpenWithOauth()) {
            user = bind(loginInfo);
        } else {
            validLoginInfo(loginInfo)
            user = userService.getBindingUser(loginInfo.mobile)
        }
        ChannelAgent channelAgent = validateLoginCa(user,Channel.findAgentChannel(channel))
        setSubscribed(user)
        updateUserLoginInfo(user)
        channelAgent.setUser(user)
        channelAgent
    }

    @Transactional
    @Override
    ChannelAgent register(RegisterInfo registerInfo) {

        log.info("register info message:{}",CacheUtil.doJacksonSerialize(registerInfo))
        validRegisterInfo(registerInfo)
        User user = findOrCreateNewUser(registerInfo)

        validateRegisterCa(user,Channel.findAgentChannel(channel))

        createNewChannelAgent(user, registerInfo)
    }

    User findOrCreateNewUser(RegisterInfo registerInfo){
        LoginInfo loginInfo = new LoginInfo().with {
            it.gender           = registerInfo?.gender
            it.verificationCode = registerInfo?.verificationCode
            it.mobile           = registerInfo?.mobile
            it
        }
        User user = doLogin(loginInfo)
        user.setIdentity(registerInfo.identity)
        user.setGender('1' == registerInfo.gender ? Gender.Enum.MALE : Gender.Enum.FEMALE)
        user.setName(registerInfo.name)
        user.setEmail(registerInfo.email)

        setRegisterIpAndChannel(user)

        userRepository.save(user)
    }

    ChannelAgent createNewChannelAgent(User user, RegisterInfo registerInfo){
        if (inviteCodeService.isChecheCode(registerInfo.inviteCode)){
            handleChecheCodeRegister(user, registerInfo)
        } else {
            handleAgentCodeRegister(user, registerInfo)
        }
    }


    private ChannelAgent handleChecheCodeRegister(User user, RegisterInfo registerInfo){
        ChecheAgentInviteCode checheCode = ccAgentInviteCodeRepository.findByInviteCode(registerInfo.inviteCode)
        ChannelAgent channelAgent  = handleCommonRegister(user, registerInfo)
        channelAgent.setInviteCode(purchaseOrderIdService.getInviteCode())
        channelAgent.setAgentLevel(AgentLevel.Enum.SALE_DIRECTOR_1)
        channelAgent.setParent(null)
        channelAgent.setDisable(false)

        channelAgentService.createQRCode(channelAgent)

        checheCode.setChannelAgent(channelAgent)
        checheCode.setEnable(false)

        channelAgentRepository.save(channelAgent)
        ccAgentInviteCodeRepository.save(checheCode)
        channelAgent
    }

    private ChannelAgent handleAgentCodeRegister(User user, RegisterInfo registerInfo){
        ChannelAgent parentAgent = channelAgentRepository.findByInviteCodeAndChannel(registerInfo.inviteCode, Channel.findAgentChannel(channel))
        ChannelAgent channelAgent  = handleCommonRegister(user, registerInfo)
        AgentLevel agentLevel = nextLevel(parentAgent.agentLevel)
        channelAgent.setInviteCode(agentLevel.isLeaf ? null : purchaseOrderIdService.getInviteCode())
        channelAgent.setAgentLevel(agentLevel)
        channelAgent.setParent(parentAgent)
        channelAgent.setDisable(false)
        if(!channelAgent.agentLevel.isLeaf){
            channelAgentService.createQRCode(channelAgent)
        }

        channelAgentRepository.save(channelAgent)
        channelAgent
    }

    private ChannelAgent handleCommonRegister(User user, RegisterInfo registerInfo){
        ChannelAgent channelAgent = new ChannelAgent()
        channelAgent.setUser(user)
        channelAgent.setChannel(Channel.findAgentChannel(channel))
        channelAgent.setCreateTime(Calendar.getInstance().getTime())
        channelAgent.setUpdateTime(Calendar.getInstance().getTime())
        channelAgent.setShopType(registerInfo.shopType)
        channelAgent.setShop(registerInfo.getShop())
        channelAgent
    }




    private void validRegisterInfo(RegisterInfo registerInfo) {
        validLoginInfo(registerInfo)
        channelAgentService.verifyAgentInviteCode(registerInfo.inviteCode, channel)
        
        if (StringUtils.isBlank(registerInfo.name)){
            throw new BusinessException(INPUT_FIELD_NOT_VALID, "请输入姓名")
        }
        
    }

    void validateRegisterCa(User user, Channel channel) {
        ChannelAgent channelAgent = channelAgentRepository.findChannelAgent(user.id, channel.id)
        if (!channelAgent) {
            return
        } else if (channelAgent.disable) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, '您的权限已被禁用')
        } else {
            throw new BusinessException(OPERATION_NOT_ALLOWED, '您已是注册用户，请前去登录')
        }
    }

    ChannelAgent validateLoginCa(User user, Channel channel) {

        if (!user) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, '亲，您还不是注册用户哦~请点击注册成为用户。')
        }
        ChannelAgent channelAgent = channelAgentRepository.findChannelAgent(user.id, channel.id)
        if (!channelAgent) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, '亲，您还不是注册用户哦~请点击注册成为用户。')
        } else if (channelAgent.disable) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, '您无操作权限')
        } else {
            return channelAgent
        }
    }
}
