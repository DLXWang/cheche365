package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterCallRecordReportService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 电销拨打数据——数据源报表
 * Created by wangshaobin on 2016/7/14.
 */
@Service
public class TelMarketingCenterCallRecordReportTask extends BaseTask{
    Logger logger = Logger.getLogger(TelMarketingCenterCallRecordReportTask.class);

    private String emailconfigPath = "/emailconfig/tel_marketing_call_record_report.yml";
    @Autowired
    private TelMarketingCenterCallRecordReportService telMarketingCenterCallRecordReportService;
    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        //邮件参数
        Map<String, Object> paramMap = new HashMap<>();
        int hour= Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String yesterdayStr = DateUtils.getDateString(DateUtils.getCustomDate(new Date(), -1, 0, 0, 0), "yyyy年MM月dd日");
        String todayStr = DateUtils.getDateString(new Date(), "yyyy年MM月dd日");
        String timePeriod = yesterdayStr +"18时—"+todayStr+ "18时(24小时)";
        paramMap.put("timePeriod",timePeriod);
        //邮件附件内容
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps = telMarketingCenterCallRecordReportService.getCallRecordInfos();
        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath,paramMap);
        addAttachment(emailInfo, emailconfigPath,paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
