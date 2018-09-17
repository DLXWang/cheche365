package com.cheche365.cheche.scheduletask.service;

import com.cheche365.cheche.manage.common.model.SchedulingJob;
import com.cheche365.cheche.manage.common.model.TaskJob;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by xu.yelong on 2016-04-12.
 */
public abstract class AbstractQuarzManager implements IQuarzManager {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected Scheduler scheduler;

    @Override
    public abstract void enableCronSchedule(List<TaskJob> jobList);

    @Override
    public abstract boolean enableCronSchedule(TaskJob taskJob);

    protected abstract JobDataMap getJobDataMap(TaskJob taskJob);

    @Override
    public boolean disableSchedule(SchedulingJob schedulingJob) {
        if (StringUtils.isEmpty(schedulingJob.getJobId())) {
            return false;
        }
        try {
            return scheduler.deleteJob(JobKey.jobKey(schedulingJob.getJobId()));
        } catch (SchedulerException e) {
            logger.debug("禁用定时任务{}失败", schedulingJob.getJobExecuteClass().getName(), e);
            return false;
        }
    }


    @Override
    public boolean pauseJob(SchedulingJob schedulingJob) {
        if (StringUtils.isEmpty(schedulingJob.getJobId())) {
            return false;
        }
        try {
            scheduler.pauseJob(JobKey.jobKey(schedulingJob.getJobId()));
            return true;
        } catch (SchedulerException e) {
            logger.debug("暂停定时任务{}失败", schedulingJob.getJobExecuteClass().getName(), e);
            return false;
        }
    }

    @Override
    public boolean resumeJob(SchedulingJob schedulingJob) {
        if (StringUtils.isEmpty(schedulingJob.getJobId())) {
            return false;
        }
        try {
            scheduler.resumeJob(JobKey.jobKey(schedulingJob.getJobId()));
            return true;
        } catch (SchedulerException e) {
            logger.debug("恢复定时任务{}失败", schedulingJob.getJobExecuteClass().getName(), e);
            return false;
        }
    }

    @Override
    public void testJob(SchedulingJob schedulingJob) {
        if (StringUtils.isEmpty(schedulingJob.getJobId())) {
            return;
        }
        try {
            scheduler.triggerJob(JobKey.jobKey(schedulingJob.getJobId()));
        } catch (SchedulerException e) {
            logger.debug("执行定时任务{}失败", schedulingJob.getJobExecuteClass().getName(), e);
        }
    }

    @Override
    public void updateCronExpression(SchedulingJob schedulingJob) {
        if (StringUtils.isEmpty(schedulingJob.getJobId())) {
            return;
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(schedulingJob.getJobId());
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(schedulingJob.getCronExpression());
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            logger.debug("修改定时任务{}时间表达式失败", schedulingJob.getJobExecuteClass().getName(), e);
        }
    }

    @Override
    public JobDetail getJobDetail(SchedulingJob schedulingJob) {
        if (StringUtils.isEmpty(schedulingJob.getJobId())) {
            return null;
        }
        try {
            return scheduler.getJobDetail(JobKey.jobKey(schedulingJob.getJobId()));
        } catch (SchedulerException e) {
            logger.debug("获取定时任务{}详情失败", schedulingJob.getJobExecuteClass().getName(), e);
        }
        return null;
    }

    @Override
    public Trigger getJobTrigger(SchedulingJob schedulingJob) {
        if (StringUtils.isEmpty(schedulingJob.getJobId())) {
            return null;
        }
        try {
            return scheduler.getTrigger(TriggerKey.triggerKey(schedulingJob.getJobId()));
        } catch (SchedulerException e) {
            logger.debug("获取定时任务{}触发器信息失败", schedulingJob.getJobExecuteClass().getName(), e);
        }
        return null;
    }

    /**
     * 获取所有任务列表
     * @return
     */
    @Override
    public List<SchedulingJob> getAllJobs() {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyGroup();
        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            List<SchedulingJob> jobList = new ArrayList<SchedulingJob>();
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    SchedulingJob job = new SchedulingJob();
                    job.setJobId(jobKey.getName());
                    job.setMemos("触发器:" + trigger.getKey());
                    job.setNextTime(trigger.getNextFireTime()); //下次触发时间
                    job.setPreviousTime(trigger.getPreviousFireTime());//上次触发时间
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    job.setJobStatus(triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        job.setCronExpression(cronExpression);
                    }
                    jobList.add(job);
                }
            }
            return jobList;
        } catch (SchedulerException e) {
            logger.debug("获取定时任务列表异常",e);
        }
        return null;
    }

    @Override
    public List<SchedulingJob> getRunningJob(){
        try {
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            List<SchedulingJob> jobList = new ArrayList<>(executingJobs.size());
            for (JobExecutionContext executingJob : executingJobs) {
                SchedulingJob job = new SchedulingJob();
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                job.setJobName(jobKey.getName());
                job.setMemos("触发器:" + trigger.getKey());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setJobStatus(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronExpression(cronExpression);
                }
                jobList.add(job);
            }
            return jobList;
        } catch (SchedulerException e) {
            logger.debug("获取运行中定时任务列表异常",e);
        }
        return null;
    }


}
