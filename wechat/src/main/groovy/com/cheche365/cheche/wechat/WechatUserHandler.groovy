package com.cheche365.cheche.wechat

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.service.UserService
import com.cheche365.cheche.core.util.BeanUtil
import com.cheche365.cheche.core.util.IpUtil
import com.cheche365.cheche.web.util.ClientTypeUtil
import com.cheche365.cheche.wechat.message.json.OAuthResult
import com.cheche365.cheche.core.model.WechatAppUserInfo
import com.cheche365.cheche.core.model.WechatUserChannel
import com.cheche365.cheche.core.model.WechatUserInfo
import com.cheche365.cheche.core.repository.WechatUserChannelRepository
import com.cheche365.cheche.core.repository.WechatUserInfoRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest

/**
 * Created by zhengwei on 2016/11/22.
 * 统一处理公众号/小程序用户保存、更新相关功能，重构之前PublicAccountManager和UserManager代码，不管OAuth还是subscribe都统一处理
 * 微信用户和车车用户关系。
 */

@Service
class WechatUserHandler {
    private Logger logger = LoggerFactory.getLogger(WechatUserHandler.class);

    static final USER_INFO_FIELDS = ['city', 'province', 'country', 'nickname', 'headimgurl', 'language', 'unionid', 'sex']
    static final USER_CHANNEL_FIELDS = ['subscribe_time', 'subscribe']

    @Autowired
    private WechatUserInfoRepository wechatUserInfoRepository;
    @Autowired
    private WechatUserChannelRepository wechatUserChannelRepository;
    @Autowired
    private UserManager userManager;
    @Autowired
    private UserService userService
    @Autowired
    private UserRepository userRepository;
    @Autowired(required = false)
    public HttpServletRequest request;

    /**
     * 微信公众号使用，由于OAuth只能返回openId等信息，没有用户相关信息（如昵称），所以需要调用微信API通过
     * openId获取到用户信息。
     * @param openId
     * @return
     */
    WechatUserChannel publicAccountSaveUserInfo(String openId, OAuthResult oAuthResult, Channel channel) {
        WechatUserInfo userInfoFromWechat = new WechatUserInfo();
        WechatUserChannel userChannelFromWechat = new WechatUserChannel();
        Map infoMap;
        if (oAuthResult?.scope?.contains('snsapi_userinfo')) {
            infoMap = userManager.getUserInfo(openId, oAuthResult.getAccess_token(), "/sns/userinfo", Map.class, channel);
        } else {
            infoMap = userManager.getUserInfo(openId, Map.class, channel);
        }
        copyFields(userInfoFromWechat, infoMap, USER_INFO_FIELDS)
        logger.debug("wechat_user_channel 表subscribe更新，needUpdate:{},oAuthResult:{}", infoMap?.keySet()?.contains(USER_CHANNEL_FIELDS[0]), oAuthResult)
        if (infoMap?.keySet()?.contains(USER_CHANNEL_FIELDS[0])) {
            copyFields(userChannelFromWechat, infoMap, USER_CHANNEL_FIELDS)
        }

        userChannelFromWechat.setWechatUserInfo(userInfoFromWechat);
        userChannelFromWechat.setOpenId(openId);
        userChannelFromWechat.setChannel(channel);
        return saveUserChannel(userChannelFromWechat);
    }

    /**
     * 小程序使用，小程序登陆后能直接拿到用户信息。
     * @param openId
     * @param wechatAppUser
     * @return
     */
    WechatUserChannel wechatAppSaveUserInfo(String openId, WechatAppUserInfo wechatAppUserInfo, Channel channel) {
        WechatUserInfo userInfoFromWechat = new WechatUserInfo();
        WechatUserChannel userChannelFromWechat = new WechatUserChannel();
        setWechatUserInfo(wechatAppUserInfo, userInfoFromWechat);
        userChannelFromWechat.setWechatUserInfo(userInfoFromWechat);
        userChannelFromWechat.setOpenId(openId);
        userChannelFromWechat.setChannel(channel);
        return saveUserChannel(userChannelFromWechat);
    }

    WechatUserChannel saveUserChannel(WechatUserChannel userChannelFromWechat) {
        WechatUserChannel userChannelInDB = wechatUserChannelRepository.findFirstByOpenId(userChannelFromWechat.getOpenId());
        WechatUserInfo userInfo;
        if (null != userChannelInDB) {
            userInfo = userChannelInDB.getWechatUserInfo();
            copyFields(userInfo, userChannelFromWechat.wechatUserInfo, USER_INFO_FIELDS);
            logger.debug("微信用户 {} 在channel {} 下已存在", userChannelInDB.getOpenId(), userChannelInDB.getChannel().getName());
        } else {
            String unionId = userChannelFromWechat.getWechatUserInfo().getUnionid();
            logger.debug("用户的unionid是：{}", unionId);
            WechatUserInfo userInfoInDB = null;
            if (null != unionId) {
                userInfoInDB = wechatUserInfoRepository.findFirstByUnionid(unionId);
            }
            if (userInfoInDB == null) {
                userInfo = userChannelFromWechat.getWechatUserInfo();
                logger.debug("微信用户 {} 首次通过微信公众号进入车车", userChannelFromWechat.getOpenId());
            } else {
                copyFields(userInfoInDB, userChannelFromWechat.wechatUserInfo, USER_INFO_FIELDS);
                userInfo = userInfoInDB;
                logger.debug("微信用户 {} 已存在，但在channel {} 没有记录open id, 新建open id ", userChannelFromWechat.getOpenId(), Channel.Enum.WE_CHAT_3.getName());
            }
            userChannelInDB = new WechatUserChannel().setChannel(userChannelFromWechat.getChannel()).setWechatUserInfo(userInfo).setOpenId(userChannelFromWechat.getOpenId());
        }
        copyFields(userChannelInDB, userChannelFromWechat, USER_CHANNEL_FIELDS);
        userChannelInDB.unsubscribed = false
        this.saveWechatUserInfo(userInfo);
        return wechatUserChannelRepository.save(userChannelInDB);
    }


    WechatUserInfo saveWechatUserInfo(WechatUserInfo wechatUserInfo) {
        User user = wechatUserInfo.getUser();
        Channel channel = ClientTypeUtil.getChannel(request);
        if (user == null) {
            user = userService.createUser(null,channel,null)
            setRegisterIpAndChannel(user);
            userRepository.save(user)
            wechatUserInfo.setUser(user);
        }

        return wechatUserInfoRepository.save(wechatUserInfo);
    }

    static setWechatUserInfo(WechatAppUserInfo wechatAppUserInfo, WechatUserInfo userInfo) {
        WechatUserInfo wechatUserInfo = userInfo == null ? new WechatUserInfo() : userInfo;
        BeanUtil.copyPropertiesIgnore(wechatAppUserInfo, wechatUserInfo, "id");
    }

    static setRegisterIpAndChannel(User user) {
        if (user != null && user.getRegisterChannel() == null) {
            final String ip = IpUtil.getIP(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
            user.setRegisterIp(ip);
            user.setRegisterChannel(Channel.Enum.WE_CHAT_3);
        }
    }

    static copyFields(oldObj, newObj, List fields) {
        fields.each {oldObj[it] = newObj[it] }
    }

}
