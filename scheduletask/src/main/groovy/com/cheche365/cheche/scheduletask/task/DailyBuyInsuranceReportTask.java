package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.DailyBuyInsuranceReportService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 按天买车险用户数据情况统计
 * Created by Luly on 2017/2/8.
 */
@Service
public class DailyBuyInsuranceReportTask extends BaseTask{
    Logger logger = Logger.getLogger(DailyBuyInsuranceReportTask.class);

    private String emailconfigPath = "/emailconfig/daily_buy_insurance_report.yml";
    @Autowired
    private DailyBuyInsuranceReportService dailyBuyInsuranceReportService;
    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        Map<String, Object> paramMap = new HashMap<>();
        String timePeriod = DateUtils.getDateString(DateUtils.getCustomDate(new Date(), -1, 0, 0, 0), "yyyy年MM月dd日");
        paramMap.put("timePeriod",timePeriod);
        //邮件附件内容
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps = dailyBuyInsuranceReportService.getDailyBuyData();
        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath,paramMap);
        addAttachment(emailInfo, emailconfigPath,paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
