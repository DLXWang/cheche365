package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.DailyInsuranceBasicReportModel;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.WeicheQuoteEmailInfo;
import com.cheche365.cheche.scheduletask.service.task.DailyInsuranceBasicReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by chenxiangyin on 2017/4/26.
 */
@Service("dailyInsuranceBasicReportTask")
public class DailyInsuranceBasicReportTask extends BaseTask{
    Logger logger = LoggerFactory.getLogger(DailyInsuranceBasicReportTask.class);
    @Autowired
    private DailyInsuranceBasicReportService dailyInsuranceBasicReportService;
    private String emailconfigPath = "/emailconfig/daily_insurance_basic_count.yml";

    @Override
    public void doProcess() {
        try {
            messageInfoList.add(getMessageInfo());
        } catch (IOException e) {
            logger.error("DailyInsuranceBasicReportTask error ",e);
        }
    }
    private MessageInfo getMessageInfo() throws IOException {
        //获取数据list
        List<DailyInsuranceBasicReportModel> emailContent = dailyInsuranceBasicReportService.getEmailContent();

        EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, null);
        addSimpleAttachment(emailInfo, this.emailconfigPath, null, emailContent);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
