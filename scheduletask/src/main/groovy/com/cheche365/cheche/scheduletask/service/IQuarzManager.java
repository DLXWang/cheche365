package com.cheche365.cheche.scheduletask.service;

import com.cheche365.cheche.manage.common.model.SchedulingJob;
import com.cheche365.cheche.manage.common.model.TaskJob;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.util.List;

/**
 * Created by xu.yelong on 2016-04-12.
 */
public interface IQuarzManager {

    /**
     * 开启批量任务
     * @param taskJobList
     */
    public void enableCronSchedule(List<TaskJob> taskJobList);


    /**
     * 开启单个任务
     * @param taskJob
     */
    public boolean enableCronSchedule(TaskJob taskJob);

    /**
     * 关闭任务
     * @param schedulingJob
     * @return
     */
    public boolean disableSchedule(SchedulingJob schedulingJob);

    /**
     * 暂停任务
     * @param schedulingJob
     * @return
     */
    public boolean pauseJob(SchedulingJob schedulingJob);

    /**
     * 恢复任务
     * @param schedulingJob
     * @return
     */
    public boolean resumeJob(SchedulingJob schedulingJob);

    /**
     * 立即执行一个任务
     * @param schedulingJob
     */
    public void testJob(SchedulingJob schedulingJob);

    /**
     * 修改任务时间表达式
     * @param schedulingJob
     */
    public void updateCronExpression(SchedulingJob schedulingJob);

    /**
     * 获取任务详情
     * @param schedulingJob
     * @return
     */
    public JobDetail getJobDetail(SchedulingJob schedulingJob);

    /**
     * 获取任务触发器
     * @param schedulingJob
     * @return
     */
    public Trigger getJobTrigger(SchedulingJob schedulingJob);

    /**
     * 获取所有定时任务
     * @return
     */
    public List<SchedulingJob> getAllJobs();

    /**
     * 获取正在运行的定时任务
     * @return
     */
    public List<SchedulingJob> getRunningJob();

}
