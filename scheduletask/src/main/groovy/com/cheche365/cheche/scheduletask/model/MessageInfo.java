package com.cheche365.cheche.scheduletask.model;

import com.cheche365.cheche.email.model.EmailInfo;

/**
 * Created by guoweifu on 2015/11/12.
 */
public class MessageInfo {
    private SmsInfo smsInfo;//任务所要发送的短信列表
    private EmailInfo emailInfo;//邮件信息

    public MessageInfo() {
    }

    private MessageInfo(SmsInfo smsInfo) {
        this.smsInfo = smsInfo;
    }

    private MessageInfo(EmailInfo emailInfo) {
        this.emailInfo = emailInfo;
    }

    public SmsInfo getSmsInfo() {
        return smsInfo;
    }

    public void setSmsInfo(SmsInfo smsInfo) {
        this.smsInfo = smsInfo;
    }

    public EmailInfo getEmailInfo() {
        return emailInfo;
    }

    public void setEmailInfo(EmailInfo emailInfo) {
        this.emailInfo = emailInfo;
    }

    public static MessageInfo createMessageInfo(EmailInfo emailInfo) {
        return new MessageInfo(emailInfo);
    }

    public static MessageInfo createMessageInfo(SmsInfo smsInfo) {
        return new MessageInfo(smsInfo);
    }
}
