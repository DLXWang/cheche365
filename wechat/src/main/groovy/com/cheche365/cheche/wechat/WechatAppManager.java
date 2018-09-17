package com.cheche365.cheche.wechat;

import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.wechat.message.json.OAuthResult;
import com.cheche365.cheche.core.model.WechatAppUserInfo;
import com.cheche365.cheche.core.model.WechatUserChannel;
import com.cheche365.cheche.wechat.util.WechatAppDecryptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Chenqc on 2016/10/20.
 */
@Component
public class WechatAppManager extends OAuthManager {

    private Logger logger = LoggerFactory.getLogger(WechatAppManager.class);

    public WechatAppUserInfo doOAuth(String code, String encryptedData, String iv, Channel channel) {

        OAuthResult oAuthResult = getOAuthResultByCode(code, WechatConstant.getAppId(channel), WechatConstant.getAppSecret(channel), channel);
        if (StringUtils.isNoneBlank(oAuthResult.getErrcode())) {
            throw new BusinessException(BusinessException.Code.EXTERNAL_SERVICE_ERROR, "OAuth异常，原因：" + oAuthResult.getErrmsg());
        }
        String openId = oAuthResult.getOpenid();
        if (logger.isDebugEnabled()) {
            logger.debug("openid is: " + openId);
        }
        WechatAppUserInfo wechatAppUserInfo = null;
        try {
            byte[] data = WechatAppDecryptor.decrypt(encryptedData, oAuthResult.getSession_key(), iv);
            logger.debug("解密得到的数据 : {}", new String(data, "UTF-8"));
            wechatAppUserInfo = CacheUtil.doJacksonDeserialize(new String(data, "UTF-8"), WechatAppUserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wechatAppUserInfo;
    }

    public WechatUserChannel saveUserInfo(WechatAppUserInfo wechatAppUserInfo, Channel channel) {
        return userHandler.wechatAppSaveUserInfo(wechatAppUserInfo.getOpenid(), wechatAppUserInfo, channel);
    }

    @Override
    public String getOAuthPath() {
        return "/sns/jscode2session";
    }

}
