package com.cheche365.cheche.ordercenter.service.message;

import com.cheche365.cheche.ordercenter.constants.OrderCenterConstants;
import com.cheche365.cheche.ordercenter.exception.OrderCenterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
@Service
@Transactional
public class MessageService implements IMessageService {

    Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private SenderFactory senderFactory;

    @Override
    public void sendMessage(String sendType, String status, Map<String, String> params, String... to) throws OrderCenterException {
        // 发送
        ISender sender = senderFactory.getSender(sendType);
        if (sender == null) {
            logger.error("没有对应的消息发送器");
            throw new OrderCenterException(
                OrderCenterConstants.EXCEPTION_NO_MESSAGE_SEND,
                OrderCenterConstants.EXCEPTION_NO_MESSAGE_SEND_MESSAGE);
        }

        if(logger.isDebugEnabled()) {
            logger.debug("消息发送开始。");
        }

        sender.sender(status, params, to);

        if(logger.isDebugEnabled()) {
            logger.debug("消息发送结束。");
        }
    }
}
