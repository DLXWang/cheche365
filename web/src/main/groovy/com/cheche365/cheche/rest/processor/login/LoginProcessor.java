package com.cheche365.cheche.rest.processor.login;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.MarketingRepository;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.core.repository.WechatUserChannelRepository;
import com.cheche365.cheche.core.repository.WechatUserInfoRepository;
import com.cheche365.cheche.core.service.UserLoginInfoService;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.core.util.IpUtil;
import com.cheche365.cheche.sms.client.service.ValidatingService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cheche365.cheche.core.constants.SmsConstants._SMS_PRODUCT_CHECHE;
import static com.cheche365.cheche.core.constants.SmsConstants._SMS_PRODUCT_KEY;

/**
 * Created by zhaozhong on 2015/9/14.
 */
public abstract class LoginProcessor extends ContextResource {

    private static Logger logger = LoggerFactory.getLogger(LoginProcessor.class);

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ValidatingService validationCodeService;

    @Autowired
    UserLoginInfoService userLoginInfoService;

    @Autowired
    HttpServletRequest request;
    @Autowired
    private WechatUserInfoRepository wechatUserInfoRepository;
    @Autowired
    private WechatUserChannelRepository wechatUserChannelRepository;
    @Autowired
    MarketingRepository marketingRepository;

    abstract List<Channel> getSupportClientType();

    public abstract <T> T hasLogin();

    public abstract <T> T login(LoginInfo loginInfo);

    public abstract <T> T register(RegisterInfo loginInfo);


    User createNewUser(LoginInfo loginInfo) {
        User user = getUserInSession();
        if (user != null) {
            userService.boundMobile(user, loginInfo.getMobile());
        } else {
            user = userService.createUser(loginInfo.getMobile(), ClientTypeUtil.getChannel(getRequest()), null);
        }
        BusinessActivity cpsBusinessActivity = businessActivity();
        if (cpsBusinessActivity != null && cpsBusinessActivity.checkActivityDate()) {
            user.setSourceType(OrderSourceType.Enum.CPS_CHANNEL_1);
            user.setSourceId(String.valueOf(cpsBusinessActivity.getId()));
        }
        setRegisterIpAndChannel(user);
        userRepository.save(user);
        return user;
    }

    protected User getUserInSession() {
        return safeGetCurrentUser();
    }

