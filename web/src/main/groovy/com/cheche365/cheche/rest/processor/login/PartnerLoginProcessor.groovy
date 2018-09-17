package com.cheche365.cheche.rest.processor.login

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PartnerUser
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.PartnerUserRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.service.PartnerUserService
import com.cheche365.cheche.partner.service.index.PartnerServiceFactory
import com.cheche365.cheche.web.util.ClientTypeUtil
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest

/**
 * Created by dongrr on 1/14/16.
 * 第三方登录处理类
 */
@Component
@Order(7)
public class PartnerLoginProcessor extends NormalLoginProcessor {

    static final Logger logger = LoggerFactory.getLogger(PartnerLoginProcessor.class)

    @Autowired
    HttpServletRequest request
    @Autowired
    PartnerUserRepository partnerUserRepository
    @Autowired
    UserRepository userRepository
    @Autowired
    PartnerServiceFactory partnerServiceFactory
    @Autowired
    PartnerUserService partnerUserService

    @Override
    List<Channel> getSupportClientType() {
        return Channel.partnerLoginChannel()
    }

    User login(LoginInfo loginInfo) {
        ApiPartner partner = ApiPartner.toApiPartner(ClientTypeUtil.getChannel(request))
        try {
            User user = super.login(loginInfo)

            createOrUpdatePartnerUser(partner, user)

            return user
        } catch (DataIntegrityViolationException ex) {
            logger.error("第三方合作渠道{}保存用户关联关系时，该手机号已经绑定，手机号为 {}", partner.getCode(), loginInfo.getMobile())
            throw ex
        }
    }

    private void createOrUpdatePartnerUser(ApiPartner partner, User user) {
        User userInSession = safeGetCurrentUser()
//处理已存在用户（先在车车注册过，后进入的partner），用数据库里的user替换用户进入首页时生成的空user（session里的）
        PartnerUser partnerUser = partnerUserRepository.findFirstByPartnerAndUser(partner, userInSession)
        if (partnerUser != null) {
            partnerUser.setUser(user)
            partnerUserRepository.save(partnerUser)
        }
        if (partner.createPartnerUser()) {
            partnerServiceFactory.getPartnerService(partner).createOrUpdateUser(partner, user.getMobile(), user.getMobile())
        }

        if (partner.withState()) {
            updatePartnerState(partner, user)
        }
    }

    private void updatePartnerState(ApiPartner partner, User user) {
        Object state = session.getAttribute(SESSION_KEY_PARTNER_STATE)
        if (null == state || StringUtils.isBlank(state.toString())) {
            return
        }
        logger.info("第三方渠道:{}更新用户:{}的state信息：{}", partner.getCode(), user.getMobile(), state.toString())
        partnerUserService.fillPartnerUserStatus(partner, user, state.toString())
    }

}
