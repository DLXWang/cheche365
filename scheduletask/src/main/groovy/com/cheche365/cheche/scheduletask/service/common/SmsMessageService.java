package com.cheche365.cheche.scheduletask.service.common;

import com.cheche365.cheche.scheduletask.constants.ExceptionConstants;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.exception.TaskException;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.SmsInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
@Service("smsMessageService")
@Transactional
public class SmsMessageService implements IMessageService {

    Logger logger = LoggerFactory.getLogger(SmsMessageService.class);

//    @Autowired
//    @Qualifier("smsServiceZucp")
//    private ISmsService smsService;

    @Override
    public void sendMessage(MessageInfo messageInfo) {
        SmsInfo smsInfo = messageInfo.getSmsInfo();
        // 检查接收人
        if (ArrayUtils.isEmpty(smsInfo.getToSms())){
            logger.error("接收人不能为空。");
            throw new TaskException(ExceptionConstants.EXCEPTION_RECIPIENT_IS_EMPTY, ExceptionConstants.EXCEPTION_RECIPIENT_IS_EMPTY_MESSAGE);
        }

        String content = smsInfo.getContent();
        // 针对各个手机号发送短信
        for (String phoneNo : smsInfo.getToSms()) {
            if(logger.isDebugEnabled()) {
                logger.debug("接收人手机号：" + phoneNo + "，短信内容：" + content);
            }
            List<String> sendData = new ArrayList<>();
            sendData.add(phoneNo);
            sendData.add(content);
            List<List> payload = new ArrayList<>();
            payload.add(sendData);
            List<List> resultList = null;//smsService.sendSmsContents(payload, false);

            if(resultList != null && resultList.size() > 0) {
                List result = resultList.get(0);
                int errorCode = (int)result.get(0);
                if (TaskConstants.SMS_SEND_STATUS_SUCCESS == errorCode) {
                    logger.info("发送短信成功.");
                } else {
                    logger.error("发送短信失败.");
                }
            }
        }
    }
}
