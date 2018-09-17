package com.cheche365.cheche.admin.service.task;

import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.model.TaskJob;
import com.cheche365.cheche.manage.common.model.TaskJobOperate;
import com.cheche365.cheche.manage.common.repository.TaskJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 立即执行全部task
 * Created by zhangtc on 2017/11/9.
 */
@Service("taskOnceService")
public class TaskOnceService {

    @Autowired
    private TaskJobRepository taskJobRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String SCHEDULES_TASK_RUNNING_OPERATE = "schedules.task.running.operate";
    private static final String SCHEDULES_TASK_RUNNING_RESULT = "schedules.task.running.result";
    private static final Long TASK_LIMIT_RUNTIME = 43200000L;//12小时=43200000毫秒

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public long doOnce() {
        boolean result = true;
        Integer operate = TaskJobOperate.Enum.ONCE;
        List<TaskJob> taskJobs = (List<TaskJob>) taskJobRepository.findAll();
        for (TaskJob taskJob : taskJobs) {
            Long isPush = stringRedisTemplate.opsForList().leftPush(SCHEDULES_TASK_RUNNING_OPERATE, CacheUtil.doJacksonSerialize(new TaskJobOperate(taskJob, operate)));
            result = isPush > 0 && result;
        }
        if (result) {
            stringRedisTemplate.opsForHash().put(SCHEDULES_TASK_RUNNING_RESULT, "startTime", String.valueOf(System.currentTimeMillis()));
        }
        return result ? System.currentTimeMillis() : 0L;
    }

    public Map getOnce() {
        List<TaskJob> taskJobs = (List<TaskJob>) taskJobRepository.findAll();
        Map runningTasks = pickOutRunning(taskJobs);
        Boolean finish = runningTasks.size() == 0;
        Map result = stringRedisTemplate.opsForHash().entries(SCHEDULES_TASK_RUNNING_RESULT);
        if (finish) {
            stringRedisTemplate.delete(SCHEDULES_TASK_RUNNING_RESULT);
        }
        result.putAll(runningTasks);
        result.put("finished", finish);
        return result;
    }

    public boolean isOnceRunning() {
        if (stringRedisTemplate.opsForHash().get(SCHEDULES_TASK_RUNNING_RESULT, "startTime") != null) {
            Long startTime = Long.valueOf(stringRedisTemplate.opsForHash().get(SCHEDULES_TASK_RUNNING_RESULT, "startTime").toString());
            return System.currentTimeMillis() - startTime <= TASK_LIMIT_RUNTIME;
        }
        return false;
    }

    /**
     * 多节点部署时预防重复安排任务
     * before doOnce()
     */
    public void resetOnce() {
        stringRedisTemplate.delete(SCHEDULES_TASK_RUNNING_RESULT);
        stringRedisTemplate.opsForHash().put(SCHEDULES_TASK_RUNNING_RESULT, "startTime", String.valueOf(System.currentTimeMillis()));
    }

    private Map pickOutRunning(List<TaskJob> taskJobs) {
        Map runningTasks = new HashMap();
        taskJobs.stream().filter(taskJob -> !stringRedisTemplate.opsForHash().hasKey(SCHEDULES_TASK_RUNNING_RESULT, taskJob.getJobClass())).forEach(taskJob -> {
            runningTasks.put(taskJob.getJobClass(), "running");
        });
        return runningTasks;
    }
}
