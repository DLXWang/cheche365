package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.AutoFinancingInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.service.task.AutoFinancingMarketingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AutoFinancingMarketingTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(AutoFinancingMarketingTask.class);
    private static final String START_TIME_CACHE_KEY = "schedulestask.maketing.autoFinancing";

    @Autowired
    AutoFinancingMarketingService autoFinancingMarketingService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private String emailconfigPath = "/emailconfig/auto_financing_marketing_email.yml";


    @Override
    protected void doProcess() throws Exception {
        messageInfoList.add(getMessageInfo());
    }

    /**
     * 装配任务消息
     *
     * @return
     */
    private MessageInfo getMessageInfo() throws IOException {
        Calendar now = Calendar.getInstance();
        Date nowTime = now.getTime();
        String endTimeStr = DateUtils.getDateString(nowTime,DateUtils.DATE_LONGTIME24_PATTERN);
        String cachedStartDate = redisTemplate.opsForValue().get(START_TIME_CACHE_KEY);
        Date startTime;
        if (StringUtils.isBlank(cachedStartDate)){
            now.add(Calendar.MINUTE, -30);
            startTime = now.getTime();
        }else{
            startTime = DateUtils.getDate(cachedStartDate,DateUtils.DATE_LONGTIME24_PATTERN);
        }
        //获取内容参数
        Map<String, List<AutoFinancingInfo>> paramMap = autoFinancingMarketingService.getContentParam(startTime);
        if (paramMap != null && paramMap.size() != 0) {
            //装配邮件信息
            EmailInfo emailInfo=assembleEmailInfo(emailconfigPath, null);
            addAttachment(emailInfo, emailconfigPath, null, paramMap);
            redisTemplate.opsForValue().set(START_TIME_CACHE_KEY,endTimeStr);
            return MessageInfo.createMessageInfo(emailInfo);
        }else {
            logger.debug("Didn't find any mobile, will not send email!");
            redisTemplate.opsForValue().set(START_TIME_CACHE_KEY,endTimeStr);
            return null;
        }
    }

}
