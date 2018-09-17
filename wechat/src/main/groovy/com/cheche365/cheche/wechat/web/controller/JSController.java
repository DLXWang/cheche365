package com.cheche365.cheche.wechat.web.controller;

import com.cheche365.cheche.common.util.HashUtils;
import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.wechat.AccessTokenManager;
import com.cheche365.cheche.wechat.WechatAppManager;
import com.cheche365.cheche.core.model.WechatAppUserInfo;
import com.cheche365.cheche.core.model.WechatUserChannel;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cheche365.cheche.core.WechatConstant.APP_ID;

/**
 * Created by liqiang on 4/11/15.
 */

@Controller
public class JSController {

    public static final String FLOW_SESSION_ONLY = "sessionOnly";  //小程序只生成session id流程，不处理oauth相关信息，目前使用用户拒绝授权小程序情况
    @Autowired
    private AccessTokenManager accessTokenManager;

    @Autowired
    private WechatAppManager wechatAppManager;

    @Autowired(required = false)
    private HttpServletRequest request;

    private Logger logger = LoggerFactory.getLogger(JSController.class);

    @RequestMapping(value = "/web/wechat/js", method = RequestMethod.GET)
    @ResponseBody
    public JsConfig config(@RequestParam(required = false) String page) {
        if (logger.isDebugEnabled()) {
            logger.debug("JSController config method is invoked.");
        }
        JsConfig jsConfig = new JsConfig();
        jsConfig.setAppid(WechatConstant.APP_ID);
        jsConfig.setNonceStr(RandomStringUtils.randomAlphanumeric(32));
        jsConfig.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000));

        String jsticket = accessTokenManager.getJSTicket(APP_ID);
        if (logger.isDebugEnabled()) {
            logger.debug("ticket is: " + jsticket);
        }

        List<String> params = new ArrayList<>();
        params.add("noncestr=" + jsConfig.getNonceStr());
        params.add("timestamp=" + jsConfig.getTimestamp());
        params.add("jsapi_ticket=" + jsticket);
        params.add("url=" + page);
        Collections.sort(params);

        StringBuilder stringBuilder = new StringBuilder();
        params.forEach(e -> stringBuilder.append(e).append("&"));
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        if (logger.isDebugEnabled()) {
            logger.debug("temp string is: " + stringBuilder.toString());
        }
        String tempString = HashUtils.sha1(stringBuilder.toString());
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("calculated signature [%s]", tempString));
        }
        jsConfig.setSignature(tempString);

        return jsConfig;
    }

    @RequestMapping(value = "/web/wechatapp/session", method = RequestMethod.POST)
    @ResponseBody
    public Map wechatAppHandShake(@RequestBody String jsonStr) {
        JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        HttpSession session;
        User user = null;
        Channel channel = getWechatAppChannel(jsonObj);

        if (FLOW_SESSION_ONLY.equals(jsonObj.get("flow"))) {
            logger.debug("微信小程序用户进入session only流程，跳过模拟登陆步骤");
            session = initSession(null, channel);
        } else {
            WechatUserChannel wechatUserChannel = initWechatUser(jsonObj);
            user = wechatUserChannel.getWechatUserInfo().getUser();
            session = initSession(wechatUserChannel.getWechatUserInfo().getUser(), wechatUserChannel.getChannel());
        }

        Map result = new HashMap();
        result.put("sessionId", session.getId());
        result.put("isLogin", null != user && (Boolean.TRUE.equals(user.isBound())));

        return result;
    }

    private Channel getWechatAppChannel(JSONObject jsonObj) {
        return jsonObj.get("channelId") == null ? Channel.Enum.WE_CHAT_APP_39 : Channel.toChannel(Long.parseLong(String.valueOf(jsonObj.get("channelId"))));
    }

    private WechatUserChannel initWechatUser(JSONObject jsonObj) {
        String code = (String) jsonObj.get("code");
        JSONObject wechatAppUserInfo = jsonObj.getJSONObject("wechatAppUserInfo");
        String encryptedData = (String) wechatAppUserInfo.get("encryptedData");
        String iv = (String) wechatAppUserInfo.get("iv");
        Channel channel = getWechatAppChannel(jsonObj);

        WechatAppUserInfo userInfo = wechatAppManager.doOAuth(code, encryptedData, iv, channel);
        WechatUserChannel wechatUserChannel = wechatAppManager.saveUserInfo(userInfo, channel);
        if (wechatUserChannel == null) {
            throw new BusinessException(BusinessException.Code.EXTERNAL_SERVICE_ERROR, "微信小程序授权失败！！！");
        }
        logger.debug("微信小程序用户 union id: {}, 对应车车用户{}", wechatUserChannel.getWechatUserInfo().getUnionid(), wechatUserChannel.getWechatUserInfo().getUser().getId());
        return wechatUserChannel;
    }

    private HttpSession initSession(User user, Channel channel) {
        HttpSession session = request.getSession(true);
        if (user != null) {
            CacheUtil.cacheUser(request.getSession(), user);
        }
        ClientTypeUtil.cacheChannel(request, channel);
        return session;
    }

}
