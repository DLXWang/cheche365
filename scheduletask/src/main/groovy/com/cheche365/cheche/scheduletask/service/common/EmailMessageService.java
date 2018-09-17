package com.cheche365.cheche.scheduletask.service.common;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.email.service.IEmailService;
import com.cheche365.cheche.scheduletask.constants.ExceptionConstants;
import com.cheche365.cheche.scheduletask.exception.MessageException;
import com.cheche365.cheche.scheduletask.exception.TaskException;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
@Service("emailMessageService")
@Transactional
public class EmailMessageService implements IMessageService {

    Logger logger = LoggerFactory.getLogger(EmailMessageService.class);

    @Autowired
    private IEmailService emailService;

    @Override
    public void sendMessage(MessageInfo messageInfo) {

        EmailInfo emailInfo = messageInfo.getEmailInfo();
//        String[] tos = {"guowf@cheche365.com"};
//        emailInfo.setTo(tos);

        // 检查接收人
        if (ArrayUtils.isEmpty(emailInfo.getTo())){
            logger.error("receiver can not be null.");
            throw new TaskException(ExceptionConstants.EXCEPTION_RECIPIENT_IS_EMPTY, ExceptionConstants.EXCEPTION_RECIPIENT_IS_EMPTY_MESSAGE);
        }

        try {
            // 发送邮件
            emailService.sender(emailInfo);
            // 发送邮件成功后删除附件
            if(emailInfo.getAttachments().size() > 0) {
                String filePath = emailInfo.getAttachments().values().iterator().next();
                new File(filePath).deleteOnExit();
            }
        } catch (Exception ex) {
            logger.error("send message error, include email and sms.",ex);
            throw new MessageException(ExceptionConstants.EXCEPTION_SEND_MESSAGE, ExceptionConstants.EXCEPTION_SEND_MESSAGE_MESSAGE);
        }
    }
}