    /**
     * bind不允许调用
     */
    @Transactional
    public User bind(LoginInfo loginInfo) {
        if (!isOpenWithOauth()) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "client for " + getSupportClientType().toString() + " , api bind is not allowed");
        }else {
            validLoginInfo(loginInfo);
            final User userInSession = getCurrentUser(), userInDB = userService.getUserByMobile(loginInfo.getMobile());
            //登陆操作
            User user = doBindProcess(userInSession, userInDB, loginInfo);
            updateUserLoginInfo(user);
            setSubscribed(user);
            return user;
        }
    }

    User getCurrentUser(){
        if(Channel.Enum.PARTNER_CHEBAOYI_67.equals(getChannel())){
            return safeGetCurrentUser();
        }else {
            return currentUser();
        }
    }

    void setRegisterIpAndChannel(User user) {
        if (user != null && user.getRegisterChannel() == null) {
            HttpServletRequest request = getRequest();
            user.setRegisterIp(IpUtil.getIP(request));
            user.setRegisterChannel(ClientTypeUtil.getChannel(request));
        }
    }

    HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    void updateUserLoginInfo(User user) {
        userLoginInfoService.updateUserLoginInfo(user, IpUtil.getIP(getRequest()), ClientTypeUtil.getChannel(getRequest()));
    }

    void validNull(LoginInfo loginInfo) {
        if (StringUtils.isBlank(loginInfo.getMobile()) || StringUtils.isBlank(loginInfo.getVerificationCode())) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "请输入手机号和验证码信息");
        }
    }

    void validValidationCode(LoginInfo loginInfo) {
        Map<String, String> additionalParam = new HashMap<>();
        additionalParam.put(_SMS_PRODUCT_KEY, _SMS_PRODUCT_CHECHE);
        validationCodeService.validate(loginInfo.getMobile(), loginInfo.getVerificationCode(), additionalParam);
    }

    void validLoginInfo(LoginInfo loginInfo) {
        validNull(loginInfo);
        validValidationCode(loginInfo);
    }

    boolean userEquals(User u1, User u2) {
        return null != u1 && null != u2 && Objects.equals(u1.getId(), u2.getId());
    }
    /**
     * 判断是否是通过oauth方式打开
     * 认为session和wechatUserInfo都存在，则是已oauth方式打开的
     *
     * @return
     */
    Boolean isOpenWithOauth() {
        List<Channel> oAuthChannel = oAuthChannels();
        if(oAuthChannel.contains(getChannel())){
            User user = safeGetCurrentUser();
            return null != user && null != wechatUserInfoRepository.findFirstByUser(user);
        }else {
            return false;
        }

    }

    private List<Channel> oAuthChannels() {
        List<Channel> oAuthChannel = new ArrayList<>();
        oAuthChannel.add(Channel.Enum.WE_CHAT_3);
        oAuthChannel.add(Channel.Enum.WE_CHAT_APP_39);
        oAuthChannel.add(Channel.Enum.PARTNER_CHEBAOYI_67);
        return oAuthChannel;
    }

    /**
     * 登陆操作
     *
     * @param userInSession session用户信息
     * @param userInDB      根据mobile查询出的用户信息
     * @param loginInfo     用户登录信息
     * @return User对象
     */
    User doBindProcess(User userInSession, User userInDB, LoginInfo loginInfo) {
        User resultUser;
        if (
            null == (resultUser = opsForEquals(userInSession, userInDB, loginInfo))
                && null == (resultUser = opsForNoBoundAndMobileNoUsed(userInSession, userInDB, loginInfo))
                && null == (resultUser = opsForHasBoundAndMobileNoUsed(userInSession, userInDB, loginInfo))
                && null == (resultUser = opsForMobileHasUsed(userInSession, userInDB, loginInfo))
            ) {
            opsForHasBound(userInSession, userInDB, loginInfo);
            if (null == resultUser) {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "操作异常");
            }
        }
        return resultUser;
    }
    /**
     * session用户和mobile用户是同一个用户
     *
     * @param userInSession
     * @param userInDB
     * @param loginInfo
     * @return
     */
    User opsForEquals(User userInSession, User userInDB, LoginInfo loginInfo) {
        if (userEquals(userInSession, userInDB)) {
            logger.debug("Current user is bindingUser {}, Don't need to bind", loginInfo.getMobile());
            setUserFlag(userInSession);
            return userInSession;
        }
        return null;
    }
    /**
     * 原用户未绑定， mobile用户不存在，直接修改
     * 直接修改session中用户信息
     *
     * @param userInSession
     * @param userInDB
     * @param loginInfo
     * @return
     */
    User opsForNoBoundAndMobileNoUsed(User userInSession, User userInDB, LoginInfo loginInfo) {
        if (!Boolean.TRUE.equals(userInSession.isBound()) && null == userInDB) {
            userService.boundMobile(userInSession, loginInfo.getMobile());
            setUserFlag(userInSession);
            return userInSession;
        }
        return null;
    }

    /**
     * 原用户已经绑定，mobile用户不存在，新建一个用户并且切换
     *
     * @param userInSession
     * @param userInDB
     * @param loginInfo
     * @return
     */
    User opsForHasBoundAndMobileNoUsed(User userInSession, User userInDB, LoginInfo loginInfo) {
        if (Boolean.TRUE.equals(userInSession.isBound()) && null == userInDB) {
            User newUser = createNewUser(loginInfo);
            WechatUserInfo wechatUserInfoFromSession = wechatUserInfoRepository.findFirstByUser(userInSession);
            wechatUserInfoFromSession.setUser(newUser);
            wechatUserInfoRepository.save(wechatUserInfoFromSession);
            return newUser;
        }
        return null;
    }

    /**
     * 只要db中的user不是wechat用户就替换
     *
     * @param userInSession
     * @param userInDB
     * @param loginInfo
     * @return
     */
    User opsForMobileHasUsed(User userInSession, User userInDB, LoginInfo loginInfo) {
        final WechatUserInfo wechatUserInfoFromDB = (userInDB != null) ? wechatUserInfoRepository.findFirstByUser(userInDB) : null;
        if (null == wechatUserInfoFromDB) {
            logger.debug("Replace bindingUser '{}' to currentUser '{}', mobile {}", userInDB.getId(), userInSession.getId(), loginInfo.getMobile());
            setUserFlag(userInDB);
            final WechatUserInfo wechatUserInfoFromSession = wechatUserInfoRepository.findFirstByUser(userInSession);
            wechatUserInfoFromSession.setUser(userInDB);
            wechatUserInfoRepository.save(wechatUserInfoFromSession);
            return userInDB;
        }
        return null;
    }

    /**
     * 如果mobile已经是wechat用户，不允许绑定
     *
     * @param userInSession
     * @param userInDB
     * @param loginInfo
     * @return
     */
    User opsForHasBound(User userInSession, User userInDB, LoginInfo loginInfo) {
        final WechatUserInfo wechatUserInfoFromDB = userInDB != null ? wechatUserInfoRepository.findFirstByUser(userInDB) : null;
        if (null != wechatUserInfoFromDB) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "这个电话号码已经被绑定到其他微信用户");
        }
        return null;
    }

    /**
     * 设置User Wechat标志字段，不序列化到DB中，前端使用
     *
     * @param user
     */
    void setUserFlag(User user) {
        user.setWechatUser(Boolean.TRUE);
    }

    void setSubscribed(User user) {
        WechatUserChannel wechatUserChannel = wechatUserChannelRepository.findLastByUserChannel(user, ClientTypeUtil.getChannel(request));
        user.setUnsubscribed(null == wechatUserChannel ? true : wechatUserChannel.isUnsubscribed()); //null的情况为了处理非oauth进入车车的情况，如直接通过微信打开M站
    }

    User doLogin(LoginInfo loginInfo) {
        User user;

        if (marketingLogin(loginInfo)) return safeGetCurrentUser();

        if (isOpenWithOauth()) {
            user = bind(loginInfo);
        } else {
            validLoginInfo(loginInfo);
            user = findOrCreateNewUser(loginInfo);
            updateUserLoginInfo(user);
        }
        if (oAuthChannels().contains(getChannel())) {
            setSubscribed(user);
        }
        return user;
    }

    /**
     * 如果当前登陆请求是，活动页发送过来的，如果当前session有user，此时只做的验证码校验，不持久化活动页发送的login请求的mobile，
     * 解决问题是如果当前session中有user，活动页发送的login请求mobile和session中mobile不一致，防止篡改当前session中的user，
     * 以及与wechatUserInfo的绑定关系
     * @param loginInfo
     * @return
     */

    private boolean marketingLogin(LoginInfo loginInfo) {
        String referer = request.getHeader(HttpHeaders.REFERER);
        if (StringUtils.isNotEmpty(referer) && referer.contains("/marketing/m/") && safeGetCurrentUser() != null) {
            validLoginInfo(loginInfo);
            Pattern p = Pattern.compile("(\\d{9})");
            Matcher m = p.matcher(referer);
            if (m.find()){
                String code = m.group();
                Marketing marketing = marketingRepository.findFirstByCode(code);
                return !marketing.attendCreateUser();
            }
            return true;
        }
        return false;
    }

    User findOrCreateNewUser(LoginInfo loginInfo) {
        User user = userService.getUserByMobile(loginInfo.getMobile());
        if (null == user) {
            user = createNewUser(loginInfo);
            setRegisterIpAndChannel(user);
            userRepository.save(user);
        }
        return user;
    }
}
