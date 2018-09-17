package com.cheche365.cheche.scheduletask.service.common;

import com.cheche365.cheche.scheduletask.model.MessageInfo;


/**
 * 系统信息发送服务
 * Created by guoweifu on 2015/11/11.
 */
public interface IMessageService {

    /**
     * 发送消息
     * @param messageInfo
     */
    public void sendMessage(MessageInfo messageInfo);
}
