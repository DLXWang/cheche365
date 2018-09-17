package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.DatebaoDataSyncReportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大特保数据同步邮件
 * Created by wangshaobin on 2017/4/26.
 */
@Service
public class DatebaoDataSyncReportTask extends BaseTask {
    @Autowired
    private DatebaoDataSyncReportService datebaoDataSyncReportService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String emailconfigPath = "/emailconfig/datebao_data_sync_report.yml";

    private static final String EMAIL_TITLE_PARAM = "timePeriod";
    private static final String TIME_FORMAT_STR = "yyyy年MM月dd日";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        Map<String, Object> paramMap = getEmailTitleTime();
        //邮件附件内容
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps = datebaoDataSyncReportService.getSyncDatebaoData();

        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath, paramMap);
        addAttachment(emailInfo, emailconfigPath, paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }

    private Map<String, Object> getEmailTitleTime(){
        String startTime = null;
        String endTime = DateUtils.getDateString(DateUtils.getCustomDate(new Date(),-1,23,59,59),TIME_FORMAT_STR);
        String previousTimeStr = stringRedisTemplate.opsForValue().get(TaskConstants.DATEBAO_DATA_SYNC_TIME);
        if(StringUtils.isEmpty(previousTimeStr))
            startTime = DateUtils.getDateString(DateUtils.getCustomDate(new Date(),-1,0,0,0),TIME_FORMAT_STR);
        else {//如果定时任务第一次执行，redis中没有上次执行时间，默认取昨天零点
            Date previousTime = DateUtils.getDate(previousTimeStr,DateUtils.DATE_LONGTIME24_PATTERN);
            startTime = DateUtils.getDateString(previousTime, TIME_FORMAT_STR);
        }
        //邮件参数
        Map<String, Object> paramMap = new HashMap<>();
        String timePeriod = startTime +"0点—"+endTime+"24点";
        paramMap.put(EMAIL_TITLE_PARAM,timePeriod);
        return paramMap;
    }
}
