package com.cheche365.cheche.scheduletask.constants;

/**
 * Created by guoweifu on 2015/12/4.
 */
public class ExceptionConstants {

    /**
     * 异常
     */

    // 初始化Velocity错误
    public static final String EXCEPTION_INIT_VELOCITY = "202";
    public static final String EXCEPTION_INIT_VELOCITY_MESSAGE = "init velocity error.";

    // 解析模板错误
    public static final String EXCEPTION_PARSE_TEMPLATE = "203";
    public static final String EXCEPTION_PARSE_TEMPLATE_MESSAGE = "parse template error.";

    // 发送消息失败
    public static final String EXCEPTION_SEND_MESSAGE = "204";
    public static final String EXCEPTION_SEND_MESSAGE_MESSAGE = "send message error.";

    // 接收人不能为空
    public static final String EXCEPTION_RECIPIENT_IS_EMPTY = "205";
    public static final String EXCEPTION_RECIPIENT_IS_EMPTY_MESSAGE = "receive address is empty.";
}
