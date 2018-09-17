package com.cheche365.cheche.rest.processor.login;

import com.cheche365.cheche.core.model.Channel;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * IOS登陆处理类
 * Created by zhaozhong on 2015/12/18.
 */
@Order(2)
@Component
public class IOSLoginProcessor extends AndroidLoginProcessor {

    /**
     * 支持客户端类型IOS
     */
    @Override
    public List<Channel> getSupportClientType() {
        return Collections.singletonList(Channel.Enum.IOS_4);
    }

    /**
     * 预留appstore审核使用后门
     */
    @Override
    void validValidationCode(LoginInfo loginInfo) {
        Boolean isIOSTesting = "13612345678".equals(loginInfo.getMobile()) && "999999".equals(loginInfo.getVerificationCode());
        if (!isIOSTesting) {
            super.validValidationCode(loginInfo);
        }
    }

    @Override
    Channel deviceClientType() {
        return Channel.Enum.IOS_4;
    }
}
