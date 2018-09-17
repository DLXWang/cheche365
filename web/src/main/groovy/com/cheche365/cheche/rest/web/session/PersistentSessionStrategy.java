package com.cheche365.cheche.rest.web.session;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.TokenTimeOutException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhengwei on 5/20/15.
 * session统一处理机制，根据不同客户端类型采用相应策略处理session相关操作
 */
public class PersistentSessionStrategy implements HttpSessionStrategy {

    @Autowired
    private MobileSessionHandler mobileSessionHandler;
    @Autowired
    private WeChatAppSessionHandler weChatAppSessionHandler;
    @Autowired
    private OrderCenterSessionHandler orderCenterSessionHandler;
    @Autowired
    private ClaimAppSessionHandler claimAppSessionHandler;

    private CookieHttpSessionStrategy mSiteCookieStrategy;


    public PersistentSessionStrategy() {
        mSiteCookieStrategy = new CookieHttpSessionStrategy();
    }

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {

        boolean isMobileClientType = ClientTypeUtil.isMobileClientType(request);

        if (isMobileClientType) {
            String mobileSessionId = getInternalSession(request, ClientTypeUtil.getMobileClientTypeByRequest(request));
            if (null != mobileSessionId)
                return mobileSessionId;

        } else if (weChatAppSessionHandler.isWechatApp(request)) {
            return weChatAppSessionHandler.get(request);

        } else if (claimAppSessionHandler.isClaimApp(request)) {
            return claimAppSessionHandler.get(request);

        } else if (orderCenterSessionHandler.isOrderCenter(request)) {
            return orderCenterSessionHandler.get(request);
        }

        return mSiteCookieStrategy.getRequestedSessionId(request);
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {

        Channel channel = ClientTypeUtil.getMobileClientTypeByRequest(request);
        if (request.getHeader(WebConstants.ORDER_CENTER_TOKEN)!=null) {
            request.setAttribute(WebConstants.ORDER_CENTER_TOKEN,true);
            throw new TokenTimeOutException("token已过期,请重新获取token", null);
        }
        if (channel != null) {
            this.mobileSessionHandler.updateSessionId(request, session, channel);
        }

        mSiteCookieStrategy.onNewSession(session, request, response);
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
        mSiteCookieStrategy.onInvalidateSession(request, response);
    }

    private String getInternalSession(HttpServletRequest request, Channel channel) {
        return this.mobileSessionHandler.get(request, channel);
    }

}
