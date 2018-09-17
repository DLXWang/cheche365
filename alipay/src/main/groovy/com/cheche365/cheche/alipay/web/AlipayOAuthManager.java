package com.cheche365.cheche.alipay.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserUserinfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserUserinfoShareResponse;
import com.cheche365.cheche.alipay.constants.AlipayServiceEnvConstants;
import com.cheche365.cheche.alipay.util.RequestUtil;
import com.cheche365.cheche.alipay.web.factory.AlipayAPIClientFactory;
import com.cheche365.cheche.core.model.AlipayUserInfo;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Gender;
import com.cheche365.cheche.core.model.IdentityType;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.UserSource;
import com.cheche365.cheche.core.repository.AlipayUserInfoRepository;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.core.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * zhengwei于2016/01/21修改@Autowired HttpSession代码，改为从spring上下文中读取当前request，再从request中获取session。否则定时任务依赖支付宝项目会有问题。
 */
@Component
public class AlipayOAuthManager {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AlipayUserInfoRepository alipayUserInfoRepository;
    @Autowired
    private UserService userService;

    private static final String ALIPAY_ACCESSTOKEN_KEY = "ALIPAY_ACCESSTOKEN_KEY";

    private static final Logger logger = LoggerFactory.getLogger(AlipayOAuthManager.class);

    public User authenticate(HttpServletRequest request) {
        return authenticate(request, Channel.Enum.ALIPAY_21);
    }

    public User authenticate(HttpServletRequest request, Channel channel) {
        Map<String, String> params = RequestUtil.getRequestParams(request);
        String authCode = params.get("auth_code");
        AlipaySystemOauthTokenRequest oauthTokenRequest = new AlipaySystemOauthTokenRequest();
        oauthTokenRequest.setCode(authCode);
        oauthTokenRequest.setGrantType(AlipayServiceEnvConstants.GRANT_TYPE);
        AlipayClient alipayClient = AlipayAPIClientFactory.getAlipayClient();
        User user = null;
        try {
            logger.debug("获取 OauthToken Start --> {}", oauthTokenRequest.getTextParams().toString());
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(oauthTokenRequest);
            logger.debug("获取 OauthToken End --> {}", oauthTokenResponse.getBody());
            AlipayUserInfo alipayUserInfo = getAlipayUserInfo(oauthTokenResponse, channel);
            user = alipayUserInfo.getUser();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("alipay oauth error", e);
        }
        return user;

    }

    @Transactional
    private AlipayUserInfo getAlipayUserInfo(AlipaySystemOauthTokenResponse oauthTokenResponse, Channel channel) {
        AlipayUserInfo alipayUserInfo = alipayUserInfoRepository.findByOpenid(oauthTokenResponse.getAlipayUserId());
        if (alipayUserInfo == null) {
            logger.debug("Create new alipayUserInfo");
            alipayUserInfo = createNewAlipayUserInfo(oauthTokenResponse, channel);
        }
        cacheAccessToken(oauthTokenResponse.getAccessToken());
        return alipayUserInfo;
    }

    private AlipayUserInfo createNewAlipayUserInfo(AlipaySystemOauthTokenResponse userinfoShareResponse, Channel channel) {
        AlipayUserInfo userInfo = new AlipayUserInfo();
        userInfo.setOpenid(userinfoShareResponse.getAlipayUserId());
        userInfo.setCreateTime(Calendar.getInstance().getTime());

        userInfo.setUser(userService.createUser(null,channel,UserSource.Enum.ALIPAY_WALLET));
        alipayUserInfoRepository.save(userInfo);
        return userInfo;
    }

    private AlipayUserUserinfoShareResponse requestAlipayUserUserinfoShareResponse(String accessToken) {
        AlipayClient alipayClient = AlipayAPIClientFactory.getAlipayClient();
        AlipayUserUserinfoShareRequest userinfoShareRequest = new AlipayUserUserinfoShareRequest();
        AlipayUserUserinfoShareResponse userinfoShareResponse = null;
        try {
            logger.debug("获取 UserUserinfoShare Start --> {}", userinfoShareRequest.getTextParams().toString());
            userinfoShareResponse = alipayClient.execute(userinfoShareRequest, accessToken);
            logger.debug("获取 UserUserinfoShare End --> {} ", userinfoShareResponse.getBody());
        } catch (AlipayApiException e) {
            logger.error("alipay oauth error", e);
            e.printStackTrace();
        }
        return userinfoShareResponse;
    }

    public void oauthAlipayUserInfo(AlipayUserInfo alipayUserInfo) {
        AlipayUserUserinfoShareResponse userinfoShareResponse = requestAlipayUserUserinfoShareResponse(getAccessToken());
        updateAlipayUserInfo(alipayUserInfo, userinfoShareResponse);
        removeAccessToken();
    }


