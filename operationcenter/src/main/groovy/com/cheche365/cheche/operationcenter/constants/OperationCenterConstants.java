package com.cheche365.cheche.operationcenter.constants;

import org.slf4j.Logger;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
public class OperationCenterConstants {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(OperationCenterConstants.class);

    // 登录错误消息
    public static final String LOGIN_ERROR_CODE = "email is not found";

    // 登录错误消息
    public static final String LOGIN_ERROR_MESSAGE = "登录失败，请确认您的邮箱和密码.";

    // 无权限错误消息
    public static final String AUTHENTICATION_ERROR_CODE = "no permission to login";

    // 用户锁定错误消息
    public static final String AUTHENTICATION_LOCKED_CODE = "user is locked";

    public static final Integer PASSWORD_ERROR_COUNT = 5;
    // 用户锁定错误消息
    public static final String AUTHENTICATION_LOCKED_MESSAGE = "您的账号已经被锁定.";

    // 无权限错误消息
    public static final String AUTHENTICATION_ERROR_MESSAGE = "您没有权限登录该系统.";

    // 权限错误编号
    public static final String ACCESS_ERROR_CODE = "access_error_message";

    // 权限错误消息
    public static final String ACCESS_ERROR_MESSAGE = "您没有权限执行该操作。请重新确认。";

    // 微信二维码渠道
    public static final String QRCODE_CHANNEL = "qrcode:channel:";

    // 微信二维码渠道：扫描
    public static final String WECHAT_QRCODE_SCAN = "scan";

    // 微信二维码渠道：关注
    public static final String WECHAT_QRCODE_SUBSCRIBE = "subscribe";

    // 微信二维码更新操作：更新渠道号并保存
    public static final Integer WECHAT_QRCODE_UPDATE_CREATE = 1;

    // 微信二维码更新操作：更新保存
    public static final Integer WECHAT_QRCODE_UPDATE_SAVE = 2;

    // 微信二维码渠道
    public static final String REDIS_QRCODE_CHANNEL_KEY = "qrcode:channel:";

    // 微信二维码渠道在redis中的key
    public static final String REDIS_QRCODE_CHANNEL_CODE_KEY = "orderCenter_qrcChannelId:";


    public static final String ROLE_PREFIX = "ROLE_";

    public static final String permission = "permission";

    public static final String RESET_PASSWORD_KEY = "internalUser:reset:password:set";

    public static final String RESET_PASSWORD_LOCK_KEY = "internalUser:reset:password:lock:set";

    public static final String USER_LOCK_KEY = "internalUser:operation:lock";

    public static String getInternalUserKey(Long internalUserId) {
        return "internalUser:operation:key:" + internalUserId;
    }

}
