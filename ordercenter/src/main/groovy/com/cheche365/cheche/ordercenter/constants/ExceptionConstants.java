package com.cheche365.cheche.ordercenter.constants;

/**
 * Created by guoweifu on 2015/11/9.
 */
public class ExceptionConstants {
    // 登录错误消息
    public static final String LOGIN_ERROR_CODE = "email is not found";

    // 登录错误消息
    public static final String LOGIN_ERROR_MESSAGE = "登录失败，请确认您的邮箱和密码.";

    // 无权限错误消息
    public static final String AUTHENCATION_ERROR_CODE = "no permission to login";

    // 无权限错误消息
    public static final String AUTHENCATION_ERROR_MESSAGE = "您没有权限登录该系统.";

    // 权限错误编号
    public static final String ACCESS_ERROR_CODE = "access_error_message";

    // 权限错误消息
    public static final String ACCESS_ERROR_MESSAGE = "您没有权限执行该操作。请重新确认。";

    // 用户锁定错误消息
    public static final String AUTHENTICATION_LOCKED_CODE = "user is locked";

    // 用户锁定错误消息
    public static final String AUTHENTICATION_LOCKED_MESSAGE = "您的账号已经被锁定.";
}
