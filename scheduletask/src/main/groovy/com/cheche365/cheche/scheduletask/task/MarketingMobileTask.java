package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.MarketingMobileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 营销活动手机数据
 * Created by liqiang on 9/15/15.
 */
@Service
public class MarketingMobileTask extends BaseTask {

    private static final String START_TIME_CACHE_KEY = "schedulestask.maketing.mobile";

    Logger logger = LoggerFactory.getLogger(MarketingMobileTask.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MarketingMobileService marketingMobileService;

    private String emailconfigPath = "/emailconfig/marketing_mobile_email.yml";


    /**
     * 执行任务详细内容
     *
     * @return
     */
    @Override
    public void doProcess() throws Exception {
        //添加消息
        MessageInfo messageInfo = getMessageInfo();
        if(messageInfo != null){
            messageInfoList.add(messageInfo);
        }
    }


    /**
     * 装配任务消息
     * @return
     */
    private MessageInfo getMessageInfo() throws IOException {

        //设置任务所需要的数据。
        Calendar now = Calendar.getInstance();
        Date nowTime = now.getTime();
        String endTimeStr = DateUtils.getDateString(nowTime,DateUtils.DATE_LONGTIME24_PATTERN);
        String startTimeStr = "";
        String cachedStartDate = redisTemplate.opsForValue().get(START_TIME_CACHE_KEY);
        Date startTime;
        if (StringUtils.isBlank(cachedStartDate)){
            now.add(Calendar.MINUTE, -1);
            startTime = now.getTime();
            startTimeStr =  DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN);
        }else{
            startTimeStr = cachedStartDate;
            startTime = DateUtils.getDate(cachedStartDate,DateUtils.DATE_LONGTIME24_PATTERN);
        }
        // 邮件附件数据
        List<PurchaseOrderInfo> attachmentDataList = marketingMobileService.getPurchaseOrderInfos(startTime,nowTime);
        if (!attachmentDataList.isEmpty()) {
            //模板所需要的数据
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("startTime", startTimeStr);//统计开始时间
            paramMap.put("endTime", endTimeStr);//统计结束时间
            //装配邮件信息
            EmailInfo emailInfo = assembleEmailInfo(emailconfigPath, paramMap);
            addSimpleAttachment(emailInfo,emailconfigPath,null,attachmentDataList);
            redisTemplate.opsForValue().set(START_TIME_CACHE_KEY,endTimeStr);
            return MessageInfo.createMessageInfo(emailInfo);
        }else {
            logger.debug("Didn't find any mobile, will not send email!");
            redisTemplate.opsForValue().set(START_TIME_CACHE_KEY,endTimeStr);
            return null;
        }
    }
}
