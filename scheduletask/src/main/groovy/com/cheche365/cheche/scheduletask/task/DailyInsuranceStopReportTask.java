package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.DailyInsuranceStopReport;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.service.task.DailyInsuranceStopReportService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * #安心用户停复驶记录信息同步
 * Created by mujiguang on 2017/7/27.
 */
@Service
public class DailyInsuranceStopReportTask extends BaseTask{
    Logger logger = Logger.getLogger(DailyInsuranceStopReportTask.class);

    private String emailconfigPath = "/emailconfig/daily_insurance_stop_report.yml";
    @Autowired
    private DailyInsuranceStopReportService dailyInsuranceStopReportService;
    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        Map<String, Object> paramMap = new HashMap<>();
        String timePeriod = DateUtils.getDateString(DateUtils.calculateDateByDay(new Date(),-1), "yyyy年MM月dd日");
        paramMap.put("timePeriod",timePeriod);
        //邮件附件内容
        Map<String,List<DailyInsuranceStopReport>> sheetDataMaps = dailyInsuranceStopReportService.getEmailContent();
        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath,paramMap);
        addAttachment(emailInfo, emailconfigPath,paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
