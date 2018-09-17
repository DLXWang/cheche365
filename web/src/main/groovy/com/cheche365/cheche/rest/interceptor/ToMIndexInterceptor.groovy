package com.cheche365.cheche.rest.interceptor

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.web.service.http.SessionUtils
import com.cheche365.cheche.web.util.ClientTypeUtil
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.cheche365.cheche.core.constants.WebConstants.*
import static com.cheche365.cheche.web.util.ClientTypeUtil.getChannel

/**
 * Created by shanxf on 2018/4/21.
 */
class ToMIndexInterceptor extends HandlerInterceptorAdapter {

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Channel beforeSwitchChannel = ClientTypeUtil.getCachedChannel(request)

        if (!request.queryString?.contains('src=') &&
            (Channel.agents().contains(beforeSwitchChannel) || Channel.allPartners().contains(beforeSwitchChannel))
        ) {
            SessionUtils.removeSessionAttr([SESSION_KEY_USER, SESSION_KEY_CHANNEL_AGENT, SESSION_KEY_CLIENT_TYPE],request)
        }

        true
    }

}
