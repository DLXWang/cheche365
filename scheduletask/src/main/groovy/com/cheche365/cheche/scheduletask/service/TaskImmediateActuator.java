package com.cheche365.cheche.scheduletask.service;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.manage.common.model.SchedulingJob;
import com.cheche365.cheche.manage.common.model.TaskJob;
import com.cheche365.cheche.scheduletask.service.common.TaskRunningService;
import com.cheche365.cheche.scheduletask.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * task执行器
 * Created by zhangtc on 2017/11/1.
 */
@Service
class TaskImmediateActuator {

    private static final String SCHEDULES_TASK_RUNNING_RESULT = "schedules.task.running.result";
    private static final Long RESTART_COLD_DOWN = 60000L;

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(3);

    private static boolean unInstantiation = true;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TaskRunningService taskRunningService;

    public void run(TaskJob taskJob) {
        instantiation();
        threadPool.execute(new Actuator(taskJob));
    }

    /**
     * 预防服务重启导致执行中断，要等至上次执行超时的问题
     * 多节点部署可能导致整个测试重启
     */
    private void instantiation() {
        if (unInstantiation) {
            Object object = stringRedisTemplate.opsForHash().get(SCHEDULES_TASK_RUNNING_RESULT, "startTime");
            if (object != null) {
                Long startTime = Long.valueOf(object.toString());
                if (System.currentTimeMillis() - startTime > RESTART_COLD_DOWN) {
                    stringRedisTemplate.delete(SCHEDULES_TASK_RUNNING_RESULT);
                }
            }
            unInstantiation = false;
        }
    }

    class Actuator implements Runnable {

        private TaskJob taskJob;

        Actuator(TaskJob taskJob) {
            this.taskJob = taskJob;
        }

        @Override
        public void run() {
            BaseTask baseTask;
            boolean result = false;
            String errorStr = "failed";
            try {
                SchedulingJob job = SchedulingJob.createSchedulingJob(taskJob);
                Object object = ApplicationContextHolder.getApplicationContext().getBean(job.getSpringId());
                if (object != null) {
                    baseTask = (BaseTask) object;
                    waitRunning(taskJob.getJobName());
                    result = baseTask.process();
                }
            } catch (NoSuchBeanDefinitionException e) {
                result = pickInvalidClass(taskJob.getJobClass());
            } catch (Throwable e) {
                errorStr = e.getMessage();
                logger.debug("定时任务测试{}失败", taskJob.getJobClass(), e);
            } finally {
                stringRedisTemplate.opsForHash().put(SCHEDULES_TASK_RUNNING_RESULT, taskJob.getJobClass(), result ? "sucess" : errorStr);
                logger.debug("定时任务测试{}完成", taskJob.getJobClass());
            }
        }

        private void waitRunning(String jobName) {
            boolean running = true;
            while (running) {
                running = taskRunningService.isRunningFlag(jobName);
            }
        }
    }

    public boolean pickInvalidClass(String jobClass) {
        stringRedisTemplate.opsForHash().put(SCHEDULES_TASK_RUNNING_RESULT, jobClass, "sucess");
        logger.debug("开启定时任务测试{}失败，任务对象不存在", jobClass);
        return true;
    }
}
