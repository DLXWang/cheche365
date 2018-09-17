package com.cheche365.cheche.ordercenter.session;

import com.cheche365.cheche.ordercenter.third.clink.ClinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * session统一处理机制，监听session失效 清空client
 */
public class ClearSessionStrategy implements HttpSessionStrategy {


    private CookieHttpSessionStrategy mSiteCookieStrategy;
    @Autowired
    private ClinkService clinkService;


    public ClearSessionStrategy() {
        mSiteCookieStrategy = new CookieHttpSessionStrategy();

        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(OrderCenterRedisHttpSessionConfiguration.KEY_PREFIX + "_SESSION");
        serializer.setCookiePath("/");

        mSiteCookieStrategy.setCookieSerializer(serializer);
    }

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {

        return mSiteCookieStrategy.getRequestedSessionId(request);
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {


        mSiteCookieStrategy.onNewSession(session, request, response);
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
        mSiteCookieStrategy.onInvalidateSession(request, response);
    }

}
