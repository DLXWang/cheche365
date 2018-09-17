package com.cheche365.cheche.partner.service.index

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.repository.AutoRepository
import com.cheche365.cheche.core.repository.UserAutoRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.service.PartnerUserService
import com.cheche365.cheche.core.service.UserLoginInfoService
import com.cheche365.cheche.core.service.UserService
import com.cheche365.cheche.core.util.AutoUtils
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.core.util.IpUtil
import com.cheche365.cheche.partner.handler.index.PartnerIndexParams
import com.cheche365.cheche.web.service.http.SessionUtils
import com.cheche365.cheche.web.util.ClientTypeUtil
import groovy.json.JsonSlurper
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.constants.WebConstants.SESSION_KEY_PARTNER_UID
import static com.cheche365.cheche.core.exception.BusinessException.Code.INPUT_FIELD_NOT_VALID
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_APP_SECRET
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey
import static com.cheche365.cheche.core.serializer.SerializerUtil.toMapKeepFields
import static com.cheche365.cheche.core.util.ValidationUtil.validMobile
import static com.cheche365.cheche.partner.utils.PartnerEncryptUtil.decrypt

/**
 * Created by chenxiaozhe on 16-4-22.
 */
@Service("basePartnerService")
class PartnerService {
    static final String MOBILE = "mobile"

    @Autowired
    private PartnerUserService partnerUserService
    @Autowired
    private UserRepository userRepository
    @Autowired
    protected AutoService autoService
    @Autowired
    protected AutoRepository autoRepository
    @Autowired
    protected UserAutoRepository userAutoRepository
    @Autowired
    protected UserLoginInfoService userLoginInfoService
    @Autowired
    protected UserService userService
    @Autowired
    private ChannelAgentRepository channelAgentRepository

    protected final Logger logger = LoggerFactory.getLogger(getClass())

    ApiPartner apiPartner() {
        return null
    }

    void decryptParam(ApiPartner partner, PartnerIndexParams param) {
        String originalMobile = param.mobile
        if (originalMobile && !validMobile(originalMobile)) {
            param.put('mobile', decrypt(originalMobile, findByPartnerAndKey(partner, SYNC_APP_SECRET).value))
            if (!validMobile(param.mobile)) {
                throw new BusinessException(INPUT_FIELD_NOT_VALID, "手机号格式验证失败 " + param.mobile)
            }
            logger.debug("解密mobile，密文: {}，明文: {}", originalMobile, param.mobile)
        }
    }

    void preHandle(ApiPartner partner, PartnerIndexParams param) {
        String uid = param.uid
        if (StringUtils.isNotBlank(uid) && uid.contains("+")) {
            logger.debug(" uid contains +, will replace with white space, before {}", uid)
            param.put('uid', uid.replace("+", " "))
        }

        if (partner.withAuto() && param.auto) {
            param.putAll(new JsonSlurper().parseText(param.auto))
        }
    }

    /**
     * 填充车辆信息，用数据库里的车辆信息补全用户传的车辆信息
     */
    @Transactional
    void fillUpAuto(PartnerIndexParams param, User user) {
        if (!param.licensePlateNo?.trim() || !param.owner?.trim()) {
            return
        }

        param.licensePlateNo = param.licensePlateNo?.toUpperCase()
        param.enrollDate = DateUtils.getDate(param.enrollDate as String, DateUtils.DATE_SHORTDATE_PATTERN)
        param.area = AutoUtils.getAreaOfAuto(param.licensePlateNo)
        Auto newAuto = new Auto(param.subMap(['licensePlateNo', 'owner', 'identity', 'engineNo', 'vinNo', 'enrollDate', 'area']))

        autoService.validAreaOfAuto(newAuto)
        param.put('auto', CacheUtil.doJacksonSerialize(
            toMapKeepFields(
                newAuto.with {
                    List<Auto> autos = user ? userAutoRepository.findAutosByConditions(user, newAuto) : []
                    !autos.isEmpty() ? autos.first() : newAuto
                },
            'area, code, autoType, enrollDate, licensePlateNo, identity, owner, engineNo, vinNo'),
            true))
    }

    User createOrUpdateUser(ApiPartner partner, PartnerIndexParams param) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
        ChannelAgent channelAgent = SessionUtils.get(request.getSession(), WebConstants.SESSION_KEY_CHANNEL_AGENT)
        User user = channelAgent && request.session.getAttribute(SESSION_KEY_PARTNER_UID)?.toString() == param.uid ? channelAgent.user : createOrUpdateUser(partner, param.uid, param.mobile)
        partnerUserService.fillPartnerUserStatus(partner, user, param.state)
        return user
    }

    @Transactional
    User createOrUpdateUser(ApiPartner partner, String uid, String mobile) {
        log(partner, String.format("第三方链接跳转，创建或者更新用户关联信息 , partner code = [%s], uid = [%s], mobile = [%s]", partner.getCode(), uid, mobile))
        User user = userService.findOrCreateUser(mobile, Channel.findByApiPartner(partner), null)
        if (!StringUtils.isBlank(uid)) {
            user = partnerUserService.createOrUpdatePartnerUser(partner, uid, mobile, user).getUser()
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
        userLoginInfoService.updateUserLoginInfo(user, IpUtil.getIP(request), ClientTypeUtil.getChannel(request),MobileSourceType.Enum.PARTNER_URL)
        return user
    }

    protected void log(ApiPartner partner, String logMessage) {
        logger.debug(String.format(" %s log : [%s] ", partner.getCode(), logMessage))
    }

    void cacheChannelAgent(User user, ApiPartner partner, session) {
        def channel = Channel.findByApiPartner(partner)
        def channelAgent = channel ? channelAgentRepository.findByUserAndChannel(user, Channel.findAgentChannel(channel)) : null
        if (channelAgent) {
            CacheUtil.cacheChannelAgent(session, channelAgent)
        }
    }
}
