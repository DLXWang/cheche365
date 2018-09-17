package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.HaodaiOrderReportModel;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.service.task.HaodaiOrderReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 微车渠道的报价日志记录报表
 * 定时时间：一天一次 每天3点发送
 * 接收人：
 * Created by chenxiangyin on 2017/6/2.
 */
@Service
public class HaodaiOrderReportTask extends BaseTask {
    Logger logger = LoggerFactory.getLogger(HaodaiOrderReportTask.class);
    @Autowired
    private HaodaiOrderReportService haodaiOrderReportService;
    private String emailconfigPath = "/emailconfig/haodai_order_report.yml";

    @Override
    public void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        //获取数据list
        List<HaodaiOrderReportModel> dataList = haodaiOrderReportService.getEmailContent();
        EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, null);
        addSimpleAttachment(emailInfo, this.emailconfigPath, null, dataList);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
