package com.cheche365.cheche.rest.interceptor

import com.cheche365.cheche.core.repository.MarketingRepository
import com.cheche365.cheche.core.service.OAuthUrlGenerator
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

import static com.cheche365.cheche.core.constants.WebConstants.SESSION_KEY_WECHAT_USER_INFO
import static com.cheche365.cheche.core.constants.WebConstants.WECHAT_OAUTH_CALLBACK
import static com.cheche365.cheche.core.constants.WebConstants.getMarketingUUIDSessionKey
import static com.cheche365.cheche.core.model.Channel.Enum.WE_CHAT_3
import static com.cheche365.cheche.core.util.URLUtils.splitQuery
import static com.cheche365.cheche.web.util.ClientTypeUtil.getChannel

/**
 * Created by liheng on 2018/1/23 023.
 */
@Slf4j
class MarketingInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    HttpSession session
    @Autowired
    private OAuthUrlGenerator oAuthUrlGenerator
    @Autowired
    private MarketingRepository marketingRepository

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        def params = splitQuery request.queryString

        if(!validateMarketingPath(request)){
            response.sendRedirect('/')
            return false
        }

        def marketingCode = (request.servletPath - '/marketing/m/').substring(0, 9)
        def marketing = marketingRepository.findFirstByCode marketingCode

        if (!marketing?.withUUID()) {
            session.removeAttribute getMarketingUUIDSessionKey(marketingCode)
        } else if (params.uuid) {
            session.setAttribute getMarketingUUIDSessionKey(marketingCode), params.uuid
        }

        if (WE_CHAT_3 == getChannel(request) && marketing?.needWechatOAuth() && !session.getAttribute(SESSION_KEY_WECHAT_USER_INFO)) {
            def state = [code: marketingCode, type: 'marketing', channelId: WE_CHAT_3.id, oauthonly: 'false']
            response.sendRedirect oAuthUrlGenerator.toOAuthUrl(WECHAT_OAUTH_CALLBACK, 'snsapi_userinfo', state)
            return false
        }

        true
    }

    static boolean validateMarketingPath(HttpServletRequest request){
        (request.servletPath - '/marketing/m/').with {
            it.indexOf("/") > 0 ? it.substring(0, it.indexOf("/")) : it
        }.with { code ->
            code.size() >= 9 && code.every { Character.isDigit(it as char) }

        }
    }

}
