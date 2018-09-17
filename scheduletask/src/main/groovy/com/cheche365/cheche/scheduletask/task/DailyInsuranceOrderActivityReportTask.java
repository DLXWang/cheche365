package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.DailyInsuranceOrderActivityReportService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安心用户下单和用户活动信息查询
 * Created by Luly on 2017/3/14.
 */
@Service
public class DailyInsuranceOrderActivityReportTask extends BaseTask{
    Logger logger = Logger.getLogger(DailyInsuranceOrderActivityReportTask.class);

    private String emailconfigPath = "/emailconfig/daily_insurance_order_activity_report.yml";
    @Autowired
    private DailyInsuranceOrderActivityReportService dailyInsuranceOrderAndActivityReportService;
    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        Map<String, Object> paramMap = new HashMap<>();
        String timePeriod = DateUtils.getDateString(new Date(), "yyyy年MM月dd日");
        paramMap.put("timePeriod",timePeriod);
        //邮件附件内容
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps = dailyInsuranceOrderAndActivityReportService.getDailyOrderActivityData();
        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath,paramMap);
        addAttachment(emailInfo, emailconfigPath,paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
