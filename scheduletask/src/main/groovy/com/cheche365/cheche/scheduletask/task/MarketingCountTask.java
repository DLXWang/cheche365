package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.service.task.MarketingCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * Created by gaochengchun on 2015/9/6.
 */
@Service
public class MarketingCountTask extends BaseTask {

    @Autowired
    private MarketingCountService marketingCountService;

    private String emailconfigPath = "/emailconfig/marketing_count_email.yml";


    /**
     * 执行任务详细内容
     *
     * @return
     */
    @Override
    public void doProcess() throws Exception {
        //添加消息
        messageInfoList.add(getMessageInfo());
    }

    /**
     * 装配任务消息
     * @return
     */
    private MessageInfo getMessageInfo() throws IOException {
        //获取内容参数
        Map<String, Object> paramMap = marketingCountService.getContentParam();

        //装配邮件信息
        EmailInfo emailInfo = assembleEmailInfo(emailconfigPath, paramMap);

        return MessageInfo.createMessageInfo(emailInfo);
    }

}
