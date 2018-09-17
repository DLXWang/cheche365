package com.cheche365.cheche.ordercenter.service.message;

import com.cheche365.cheche.ordercenter.exception.OrderCenterException;

import java.util.Map;

/**
 * 消息发送器接口
 * Created by sunhuazhong on 2015/4/28.
 */
public interface ISender {
    /**
     * @param status   消息的发送状态
     * @param params  消息的参数
     * @param to      消息的接收人
     */
    public void sender(String status, Map<String, String> params, String... to);
}
