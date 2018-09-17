package com.cheche365.cheche.ordercenter.service.message;

import com.cheche365.cheche.ordercenter.constants.OrderCenterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 根据不同的消息类型，取得适应的消息发送器
 * Created by sunhuazhong on 2015/4/28.
 */
@Component
public class SenderFactory {
    @Autowired
    @Qualifier("emailSender")
    private ISender emailSender;

    @Autowired
    @Qualifier("smsSender")
    private ISender smsSender;

    public ISender getSender(String type) {
        ISender sender = null;
        if (OrderCenterConstants.SEND_TYPE_EMAIL.equalsIgnoreCase(type)) {
            // 邮件
            sender = emailSender;
        } else if (OrderCenterConstants.SEND_TYPE_SMS.equalsIgnoreCase(type)) {
            // 短信
            sender = smsSender;
        }
        return sender;
    }
}
