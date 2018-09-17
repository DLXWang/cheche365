package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterInputCallBySortReportService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 按渠道和地区统计进库量和已跟进
 * Created by Luly on 2016/12/22.
 */
@Service
public class TelMarketingCenterInputCallBySortReportTask extends BaseTask{
    Logger logger = Logger.getLogger(TelMarketingCenterInputCallBySortReportTask.class);

    private String emailconfigPath = "/emailconfig/tel_marketing_input_call_report.yml";
    @Autowired
    private TelMarketingCenterInputCallBySortReportService telMarketingCenterInputCallBySortReportService;
    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        //邮件参数
        Map<String, Object> paramMap = new HashMap<>();
        String yesterdayStr = DateUtils.getDateString(DateUtils.getCustomDate(new Date(), -1, 0, 0, 0), "yyyy年MM月dd日");
        String todayStr = DateUtils.getDateString(new Date(), "yyyy年MM月dd日");
        String timePeriod = yesterdayStr +"17时—"+todayStr+ "17时";
        paramMap.put("timePeriod",timePeriod);
        //邮件附件内容
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps = telMarketingCenterInputCallBySortReportService.getDataBySort();
        //装配邮件信息
        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath,paramMap);
        addAttachment(emailInfo, emailconfigPath,paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }
}
