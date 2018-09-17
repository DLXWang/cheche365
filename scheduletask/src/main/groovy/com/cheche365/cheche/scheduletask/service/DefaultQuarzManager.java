package com.cheche365.cheche.scheduletask.service;

import com.cheche365.cheche.manage.common.model.SchedulingJob;
import com.cheche365.cheche.manage.common.model.TaskJob;
import com.cheche365.cheche.scheduletask.task.TaskProxy;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by xu.yelong on 2016-04-12.
 */
@Component("defaultQuarzManager")
public class DefaultQuarzManager extends AbstractQuarzManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void enableCronSchedule(List<TaskJob> jobList) {
        for (TaskJob taskJob : jobList) {
            try {
                SchedulingJob job = SchedulingJob.createSchedulingJob(taskJob);
                JobDataMap paramsMap = getJobDataMap(taskJob);
                enableCronSchedule(job, paramsMap);
            } catch (ClassNotFoundException e) {
                logger.debug("开启定时任务{}失败，任务对象不存在", taskJob.getJobClass(), e);
            }
        }
    }

    @Override
    public boolean enableCronSchedule(TaskJob taskJob) {
        try {
            SchedulingJob job = SchedulingJob.createSchedulingJob(taskJob);
            JobDataMap paramsMap = getJobDataMap(taskJob);
            return enableCronSchedule(job, paramsMap);
        } catch (ClassNotFoundException e) {
            logger.debug("开启定时任务{}失败，任务对象不存在", taskJob.getJobClass(), e);
            return false;
        }
    }

    public boolean enableCronSchedule(SchedulingJob job, JobDataMap jobDataMap) {
        if (job == null) {
            return false;
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobId());
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger != null) {
                scheduler.deleteJob(JobKey.jobKey(job.getJobId()));
            }
            JobDetailImpl jobDetail = (JobDetailImpl) JobBuilder.newJob(TaskProxy.class).withIdentity(job.getJobId()).build();
            jobDataMap.put(SchedulingJob.CLASS_NAME, job);
            jobDetail.setJobDataMap(jobDataMap);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
            trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, trigger);
            return true;
        } catch (SchedulerException e) {
            logger.debug("开启定时任务{}失败", job.getJobExecuteClass().getName(), e);
            return false;
        }
    }

    @Override
    public JobDataMap getJobDataMap(TaskJob taskJob) {
        JobDataMap paramMap = new JobDataMap();
        if (StringUtils.isNotEmpty(taskJob.getParamKey1()) && StringUtils.isNotEmpty(taskJob.getParamValue1())) {
            paramMap.put(taskJob.getParamKey1(), taskJob.getParamValue1());
        }
        if (StringUtils.isNotEmpty(taskJob.getParamKey2()) && StringUtils.isNotEmpty(taskJob.getParamValue2())) {
            paramMap.put(taskJob.getParamKey2(), taskJob.getParamValue2());
        }
        if (StringUtils.isNotEmpty(taskJob.getParamKey3()) && StringUtils.isNotEmpty(taskJob.getParamValue3())) {
            paramMap.put(taskJob.getParamKey3(), taskJob.getParamValue3());
        }
        return paramMap;
    }

}
