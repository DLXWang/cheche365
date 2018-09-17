package com.cheche365.cheche.scheduletask.service;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.manage.common.model.SchedulingJob;
import com.cheche365.cheche.manage.common.model.TaskJob;
import com.cheche365.cheche.manage.common.model.TaskJobOperate;
import com.cheche365.cheche.manage.common.service.TaskJobService;
import com.cheche365.cheche.scheduletask.service.common.TaskRunningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by xu.yelong on 2016-04-12.
 */
@Component
public class TaskManager {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ApplicationContextHolder applicationContextHolder;

    @Autowired
    private TaskJobService taskJobService;

    @Autowired
    private TaskRunningService taskRunningService;

    @Autowired
    @Qualifier("defaultQuarzManager")
    private IQuarzManager quarzManager;

    @Autowired
    private TaskImmediateActuator taskImmediateActuator;

    @PostConstruct
    private void loadAllJob() {
        taskReset();
        new Thread(new CheckTaskStatusThread()).start();
    }


    private void taskReset() {
        List<TaskJob> taskJobList = taskJobService.findByEnable();
        logger.debug("加载有效定时任务数量为{}", taskJobList.size());
        quarzManager.enableCronSchedule(taskJobList);
        taskRunningService.setRedisRunningList(quarzManager.getAllJobs());
    }

    class CheckTaskStatusThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TaskJobOperate taskJobOperate = taskRunningService.getRedisRunningOperate();
                if (taskJobOperate == null) {
                    continue;
                }
                TaskJob taskJob = taskJobOperate.getTaskJob();
                Integer operate = taskJobOperate.getOperate();
                try {
                    if (taskJob != null) {
                        logger.debug("更新定时任务:----{},操作类型:----{}", taskJob.getJobName(), operate);
                        SchedulingJob schedulingJob = SchedulingJob.createSchedulingJob(taskJob);
                        if (operate == TaskJobOperate.Enum.ADD || operate == TaskJobOperate.Enum.UPD) {
                            quarzManager.enableCronSchedule(taskJob);
                        } else if (operate == TaskJobOperate.Enum.DEL && schedulingJob != null) {
                            quarzManager.disableSchedule(schedulingJob);
                        } else if (operate == TaskJobOperate.Enum.ONCE) {
                            taskImmediateActuator.run(taskJob);
                        }
                    } else {
                        logger.debug("读取/重置定时任务,操作类型:----{}", operate);
                        if (operate == TaskJobOperate.Enum.READ) {
                            taskRunningService.setRedisRunningList(quarzManager.getAllJobs());
                        } else if (operate == TaskJobOperate.Enum.RESET) {
                            taskReset();
                        }
                    }
                } catch (ClassNotFoundException e) {
                    taskImmediateActuator.pickInvalidClass(taskJob.getJobClass());
                    logger.debug("设置redis任务状态异常--{}", taskJob.getJobName(), e);
                }
                taskRunningService.setRedisRunningList(quarzManager.getAllJobs());
            }
        }
    }
}
