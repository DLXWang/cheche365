package com.cheche365.cheche.rest.processor.login;

import com.cheche365.cheche.alipay.web.AlipayOAuthManager;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.AlipayUserInfo;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.AlipayUserInfoRepository;
import com.cheche365.cheche.core.util.CacheUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by zhaozhong on 2015/12/18.
 */
@Order(1)
@Component
public class AlipayLoginProcessor extends NormalLoginProcessor {

    @Autowired
    private AlipayUserInfoRepository alipayUserInfoRepository;
    @Autowired
    private AlipayOAuthManager alipayOAuthManager;

    private static final Logger logger = LoggerFactory.getLogger(AlipayLoginProcessor.class);

    /**
     * 支持客户端类型:支付宝
     */
    @Override
    public List<Channel> getSupportClientType() {
        return Arrays.asList(Channel.Enum.ALIPAY_21);
    }

    /**
     * hasLogin接口，会调用share_info，获取用户数据
     */
    @Override
    public User hasLogin() {
        User user = super.hasLogin();
        AlipayUserInfo alipayUserInfo;
        if (null != user && null != (alipayUserInfo = alipayUserInfoRepository.findFirstByUser(user))) {
            syncAlipayShareData(alipayUserInfo);
            user = alipayUserInfo.getUser();
            setUserFlag(user);
        }
        return user;
    }

    @Override
    User opsForHasBoundAndMobileNoUsed(User userInSession, User userInDB, LoginInfo loginInfo) {
        if (Boolean.TRUE.equals(userInSession.isBound()) && null == userInDB) {
            User newUser = createNewUser(loginInfo);
            AlipayUserInfo alipayUserInfoFromSession = alipayUserInfoRepository.findFirstByUser(userInSession);
            alipayUserInfoFromSession.setUser(newUser);
            alipayUserInfoRepository.save(alipayUserInfoFromSession);
            return newUser;
        }
        return null;
    }

    /**
     * 添加用户信息复制
     */
    @Override
    User opsForMobileHasUsed(User userInSession, User userInDB, LoginInfo loginInfo) {
        final AlipayUserInfo alipayUserInfoFromDB = userInDB != null ? alipayUserInfoRepository.findFirstByUser(userInDB) : null;
        if (null == alipayUserInfoFromDB) {
            logger.debug("Replace bindingUser '{}' to currentUser '{}', mobile {}", userInDB.getId(), userInSession.getId(), loginInfo.getMobile());

            setValue(userInDB::setName, userInSession::getName, userInSession.getName());
            setValue(userInDB::setIdentity, userInSession::getIdentity, userInSession.getIdentity());
            setValue(userInDB::setIdentityType, userInSession::getIdentityType, userInSession.getIdentityType());
            setUserFlag(userInDB);

            final AlipayUserInfo alipayUserInfoFromSession = alipayUserInfoRepository.findFirstByUser(userInSession);
            alipayUserInfoFromSession.setUser(userInDB);
            alipayUserInfoRepository.save(alipayUserInfoFromSession);
            return userInDB;
        }
        return null;
    }

    /**
     * 如果mobile已经是支付宝用户，不允许绑定
     */
    User opsForHasBound(User userInSession, User userInDB, LoginInfo loginInfo) {
        final AlipayUserInfo alipayUserInfoFromDB = userInDB != null ? alipayUserInfoRepository.findFirstByUser(userInDB) : null;
        if (null != alipayUserInfoFromDB) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "这个电话号码已经被绑定到其他支付宝用户");
        }
        return null;
    }

    @Override
    void setUserFlag(User user) {
        user.setAlipayUser(Boolean.TRUE);
    }

    @Override
    Boolean isOpenWithOauth() {
        User user = safeGetCurrentUser();
        return null != user && null != alipayUserInfoRepository.findFirstByUser(user);
    }

    private <T> void setValue(Consumer<T> setter, Supplier<T> getter, T value) {
        if ((null == getter.get() || StringUtils.isBlank(String.valueOf(getter.get()))) && null != value) {
            setter.accept(value);
        }
    }

    /**
     * 同步支付宝share_info信息
     */
    private void syncAlipayShareData(AlipayUserInfo alipayUserInfo) {
        final User user = alipayUserInfo.getUser();
        //TODO alipay信息可能会更新
        if (alipayOAuthManager.hasAccessToken()
            && (
            StringUtils.isBlank(alipayUserInfo.getCertNo()) ||
                StringUtils.isBlank(alipayUserInfo.getRealName()) ||
                StringUtils.isBlank(user.getIdentity()) ||
                StringUtils.isBlank(user.getName())
        )
            ) {
            logger.debug("start sync alipay_share_info start at {}", new Date());
            alipayOAuthManager.oauthAlipayUserInfo(alipayUserInfo);
            CacheUtil.cacheUser(request.getSession(), user);
            logger.debug("start sync alipay_share_info end at {}", new Date());
        }
    }
}
