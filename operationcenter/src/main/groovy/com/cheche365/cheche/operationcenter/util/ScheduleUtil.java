package com.cheche365.cheche.operationcenter.util;

import com.cheche365.cheche.core.model.AdhocMessage;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.util.SMSMessageUtil;
import com.cheche365.cheche.operationcenter.service.sms.tesk.TimerSendMessageJob;
import org.quartz.JobDataMap;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Calendar;
import java.util.Date;

/**
 * 创建调度器并启动
 * Created by sunhuazhong on 2015/10/15.
 */
public class ScheduleUtil {
    public static void createSchedule(String adhocMessageInString) throws Exception {
        AdhocMessage adhocMessage = CacheUtil.doJacksonDeserialize(adhocMessageInString, AdhocMessage.class);
        String name = adhocMessage.getId() + "_" + adhocMessage.getSmsTemplate().getId();
        //创建一个作业明细对象 三个重要的属性 作业名称，作业所属组别
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setName(name + "_job_detail");
        jobDetailFactoryBean.setJobClass(TimerSendMessageJob.class);
        //创建一个作业数据Map对象 它继承于DirtyFlagMap ,该类又继承与map,所以该对象可以通过map的形式存储作业的相关属性信息
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(SMSMessageUtil.getJobDetailTimerTask(), adhocMessageInString);
        jobDetailFactoryBean.setJobDataMap(jobDataMap);
        jobDetailFactoryBean.afterPropertiesSet();

        //创建一个触发器对象，该对象的作用是用来触发作业的执行
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setName(name + "_trigger");
        cronTriggerFactoryBean.setJobDetail(jobDetailFactoryBean.getObject());
        // 秒 分 小时 日期 月份 星期 年
        cronTriggerFactoryBean.setCronExpression(getExpression(adhocMessage.getSendTime()));
        cronTriggerFactoryBean.afterPropertiesSet();

        //通过将作业明细作业数据结合注册到任务调度器上以实现对一个作业类的注册
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setTriggers(cronTriggerFactoryBean.getObject());
        schedulerFactoryBean.afterPropertiesSet();
        schedulerFactoryBean.start();
    }

    private static String getExpression(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 秒 分 小时 日期 月份 星期 年  0 0 9 * * ?
        StringBuilder sb = new StringBuilder();
        sb.append(calendar.get(Calendar.SECOND));//秒
        sb.append(" ");
        sb.append(calendar.get(Calendar.MINUTE));//分
        sb.append(" ");
        sb.append(calendar.get(Calendar.HOUR_OF_DAY));//小时
        sb.append(" ");
        sb.append(calendar.get(Calendar.DAY_OF_MONTH));//日期
        sb.append(" ");
        sb.append(calendar.get(Calendar.MONTH) + 1);//月份
        sb.append(" ? ");//星期
        sb.append(calendar.get(Calendar.YEAR));//年
        return sb.toString();
    }
}