    @Transactional
    private void updateAlipayUserInfo(AlipayUserInfo alipayUserInfo, AlipayUserUserinfoShareResponse userinfoShareResponse) {
        if (null != userinfoShareResponse && userinfoShareResponse.isSuccess()) {
            User user = alipayUserInfo.getUser();
            if (Convert.convertBoolean(userinfoShareResponse.getIsMobileAuth()) && StringUtils.isNotBlank(userinfoShareResponse.getMobile())) {
                //update AlipayUserInfo use mobile
                User existUser = userService.getUserByMobile(userinfoShareResponse.getMobile());
                if(existUser != null) {
                    user = existUser;
                    alipayUserInfo.setUser(user);
                }else {
                    userService.boundMobile(user, userinfoShareResponse.getMobile());
                }
            }
            String certNo = StringUtils.trim(userinfoShareResponse.getCertNo());
            setValue(user::setName, user::getName, userinfoShareResponse.getRealName());
            setValue(user::setGender, user::getGender, Convert.convertGender(userinfoShareResponse.getGender()));
            if(StringUtils.isNotBlank(certNo) && (15 == certNo.length() || 18 == certNo.length())) {
                setValue(user::setIdentityType, user::getIdentityType, Convert.convertIdentityType(userinfoShareResponse.getCertTypeValue()));
                setValue(user::setIdentity, user::getIdentity, userinfoShareResponse.getCertNo());
            }
            userRepository.save(user);
            syncAlipayUserInfo(userinfoShareResponse, alipayUserInfo);
            alipayUserInfoRepository.save(alipayUserInfo);
        }
    }

    private <T> void setValue(Consumer<T> setter, Supplier<T> getter, T value) {
        if ((null == getter.get() || StringUtils.isBlank(String.valueOf(getter.get()))) && null != value) {
            setter.accept(value);
        }
    }

    private void syncAlipayUserInfo(AlipayUserUserinfoShareResponse userinfoShareResponse, AlipayUserInfo alipayUserInfo) {
        try {
            BeanUtil.copyPropertiesContain(userinfoShareResponse, alipayUserInfo);
            if(StringUtils.isBlank(alipayUserInfo.getCertTypeValue())) {//CertTypeValue is null default to 0
                alipayUserInfo.setCertTypeValue("0");
            }
        } catch (FatalBeanException ex) {
            logger.debug("copy userinfoShare error", ex);
        }
    }

    public void cacheAccessToken(String accessToken) {
        logger.debug("cache accessToken : " + accessToken);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        request.getSession().setAttribute(ALIPAY_ACCESSTOKEN_KEY, accessToken);
    }

    public String getAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return (String) request.getSession().getAttribute(ALIPAY_ACCESSTOKEN_KEY);
    }

    public void removeAccessToken() {
        logger.debug("remove accessToken : " + getAccessToken());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        request.getSession().removeAttribute(ALIPAY_ACCESSTOKEN_KEY);
    }

    public boolean hasAccessToken() {
        return StringUtils.isNotBlank(getAccessToken());
    }

}

class Convert {
    public static IdentityType convertIdentityType(String str) {
        IdentityType identityType = IdentityType.Enum.IDENTITYCARD;
        if (StringUtils.isNotBlank(str)) {
            switch (str) {
                case "0":
                    identityType = IdentityType.Enum.IDENTITYCARD;
                    break;
                case "1":
                    identityType = IdentityType.Enum.PASSPORT;
                    break;
                case "2":
                    identityType = IdentityType.Enum.OFFICERARD;
                    break;
                case "3":
                    identityType = IdentityType.Enum.RETURN_PERMIT;
                    break;
                case "4":
                    identityType = IdentityType.Enum.TEMPORARY_ID;
                    break;
                case "5":
                    identityType = IdentityType.Enum.RESIDENCE_BOOKLET;
                    break;
                case "6":
                    identityType = IdentityType.Enum.OFFICERS_CARD;
                    break;
                case "7":
                    identityType = IdentityType.Enum.MTP;
                    break;
                case "8":
                    identityType = IdentityType.Enum.BUSINESS_LICENSE;
                    break;
                case "9":
                    identityType = IdentityType.Enum.OTHER_IDENTIFICATION;
                    break;
                default:
                    identityType = IdentityType.Enum.IDENTITYCARD;
            }
        }
        return identityType;
    }

    public static Gender convertGender(String str) {
        return "F".equals(str) ? Gender.Enum.FEMALE : "M".equals(str) ? Gender.Enum.MALE : null;
    }

    public static Boolean convertBoolean(String str) {
        return "T".equalsIgnoreCase(str) ? Boolean.TRUE : "F".equalsIgnoreCase(str) ? Boolean.FALSE : Boolean.FALSE;
    }

}
