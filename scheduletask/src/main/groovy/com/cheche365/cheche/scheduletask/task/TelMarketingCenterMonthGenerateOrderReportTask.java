package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterGenerateOrderReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangshaobin on 2017/6/2.
 */
@Service
public class TelMarketingCenterMonthGenerateOrderReportTask extends BaseTask {
    private String emailconfigPath = "/emailconfig/tel_marketing_month_generate_order_report.yml";

    @Autowired
    private TelMarketingCenterGenerateOrderReportService telMarketingCenterGenerateOrderReportService;

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        //邮件参数
        Map<String, Object> paramMap = new HashMap<>();
        Date latestMonth = DateUtils.getAroundMonthsDay(DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN), -1);
        String startTime = DateUtils.getDateString(DateUtils.getMonthFirstDay(latestMonth), "yyyy年MM月dd日");
        String endTime = DateUtils.getDateString(DateUtils.getMonthLastDay(latestMonth), "yyyy年MM月dd日");
        String timePeriod = startTime +"0时—" + endTime + "24时";
        paramMap.put("timePeriod",timePeriod);
        //邮件附件内容
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps = telMarketingCenterGenerateOrderReportService.getGenerateOrderInfos(getTimeParams());//1表示每月一发的报表
        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath,paramMap);
        addAttachment(emailInfo, emailconfigPath,paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }


    private Map<String, Date> getTimeParams(){
        Map<String, Date> timeMap = new HashMap<>();
        Date latestMonth = DateUtils.getAroundMonthsDay(DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN), -1);
        Date startTime = DateUtils.getMonthFirstDay(latestMonth);
        Date endTime = DateUtils.getDayEndTime(DateUtils.getMonthLastDay(latestMonth));
        timeMap.put("startTime", startTime);
        timeMap.put("endTime", endTime);
        return timeMap;
    }
}
