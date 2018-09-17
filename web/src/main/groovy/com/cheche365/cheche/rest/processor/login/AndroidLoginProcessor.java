package com.cheche365.cheche.rest.processor.login;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.rest.web.session.MobileSessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Android登陆处理类
 * Created by zhaozhong on 2015/12/21.
 */
@Component
@Order(3)
public class AndroidLoginProcessor extends NormalLoginProcessor {

    @Autowired
    private MobileSessionHandler mobileSessionHandler;//App Session持久化处理类

    /**
     * 支持客户端类型Android
     */
    @Override
    public List<Channel> getSupportClientType() {
        return Collections.singletonList(Channel.Enum.ANDROID_6);
    }

    /**
     * 登录，app添加session持久化处理
     */
    @Override
    public User login(LoginInfo loginInfo) {
        User user = super.login(loginInfo);
        if (null != user) {
            mobileSessionHandler.doLogin(getRequest(), session, user, deviceClientType());
        }
        return user;
    }

    Channel deviceClientType() {
        return Channel.Enum.ANDROID_6;
    }

}
