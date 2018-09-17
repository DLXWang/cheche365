package com.cheche365.cheche.wechat;

import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.wechat.message.json.OAuthResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenqc on 2016/10/19.
 */
public abstract class OAuthManager {

    @Autowired
    protected MessageSender messageSender;

    @Autowired
    protected WechatUserHandler userHandler;

    public abstract String getOAuthPath();

    public OAuthResult getOAuthResultByCode(String code, String appId, String secret, Channel channel) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("appid", appId);
        parameters.put("secret", secret);
        if (Channel.nativeWechatApp().contains(channel)) {
            parameters.put("js_code", code);
        } else {
            parameters.put("code", code);
        }
        parameters.put("grant_type", "authorization_code");
        OAuthResult oAuthResult = messageSender.getMessageForObject(getOAuthPath(), parameters, OAuthResult.class, false, appId);
        if (StringUtils.isNoneBlank(oAuthResult.getErrcode()) || oAuthResult.getOpenid() == null) {
            throw new BusinessException(BusinessException.Code.EXTERNAL_SERVICE_ERROR, "OAuth异常，原因：" + oAuthResult.getErrmsg());
        }
        return oAuthResult;
    }
}
