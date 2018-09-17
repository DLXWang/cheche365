package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.scheduletask.service.common.TaskRunningService;
import com.cheche365.cheche.scheduletask.util.ClassUtil;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by guoweifu on 2015/11/24.
 */
@Service
public class RestartTask {

    Logger logger = LoggerFactory.getLogger(RestartTask.class);

    @Autowired
    private TaskRunningService taskRunningService;

    /**
     * 重启被意外关闭的定时任务
     */
    public void restartTask() {
        new Thread(new restartTaskThread()).start();
    }

    class restartTaskThread implements Runnable{
        @Override
        public void run() {
            try {
                //获取BaseTask的所有子类
                List<Class> classes = ClassUtil.getAllClassBySuperClass(BaseTask.class);
                ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
                try {
                    while(applicationContext==null){
                        Thread.sleep(100);
                        applicationContext = ApplicationContextHolder.getApplicationContext();
                    }

                } catch (InterruptedException e) {
                    logger.error("系统加载失败", e);
                }
                for (Class c : classes) {
                    //判断是否意外关闭，如果是则重启任务
                    if(taskRunningService.isShutdownAtRunning(c.getName())){
                        //考虑重启定时任务时可能会需要相关参数，采用代理类将参数注入到执行对象内。
                        BaseTask baseTask = (BaseTask) applicationContext.getBean(c);
                        TaskProxy taskProxy=new TaskProxy(baseTask);
                        taskProxy.process();
                    }
                }
            } catch (Exception e) {
                logger.error("重启任务失败", e);
            }
        }
    }
}
