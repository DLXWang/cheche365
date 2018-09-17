package com.cheche365.cheche.rest.service.pushmessage

import com.cheche365.cheche.core.model.User

interface IPushService {

    /**
     * 推送消息
     * @param user
     * @param businessType
     */
    String simplePush(User user, com.cheche365.cheche.rest.service.pushmessage.PushBusinessType businessType)
}
