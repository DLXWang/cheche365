package com.cheche365.cheche.manage.common.constants;

/**
 * Created by wangfei on 2015/9/1.
 */
public class ManageCommonConstants {
    // 登录错误消息
    public static final String LOGIN_ERROR_CODE = "email is not found";

    // 登录错误消息
    public static final String LOGIN_ERROR_MESSAGE = "登录失败，请确认您的邮箱和密码.";

    // 无权限错误消息
    public static final String AUTHENTICATION_ERROR_CODE = "no permission to login";

    // 无权限错误消息
    public static final String AUTHENTICATION_ERROR_MESSAGE = "您没有权限登录该系统.";

    // 权限错误编号
    public static final String ACCESS_ERROR_CODE = "access_error_message";

    // 权限错误消息
    public static final String ACCESS_ERROR_MESSAGE = "您没有权限执行该操作。请重新确认。";

    public static final String ROLE_PREFIX = "ROLE_";

    public static final String permission = "permission";

    public static final String RESET_PASSWORD_KEY = "internalUser:reset:password:set";

    // 用户锁定错误消息
    public static final String AUTHENTICATION_LOCKED_CODE = "user is locked";

    public static final Integer PASSWORD_ERROR_COUNT = 5;
    // 用户锁定错误消息
    public static final String AUTHENTICATION_LOCKED_MESSAGE = "您的账号已经被锁定.";

    public static final String RESET_PASSWORD_LOCK_KEY = "internalUser:reset:password:lock:set";

    public static final String USER_LOCK_KEY = "internalUser:operation:lock";
}
