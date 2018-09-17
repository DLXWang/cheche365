package com.cheche365.cheche.ordercenter.service.message;

import com.cheche365.cheche.ordercenter.exception.OrderCenterException;

import java.util.Map;

/**
 * 系统消息发送服务
 * Created by sunhuazhong on 2015/4/27.
 */
public interface IMessageService {
    /**
     * 根据参数转换发送内容，发送邮件或短信
     * @param sendType 消息发送类型
     * @param status   操作状态
     * @param params   填充模板内容的参数
     * @param to       消息的接收人
     * @throws com.cheche365.cheche.ordercenter.exception.OrderCenterException 模板不存在，或是发送消息出现异常
     */
    public void sendMessage(String sendType, String status, Map<String, String> params, String... to) throws OrderCenterException;
}
