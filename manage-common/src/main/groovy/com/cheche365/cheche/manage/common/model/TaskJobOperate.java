package com.cheche365.cheche.manage.common.model;

/**
 * Created by xu.yelong on 2016-04-21.
 */
public class TaskJobOperate {
    private TaskJob taskJob;
    private Integer operate;

    public TaskJobOperate(){}

    public TaskJobOperate(TaskJob taskJob,Integer operate){
        this.taskJob=taskJob;
        this.operate=operate;
    }
    public static class Enum {
        public static final Integer ADD=1;
        public static final Integer UPD=2;
        public static final Integer DEL=3;
        public static final Integer RESET=4;
        public static final Integer READ=5;
        public static final Integer ONCE = 6;
    }

    public TaskJob getTaskJob() {
        return taskJob;
    }

    public void setTaskJob(TaskJob taskJob) {
        this.taskJob = taskJob;
    }

    public Integer getOperate() {
        return operate;
    }

    public void setOperate(Integer operate) {
        this.operate = operate;
    }
}
