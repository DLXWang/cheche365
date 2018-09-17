package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.message.RedisPublisher;
import com.cheche365.cheche.core.message.TMLoginUserMessage;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.MobileSourceType;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.UserLoginInfo;
import com.cheche365.cheche.core.repository.UserLoginInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional

/**
 * Created by mahong on 2016/7/19.
 */
@Service
@Transactional
class UserLoginInfoService {
    private static Logger logger = LoggerFactory.getLogger(UserLoginInfoService.class);

    @Autowired
    private UserLoginInfoRepository userLoginInfoRepository;

    @Autowired
    private RedisPublisher redisPublisher;

    void updateUserLoginInfo(User user, String lastLoginIp, Channel channel, MobileSourceType mobileType = null) {
        if (null == user) {
            return;
        }
        UserLoginInfo userLoginInfo = userLoginInfoRepository.findFirstByUser(user);
        if(user.getMobile()==null||user.getMobile()==""){
            return;
        }
        if (userLoginInfo == null) {
            userLoginInfo = new UserLoginInfo();
            userLoginInfo.setUser(user);
        }
        logger.info("当前用户手机号不为空情况下，更新userLoginfo中的channel信息 手机号为："+user.getMobile());
        userLoginInfo.setChannel(channel);
        userLoginInfo.setLastLoginTime(Calendar.getInstance().getTime());
        userLoginInfo.setLastLoginIp(lastLoginIp)
        userLoginInfo.setMobileSourceType(mobileType)
        userLoginInfoRepository.save(userLoginInfo)
        redisPublisher.publish(new TMLoginUserMessage().setMessage(userLoginInfo));//更新user login 的区域信息
    }
}
