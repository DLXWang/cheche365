package com.cheche365.cheche.wechat.web.controller;


import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository;
import com.cheche365.cheche.web.service.ChannelAgentService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.core.util.URLUtils;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.wechat.PublicAccountManager;
import com.cheche365.cheche.wechat.WechatUserHandler;
import com.cheche365.cheche.wechat.message.json.OAuthResult;
import com.cheche365.cheche.core.model.WechatUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.cheche365.cheche.wechat.PublicAccountManager.*;

@Controller
public class WechatOAuthCallback {

    private Logger logger = LoggerFactory.getLogger(WechatOAuthCallback.class);

    @Autowired
    private PublicAccountManager publicAccountManager;
    @Autowired
    private WechatUserHandler userHandler;

    @Autowired
    private ChannelAgentRepository channelAgentRepository;

    @Autowired
    private ChannelAgentService channelAgentService;

    @RequestMapping(WebConstants.WECHAT_OAUTH_CALLBACK)
    public String mCallback(@RequestParam String code, @RequestParam String state, HttpServletRequest request, HttpSession session) throws UnsupportedEncodingException {

        logger.info(String.format("received WeChat OAuth callback request, code [%s], state [%s]", code, state));

        if (StringUtils.isBlank(state)) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "WeChat OAuth callback参数异常，state为空");
        }

        Map stateParams = URLUtils.splitQuery(state);

        initParam(stateParams);

        OAuthResult oAuthResult = publicAccountManager.getOAuthResultByCode(code, getAppId(stateParams), getAppSecret(stateParams), getChannel(stateParams));
        WechatUserInfo wechatUserInfo = null;
        Boolean oauthOnly = Boolean.parseBoolean((String) stateParams.get("oauthonly"));
        if (!oauthOnly) {
            wechatUserInfo = userHandler.publicAccountSaveUserInfo(oAuthResult.getOpenid(), oAuthResult, getChannel(stateParams)).getWechatUserInfo();
        }
        initSession(request, session, stateParams, oAuthResult, wechatUserInfo);
        return getRedirectUrl(stateParams);
    }

    private void initSession(HttpServletRequest request, HttpSession session, Map stateParams, OAuthResult oAuthResult, WechatUserInfo wechatUserInfo) {

        Boolean oauthOnly = Boolean.parseBoolean((String) stateParams.get("oauthonly"));
        if (oauthOnly) {
            session.setAttribute(WebConstants.SESSION_KEY_WECHAT_OPEN_ID, oAuthResult.getOpenid());
        } else {
            User user = wechatUserInfo.getUser();
            CacheUtil.cacheUser(session, user);
            ClientTypeUtil.cacheChannel(request, getChannel(stateParams));
            session.setAttribute(WebConstants.SESSION_KEY_WECHAT_USER_INFO, CacheUtil.doJacksonSerialize(wechatUserInfo));
            session.setAttribute(WebConstants.OAUTH_CHANNEL, getChannel(stateParams).getId());
            channelAgentService.handleChannelAgentCache(request, user, getChannel(stateParams));
            stateParams.put("wechatUserInfo", wechatUserInfo);
        }
    }

}
