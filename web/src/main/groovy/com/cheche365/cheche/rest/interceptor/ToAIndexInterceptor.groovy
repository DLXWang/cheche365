package com.cheche365.cheche.rest.interceptor

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.web.service.http.SessionUtils
import com.cheche365.cheche.web.util.ClientTypeUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.cheche365.cheche.core.constants.WebConstants.SESSION_KEY_CHANNEL_AGENT
import static com.cheche365.cheche.core.constants.WebConstants.SESSION_KEY_USER
import static com.cheche365.cheche.core.model.Channel.Enum.PARTNER_CHEBAOYI_67
import static com.cheche365.cheche.web.util.ClientTypeUtil.cacheChannel
import static com.cheche365.cheche.web.util.ClientTypeUtil.getChannel

/**
 * Created by liheng on 2018/2/28 028.
 */
class ToAIndexInterceptor extends HandlerInterceptorAdapter {

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Channel beforeSwitchChannel = ClientTypeUtil.getCachedChannel(request)

        if (!ClientTypeUtil.isMobileClientType(request)) {
            if (!request.queryString?.contains('src=')) {
                cacheChannel request, PARTNER_CHEBAOYI_67
            }

            if (request.queryString?.contains('src=sms')) {
                cacheChannel request, PARTNER_CHEBAOYI_67
            }
        }

        Channel afterSwitchChannel = getChannel(request)

        SessionUtils.clearSession(beforeSwitchChannel, afterSwitchChannel, request)

        responseNoCache(response)

        true
    }

    private void responseNoCache(HttpServletResponse response) {
        response.setHeader('Cache-Control', 'no-cache, no-store, max-age=0, must-revalidate')
        response.setHeader('Pragma', 'no-cache')
        response.setHeader('Expires', '0')
    }

}
