package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.BaiduDataSyncEmailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 百度数据同步邮件定时任务
 * Created by wangshaobin on 2017/1/5.
 */
@Service
public class BaiduDataSyncEmailTask extends BaseTask {
    @Autowired
    private BaiduDataSyncEmailService baiduDataSyncEmailService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String emailconfigPath = "/emailconfig/baidu_data_sync_email.yml";

    private static final String EMAIL_TITLE_PARAM = "timePeriod";
    private static final String TIME_FORMAT_STR = "yyyy年MM月dd日hh时";

    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    private MessageInfo getMessageInfo() throws IOException {
        Map<String, Object> paramMap = getEmailTitleTime();
        //邮件附件内容
        Map<String,List<PurchaseOrderInfo>> sheetDataMaps = baiduDataSyncEmailService.getSyncBaiduData();

        EmailInfo emailInfo=assembleEmailInfo(emailconfigPath, paramMap);
        addAttachment(emailInfo, emailconfigPath, paramMap, sheetDataMaps);
        return MessageInfo.createMessageInfo(emailInfo);
    }

    private Map<String, Object> getEmailTitleTime(){
        Date now = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        String endTime = DateUtils.getDateString(now, TIME_FORMAT_STR);
        String previousTimeStr = stringRedisTemplate.opsForValue().get(TaskConstants.BAIDU_DATA_SYNC_TIME);
        String startTime = "";
        //如果定时任务第一次执行，redis中没有上次执行时间，默认取昨天零点
        if(!StringUtils.isEmpty(previousTimeStr)){
            Date previousTime = DateUtils.getDate(previousTimeStr,DateUtils.DATE_LONGTIME24_PATTERN);
            startTime = DateUtils.getDateString(previousTime, TIME_FORMAT_STR);
        } else {
            Date yesterday = DateUtils.getAroundDaysTime(now,-1);
            startTime = DateUtils.getDateString(yesterday,TIME_FORMAT_STR);
        }
        //邮件参数
        Map<String, Object> paramMap = new HashMap<>();
        String timePeriod = startTime +"—"+endTime;
        paramMap.put(EMAIL_TITLE_PARAM,timePeriod);
        return paramMap;
    }
}
