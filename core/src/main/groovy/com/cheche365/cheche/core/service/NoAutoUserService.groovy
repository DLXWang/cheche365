package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.noAuto.NoAutoUser
import com.cheche365.cheche.core.repository.NoAutoUserRepository
import com.cheche365.cheche.core.repository.UserRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by shanxf on 2017/6/7.
 */
@Service
@Slf4j
class NoAutoUserService {

    @Autowired
    NoAutoUserRepository noAutoUserRepository
    @Autowired
    UserRepository userRepository
    @Autowired
    UserService userService

    NoAutoUser createOrUpdate(String uid, Channel channel, String mobile){

        NoAutoUser noAutoUser
        if(uid&&mobile){
            noAutoUser=noAutoUserRepository.findFirstByUidAndChannelOrderByCreateTimeDesc(uid,channel)
            if(!noAutoUser){
                noAutoUser=createNoAutoUser(uid,channel,mobile)
            }else {
                updateMobile(noAutoUser,mobile)
            }
        }
        noAutoUser
    }

    NoAutoUser find (String uid , Channel channel){
        NoAutoUser noAutoUser=noAutoUserRepository.findFirstByUidAndChannelOrderByCreateTimeDesc(uid,channel)
        log.info("通过uid:{},channel:{} 查出的NoAutoUser：{}",uid,channel.id,noAutoUser?.id)
        noAutoUser
    }

    void updateMobile(NoAutoUser noAutoUser,String mobile){
        if (noAutoUser.getUser()?.getMobile() != mobile) {

            User existUser =userService.getUserByMobile(mobile)
            noAutoUser.setOriginalMobile(noAutoUser.getUser()?.mobile)
            if(existUser!=null){
                noAutoUser.setUser(existUser)
            }else {
                noAutoUser.getUser().setMobile(mobile)
            }
            log.info("update noAutoUser:{},old mobile:{},new mobile:{}", noAutoUser.getUid(), noAutoUser.getUser().getMobile()
                , mobile)
            noAutoUserRepository.save(noAutoUser)
            userRepository.save(noAutoUser.getUser())
        }
    }

    NoAutoUser createNoAutoUser(String uid, Channel channel, String mobile) {
        User user = userService.getUserByMobile(mobile)
        if (user && noAutoUserRepository.findFirstByUserAndChannelOrderByCreateTimeDesc(user, channel)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "手机号已绑定其他UID")
        }

        if (!user) {
            user = userService.createUser(mobile, channel, null)
        }
        NoAutoUser noAutoUserNew = new NoAutoUser(
            uid: uid,
            channel: channel,
            user: user ? user : userService.createUser(mobile, channel, null)
        )
        noAutoUserRepository.save(noAutoUserNew)
    }
}
