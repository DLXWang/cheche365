package com.cheche365.cheche.scheduletask.task;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.manage.common.service.HttpBaiduSender;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.service.common.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cxy on 2018-03-1.
 */
@Service
public class OrderUploadBaiduFTPTask extends BaseTask {
    @Autowired
    private HttpBaiduSender sender;

    @Autowired
    @Qualifier("emailMessageService")
    private IMessageService emailMessageService;
    protected static final String EMAIL_CONFIG_PATH = "/emailconfig/baidu_insure_temporary.yml";
    @Override
    protected void doProcess() throws Exception {
        String content = sender.setFile();
        sendMessage(content,EMAIL_CONFIG_PATH);
    }


    public void sendMessage(String content, String emailConfigPath) throws IOException {
        /*
        为SP12-job自动化测试临时处理
         */
        if (System.getProperty("sendMail") != null) {
            return;
        }
        //邮件内容
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("content", content);//异常信息
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setEmailInfo(assembleEmailInfo(emailConfigPath, paramMap));
        //发送邮件
        emailMessageService.sendMessage(messageInfo);
    }


}
