package com.cheche365.cheche.scheduletask.service.common;

import com.cheche365.cheche.manage.common.model.SchedulingJob;
import com.cheche365.cheche.manage.common.model.TaskJobOperate;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by guoweifu on 2015/9/21.
 */
@Service
public class TaskRunningService {
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    private static final String SCHEDULES_TASK_RUNNING_FLAG = "schedules.task.running.flag";
    private static final String SCHEDULES_TASK_RUNNING_PARAM = "schedules.task.running.param";
    private static final String SCHEDULES_TASK_RUNNING_LIST="schedules.task.running.list";
    private static final String SCHEDULES_TASK_RUNNING_OPERATE="schedules.task.running.operate";
    private static final String OPERATION_CENTER_REFRESH_MARKETINGRULE="operationcenter:controller:refresh:marketingRule";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //线程安全map 用于标记任务是否正在执行
    public static Map<String,Boolean> taskRunningMap = new ConcurrentHashMap<>();

    public static Map<String,Map> taskDataMap=new ConcurrentHashMap<>();
    public  void setRedisRunningFlag(String taskClassName){
        stringRedisTemplate.opsForHash().put(SCHEDULES_TASK_RUNNING_FLAG, taskClassName, "1");
    }

    public void removeRedisRunningFlag(String taskClassName){
        stringRedisTemplate.opsForHash().put(SCHEDULES_TASK_RUNNING_FLAG, taskClassName, "0");
    }

    public boolean isRunningFlag(String taskClassName){
        return taskRunningMap.get(taskClassName)!=null && taskRunningMap.get(taskClassName);
    }

    public boolean isShutdownAtRunning(String taskClassName){
             Object loadingFlag = stringRedisTemplate.opsForHash().get(SCHEDULES_TASK_RUNNING_FLAG,taskClassName);
        return loadingFlag!=null && loadingFlag.equals("1");
    }

    public void setRedisRunningParam(String jobId,Map jobDataMap){
        stringRedisTemplate.opsForHash().put(SCHEDULES_TASK_RUNNING_PARAM,jobId, CacheUtil.doJacksonSerialize(jobDataMap));
    }

    public Map getRedisRunningParam(String jobId){
        Object obj=stringRedisTemplate.opsForHash().get(SCHEDULES_TASK_RUNNING_PARAM,jobId);
        return obj==null?null:CacheUtil.doJacksonDeserialize(String.valueOf(obj),JobDataMap.class);
    }

    public void setRedisRunningList(List<SchedulingJob> schedulingJobs){
        stringRedisTemplate.opsForValue().set(SCHEDULES_TASK_RUNNING_LIST, CacheUtil.doJacksonSerialize(schedulingJobs));
    }

    public TaskJobOperate getRedisRunningOperate(){
        Object obj=stringRedisTemplate.opsForList().rightPop(SCHEDULES_TASK_RUNNING_OPERATE,5,TimeUnit.SECONDS);
        return obj==null?null:CacheUtil.doJacksonDeserialize(String.valueOf(obj),TaskJobOperate.class);
    }

    public List getRedisMarketingRule() {
        Object object = stringRedisTemplate.opsForList().rightPop(OPERATION_CENTER_REFRESH_MARKETINGRULE, 5, TimeUnit.SECONDS);
        return object == null ? null : CacheUtil.doJacksonDeserialize(String.valueOf(object), ArrayList.class);
    }

    public String getRedisSyncMobile(){
       return stringRedisTemplate.opsForList().rightPop(TaskConstants.MOBILE_AREA_SYNC_QUEUE, 5, TimeUnit.SECONDS);
    }

}
