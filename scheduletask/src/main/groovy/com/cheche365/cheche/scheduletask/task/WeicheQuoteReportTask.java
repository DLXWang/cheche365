package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.WeicheQuoteEmailInfo;
import com.cheche365.cheche.scheduletask.service.task.WeicheQuoteReportService;
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
 * Created by yinjianbin on 2017/2/10.
 */
@Service("weicheQuoteReportTask")
public class WeicheQuoteReportTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(WeicheQuoteReportTask.class);

    @Autowired
    private WeicheQuoteReportService weicheQuoteReportService;

    private String emailconfigPath = "/emailconfig/weiche_quote_report.yml";


    @Override
    public void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        //获取数据list
        List<WeicheQuoteEmailInfo> dataList = weicheQuoteReportService.getEmailInfoList();

        EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, null);
        addSimpleAttachment(emailInfo, this.emailconfigPath, null, dataList);
        return MessageInfo.createMessageInfo(emailInfo);
    }

}
