package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.manage.common.model.SchedulingJob;
import com.cheche365.cheche.manage.common.model.TaskJob;
import com.cheche365.cheche.manage.common.model.TaskJobDetail;
import com.cheche365.cheche.manage.common.repository.TaskJobDetailRepository;
import com.cheche365.cheche.manage.common.repository.TaskJobRepository;
import com.cheche365.cheche.manage.common.service.TaskJobService;
import com.cheche365.cheche.scheduletask.service.common.TaskRunningService;
import org.apache.commons.beanutils.BeanUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by xu.yelong on 2016-04-18.
 */
@DisallowConcurrentExecution
public class TaskProxy implements Job {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private BaseTask baseTask;
    private JobDataMap dataMap;

    private TaskJobDetailRepository taskJobDetailRepository = (TaskJobDetailRepository) ApplicationContextHolder.getApplicationContext().getBean("taskJobDetailRepository");
    private TaskJobRepository taskJobRepository = (TaskJobRepository) ApplicationContextHolder.getApplicationContext().getBean("taskJobRepository");
    private TaskRunningService taskRunningService = (TaskRunningService) ApplicationContextHolder.getApplicationContext().getBean("taskRunningService");

    public TaskProxy() {
    }

    TaskProxy(BaseTask baseTask) {
        this.baseTask = baseTask;
        Object obj = TaskRunningService.taskDataMap.get(baseTask.getClass().getName());
        obj = obj != null ? obj : taskRunningService.getRedisRunningParam(baseTask.getClass().getName());
        dataMap = obj != null ? (JobDataMap) obj : null;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        dataMap = context.getMergedJobDataMap();
        SchedulingJob schedulingJob = (SchedulingJob) dataMap.get(SchedulingJob.CLASS_NAME);
        if (!checkJobStatus(schedulingJob)) {
            logger.debug("任务名称 = {}--未启动成功，与数据库状态不一致", schedulingJob.getJobName());
            return;
        }
        logger.info("运行任务名称 = {}", schedulingJob.getJobName());
        Object object = ApplicationContextHolder.getApplicationContext().getBean(schedulingJob.getSpringId());
        if (object == null) {
            logger.debug("任务名称 = {}--未启动成功，请检查执行类是否配置正确！！！", schedulingJob.getJobName());
            return;
        }
        this.baseTask = (BaseTask) object;
        Long startTime = System.currentTimeMillis();
        boolean result = this.process();
        setTaskResult(result, context, schedulingJob, startTime);
    }

    private boolean checkJobStatus(SchedulingJob scheduleJob) {
        TaskJobService taskJobService = (TaskJobService) ApplicationContextHolder.getApplicationContext().getBean("taskJobService");
        TaskJob taskJob = taskJobService.findById(Long.parseLong(scheduleJob.getJobId()));
        return taskJob != null && taskJob.getStatus();
    }

    private void setTaskResult(boolean result, JobExecutionContext context, SchedulingJob scheduleJob, Long startTimeStamp) {
        //定时任务执行情况保存：开始时间、结束时间、执行时长、执行状态、处理记录条数
        saveJobExecuteDetail(result, context, scheduleJob, startTimeStamp);
    }

    /**
     * 将定时任务的执行详情记录到task_job_detail表中
     */
    private void saveJobExecuteDetail(boolean result, JobExecutionContext context, SchedulingJob scheduleJob, Long startTimeStamp) {
        TaskJob job = taskJobRepository.findOne(Long.parseLong(scheduleJob.getJobId()));
        Long endTimeStamp = System.currentTimeMillis();
        TaskJobDetail detail = new TaskJobDetail();
        detail.setTaskJob(job);
        detail.setStartTime(DateUtils.timeStampToDate(startTimeStamp, DateUtils.DATE_LONGTIME24_PATTERN));
        detail.setEndTime(DateUtils.timeStampToDate(endTimeStamp, DateUtils.DATE_LONGTIME24_PATTERN));
        detail.setExecuteDuration(endTimeStamp - startTimeStamp);
        detail.setStatus(result);
        //TODO: 2017-07-06 定时任务处理的记录条数待定
        /*detail.setRecordAmount();*/
        taskJobDetailRepository.save(detail);
    }

    public boolean process() {
        return doProcess(dataMap);
    }

    /**
     * 注入执行类的参数并执行任务
     *
     * @param dataMap 自定义参数map
     */
    private boolean doProcess(JobDataMap dataMap) {
        if (dataMap == null) {
            return false;
        }
        SchedulingJob schedulingJob = (SchedulingJob) dataMap.get(SchedulingJob.CLASS_NAME);
        TaskRunningService.taskDataMap.put(schedulingJob.getJobId(), dataMap);
        taskRunningService.setRedisRunningParam(schedulingJob.getJobId(), dataMap);
        try {
            BeanUtils.setProperty(baseTask, "jobDataMap", dataMap);
            return baseTask.process();
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.debug("定时任务{}注入参数异常", schedulingJob.getJobName(), e);
        } finally {
            try {
                taskRunningService.setRedisRunningParam(schedulingJob.getJobId(), new HashMap());
                BeanUtils.setProperty(baseTask, "jobDataMap", new HashMap());
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.debug("定时任务{}注入参数异常,revert jobDataMap", schedulingJob.getJobName(), e);
            }
        }
        return false;
    }
}
