package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.PartnerUser
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.PartnerUserRepository
import com.cheche365.cheche.core.repository.UserRepository
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by mahong on 2016/2/17.
 */
@Service("partnerUserService")
@Slf4j
class PartnerUserService {

    @Autowired
    private PartnerUserRepository partnerUserRepository
    @Autowired
    private UserRepository userRepository
    @Autowired
    private UserService userService

    @Transactional
    void saveNciPartnerUserMobile(PartnerUser partnerUser, String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return
        }
        User userInDB = userService.getUserByMobile(mobile)
        if (StringUtils.isBlank(partnerUser.getUser().getMobile()) && userInDB == null) {
            userService.boundMobile(partnerUser.getUser(),mobile)
        }
        log.info("世纪通宝PartnerUser表 PartnerMobile 替换前{},替换后{}", partnerUser.getPartnerMobile(), mobile)
        partnerUser.setPartnerMobile(mobile)
        partnerUserRepository.save(partnerUser)
    }

    PartnerUser createOrUpdatePartnerUser(ApiPartner partner, String uid, String mobile, User user) {
        PartnerUser partnerUser = partnerUserRepository.findFirstByPartnerAndPartnerId(partner, uid)
        if (null == partnerUser) {
            return partnerUserRepository.save(new PartnerUser(partner: partner, partnerId: uid, user: user))
        }

        if (StringUtils.isBlank(mobile)) {
            return partnerUser
        }

        User oldUser = partnerUser.getUser()
        User existUser = userService.getUserByMobile(mobile)
        if (StringUtils.isNotBlank(oldUser.getMobile())) {
            if (!existUser && oldUser.getMobile() != mobile) {
                log.info("第三方渠道:{},手机号更新userId:{}，旧手机号:{}，新手机号:{}", partnerUser.getPartner(), oldUser.getId(), oldUser.getMobile(), mobile)
                userService.boundMobile(oldUser, mobile)
                return partnerUser
            }
            log.info("第三方渠道:{},partner user用户有手机号，不更新用户手机号,userId:{},数据库手机号{}, 登录手机号{}", partnerUser.getPartner(), oldUser.getId(), oldUser.getMobile(), mobile)
            return partnerUser
        }
        if (existUser) {
            log.info("第三方渠道:{},手机号:{}已存在数据库中, 将该手机号的UserId:{}取代partner_user:{}关联旧的UserId:{}", partnerUser.getPartner(), existUser.getMobile(), existUser.getId(), partnerUser.getId(), partnerUser.getUser().getId())
            partnerUser.setUser(existUser)
            partnerUser.setUpdateTime(new Date())
            return partnerUserRepository.save(partnerUser)
        } else {
            log.info("第三方渠道:{},手机号填充，userId:{}，手机号:{}", partnerUser.getPartner(), oldUser.getId(), mobile)
            oldUser.setMobile(mobile)
            oldUser.setBound(StringUtils.isBlank(mobile) ? Boolean.FALSE : Boolean.TRUE)
            userRepository.save(oldUser)
            return partnerUser
        }
    }

    @Transactional
    void fillPartnerUserStatus(ApiPartner partner, User user, String state) {
        if (StringUtils.isBlank(state)) {
            return
        }
        PartnerUser partnerUser = partnerUserRepository.findFirstByPartnerAndUser(partner, user)
        if (partnerUser != null) {
            partnerUser.setState(state)
            partnerUserRepository.save(partnerUser)
        }
    }
}
