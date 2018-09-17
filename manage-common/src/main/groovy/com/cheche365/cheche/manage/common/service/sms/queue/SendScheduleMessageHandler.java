package com.cheche365.cheche.manage.common.service.sms.queue;

import com.cheche365.cheche.core.service.sms.SmsInfo;
import com.cheche365.cheche.manage.common.service.sms.SendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by sunhuazhong on 2015/10/13.
 */
@Component
public class SendScheduleMessageHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SendMessageService sendMessageService;

    public Integer execute(SmsInfo smsInfo) {
        String content = smsInfo.getContent();
        smsInfo.setContent(content);
        Integer result;
        String smsType = smsInfo.getSmsType();
        try {
            if (smsType.equals(SmsInfo.Enum.SMS_TYPE_VERIFY_CODE)) {
                result = sendValidateCode(smsInfo);
            } else {
                result = sendMessage(smsInfo);
            }
        } catch (Exception e) {
            logger.info("调用短信微服务发送短信异常,mobile:{}", smsInfo.getMobile(), e);
            throw new RuntimeException("调用短信微服务发送短信异常!", e);
        }
        logger.info("发送短信返回结果,mobile-->({}),短信类型-->({}), resultCode=({}),resultDetail-->({})", smsInfo.getMobile(), smsType, result, sendMessageService.getSmsResultDetail(result));
        return result;
    }

    public Integer sendMessage(SmsInfo smsInfo) {
        return sendMessageService.sendMessage(smsInfo);
    }

    public Integer sendValidateCode(SmsInfo smsInfo) {
        return sendMessageService.sendValidateCodeMessage(smsInfo);
    }

}
