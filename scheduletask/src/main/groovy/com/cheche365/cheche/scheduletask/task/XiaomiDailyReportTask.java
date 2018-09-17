package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.XiaomiDailyReportInfo;
import com.cheche365.cheche.scheduletask.service.task.XiaomiDailyReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [小米对接C端]保单数据回传定时任务 日报
 * Created by chenxy on 2018/6/25.
 */
@Service("xiaomiDailyReportTask")
public class XiaomiDailyReportTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(XiaomiDailyReportTask.class);

    @Autowired
    private XiaomiDailyReportService xiaomiDailyReportService;

    private String emailconfigPath = "/emailconfig/xiaomi_order_report_daily.yml";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        try{
            Map<String, Object> paramMap = new HashMap<>();
            String yesterdayStr = DateUtils.getDateString(DateUtils.getCustomDate(new Date(), -1, 0, 0, 0), "yyyy年MM月dd日");
            paramMap.put("timePeriod",yesterdayStr);
            List<XiaomiDailyReportInfo> dataList = xiaomiDailyReportService.getXiaomiInfoList();
            EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, paramMap);
            addSimpleAttachment(emailInfo, this.emailconfigPath, paramMap, dataList);
            return MessageInfo.createMessageInfo(emailInfo);
        }catch(Exception e){
            logger.error("创建excel异常",e);
        }
        return null;
    }

}
