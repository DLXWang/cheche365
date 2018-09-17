package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.XiaomiReportInfo;
import com.cheche365.cheche.scheduletask.service.task.XiaomiReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * [小米对接C端]保单数据回传定时任务
 * Created by chenxy on 2018/5/29.
 */
@Service("xiaomiReportTask")
public class XiaomiReportTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(XiaomiReportTask.class);

    @Autowired
    private XiaomiReportService xiaomiReportService;

    private String emailconfigPath = "/emailconfig/xiaomi_order_report.yml";

    Map jobDataMap;


    @Override
    public void doProcess() throws Exception {
        Date reportDate = new Date();
        if (jobDataMap != null) {
            Object reportDateStr = jobDataMap.get("reportDateStr");
            if (reportDateStr != null) {
                logger.debug("获取到自定义执行时间参数,reportDateStr :{}", reportDateStr);
                reportDate = DateUtils.getDate((String) reportDateStr, DateUtils.DATE_LONGTIME24_PATTERN);
            }
        }
        int today = reportDate.getDate();
        if (today == 1) {
            messageInfoList.add(getLastMonth(reportDate));
        } else if (today == 15) {
            messageInfoList.add(getFirstHalf(reportDate));
        } else if (today == 30) {
            messageInfoList.add(getSecondHalf(reportDate));
        }
    }

    private MessageInfo getMessageInfo(Date start, Date end) throws IOException {
        try {
            List<XiaomiReportInfo> dataList = xiaomiReportService.getXiaomiInfoList(start, end);

            EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, null);
            addSimpleAttachment(emailInfo, this.emailconfigPath, null, dataList);
            return MessageInfo.createMessageInfo(emailInfo);
        } catch (Exception e) {
            logger.error("创建excel异常", e);
        }
        return null;
    }

    private MessageInfo getLastMonth(Date reportDate) {
        Date lastMonth = DateUtils.getMonthFirstDay(DateUtils.getAroundMonthsDay(reportDate, -1));
        Date lastMonthEnd = DateUtils.getMonthFirstDay(reportDate);
        try {
            return getMessageInfo(lastMonth, lastMonthEnd);
        } catch (IOException e) {
            logger.error("上个月小米数据发生异常", e);
        }
        return null;
    }

    private MessageInfo getFirstHalf(Date reportDate) {
        Date dateStart = DateUtils.getMonthFirstDay(reportDate);
        Date dateEnd = get14thDayEnd(reportDate);
        try {
            return getMessageInfo(dateStart, dateEnd);
        } catch (IOException e) {
            logger.error("1-14日小米数据异常", e);
        }
        return null;
    }

    private MessageInfo getSecondHalf(Date reportDate) {
        Date dateStart = get14thDayEnd(reportDate);
        Date dateEnd = get29thDayEnd(reportDate);
        try {
            return getMessageInfo(dateStart, dateEnd);
        } catch (IOException e) {
            logger.error("15-29日小米数据异常", e);
        }
        return null;
    }

    public static Date get14thDayEnd(Date reportDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reportDate);
        calendar.set(Calendar.DATE, 14);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date get29thDayEnd(Date reportDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reportDate);
        calendar.set(Calendar.DATE, 29);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public void setJobDataMap(Map jobDataMap) {
        this.jobDataMap = jobDataMap;
    }
}
