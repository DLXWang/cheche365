package com.cheche365.cheche.rest.processor.login

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


/**
 * 常规登陆处理类
 * Created by zhaozhong on 2015/12/17.
 */
@Component
@Order(99)
class NormalLoginProcessor extends LoginProcessor {


    /**
     * 支持客户端列表
     */
    @Override
    List<Channel> getSupportClientType() {
        return Channel.allChannels()
    }

    /**
     * 是否已经登陆
     */
    @Override
    User hasLogin() {
        User user  = this.safeGetCurrentUser()
        if([Channel.Enum.WE_CHAT_3, Channel.Enum.WE_CHAT_APP_39].contains(channel)){
            if (null != user) {
                setUserFlag(user)
                setSubscribed(user)
            }
        }
        return user
    }

    /**
     * 登陆操作,如果手机号没有被使用则创建user
     */
    @Transactional
    @Override
    User login(LoginInfo loginInfo) {

        return doLogin(loginInfo)
    }



    @Transactional
    @Override
    User register(RegisterInfo registerInfo) {
        validLoginInfo(registerInfo)
        userService.validRegisterUser(registerInfo.getMobile())
        User user = findOrCreateNewUser(registerInfo)
        updateUserLoginInfo(user)
        return user
    }

}
