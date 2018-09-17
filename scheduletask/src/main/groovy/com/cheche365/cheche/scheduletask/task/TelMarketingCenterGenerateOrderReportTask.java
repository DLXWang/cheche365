package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterCallRecordReportService;
import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterGenerateOrderReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 电销拨打数据——出单报表
 * Created by wangshaobin on 2016/7/15.
 */
@Service
public class TelMarketingCenterGenerateOrderReportTask extends BaseTask{
    private String emailconfigPath = "/emailconfig/tel_marketing_generate_order_report.yml";

    @Autowired
    private TelMarketingCenterGenerateOrderReportService telMarketingCenterGenerateOrderReportService;

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        //邮件参数
        Map<String, Object> paramMap = new HashMap<>();
        String currDayStr = DateUtils.getDateString(new Date(), "yyyy年MM月dd日");
        String endTime = DateUtils.getCurrentDateString("yyyy年MM月dd日HH时");
        String timePeriod = currDayStr +"0时—" + endTime;
        paramMap.put("timePeriod",timePeriod);
        //邮件附件内容
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps = telMarketingCenterGenerateOrderReportService.getGenerateOrderInfos(getTimeParams());
        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath,paramMap);
        addAttachment(emailInfo, emailconfigPath,paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }



    private Map<String, Date> getTimeParams(){
        Map<String, Date> timeMap = new HashMap<>();
        //默认是按天发报表的时间
        //当前日期
        Date endTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        //昨天18点
        Date startTime = DateUtils.getDate(DateUtils.getCustomDate(new Date(),-1,18,10,0),DateUtils.DATE_LONGTIME24_PATTERN);
        timeMap.put("startTime", startTime);
        timeMap.put("endTime", endTime);
        return timeMap;
    }
}
