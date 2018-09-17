package com.cheche365.cheche.rest.interceptor

import com.cheche365.cheche.core.util.URLUtils
import com.cheche365.cheche.web.service.system.SuspendBillURL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

import static com.cheche365.cheche.core.constants.WebConstants.SESSION_KEY_USER
import static com.cheche365.cheche.web.service.http.SessionUtils.USER_RELATED

/**
 * Created by wenling on 2017/10/13.
 */
class MarketingSuspendBillInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate redisTemplate

    @Autowired
    HttpSession session

    @Autowired
    SuspendBillURL suspendBillPage

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Map<String, String> params = URLUtils.splitQuery(request.getQueryString())
        String userSerializeString = suspendBillPage.cachedValue(params.uuid)
        if (userSerializeString) {
            USER_RELATED.each { request.session.removeAttribute it }
            session.setAttribute(SESSION_KEY_USER, userSerializeString)
        }
        true
    }

}
