package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.*;
import com.cheche365.cheche.scheduletask.service.task.CustomerAppointmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 客户预约列表定时任务
 * 每天9点到17点 0 0 9-17 * * ?
 * Created by sunhuazhong on 10/28/15.
 */
@Service
public class CustomerAppointmentTask extends BaseTask {

    private static final String START_TIME_CACHE_KEY = "schedules.task.customer.appointment";

    @Autowired
    private CustomerAppointmentService customerAppointmentService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String emailconfigPath = "/emailconfig/customer_appointment_email.yml";


    /**
     * 执行任务详细内容
     *
     * @return
     */
    @Override
    public void doProcess() throws Exception {

        //设置任务所需要的数据
        Date endTime = Calendar.getInstance().getTime();
        String cachedStartDate = stringRedisTemplate.opsForValue().get(START_TIME_CACHE_KEY);
        Date startTime;
        if (StringUtils.isBlank(cachedStartDate)) {
            startTime = DateUtils.getDate("2015-10-01 00:00:00", DateUtils.DATE_LONGTIME24_PATTERN);
        } else {
            startTime = DateUtils.getDate(cachedStartDate, DateUtils.DATE_LONGTIME24_PATTERN);
        }

        //添加消息
        messageInfoList.add(getMessageInfo(startTime, endTime));

        //设置redis时间
        stringRedisTemplate.opsForValue().set(START_TIME_CACHE_KEY, DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN));
    }

    /**
     * 装配任务消息
     * @return
     */
    private MessageInfo getMessageInfo(Date startTime, Date endTime) throws IOException {
        // 邮件内容参数
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("startTime", DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN));//统计开始时间
        contentMap.put("endTime", DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN));//统计结束时间

        // 邮件附件数据
        List<PurchaseOrderInfo> attachmentDataList = customerAppointmentService.getPurchaseOrderInfos(endTime, startTime);

        //装配邮件信息
        EmailInfo emailInfo = assembleEmailInfo(emailconfigPath,contentMap);
        addSimpleAttachment(emailInfo,emailconfigPath,null,attachmentDataList);

        return MessageInfo.createMessageInfo(emailInfo);
    }
}
