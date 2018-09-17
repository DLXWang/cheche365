package com.cheche365.cheche.manage.common.model;


import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by xu.yelong on 2016-04-12.
 */
public class SchedulingJob {
    private String jobId; // 任务的Id，一般为所定义Bean的ID
    private String jobName; // 任务的描述
    private String jobStatus; // 任务的状态
    private String cronExpression; // 定时任务运行时间表达式
    private String memos; // 任务描述
    private Class stateFulljobExecuteClass;//同步的执行类，需要从StatefulMethodInvokingJob继承
    private Class jobExecuteClass;//异步的执行类，需要从MethodInvokingJob继承
    private Date nextTime;//下次执行时间
    private Date previousTime;//上次执行时间
    private String springId;//spring容器托管Id
    public static final String CLASS_NAME="schedulingJob";

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getMemos() {
        return memos;
    }

    public void setMemos(String memos) {
        this.memos = memos;
    }

    public Class getStateFulljobExecuteClass() {
        return stateFulljobExecuteClass;
    }

    public void setStateFulljobExecuteClass(Class stateFulljobExecuteClass) {
        this.stateFulljobExecuteClass = stateFulljobExecuteClass;
    }

    public Class getJobExecuteClass() {
        return jobExecuteClass;
    }

    public void setJobExecuteClass(Class jobExecuteClass) {
        this.jobExecuteClass = jobExecuteClass;
    }

    public Date getNextTime() {
        return nextTime;
    }

    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

    public Date getPreviousTime() {
        return previousTime;
    }

    public void setPreviousTime(Date previousTime) {
        this.previousTime = previousTime;
    }

    public String getSpringId() {
        return springId;
    }

    public void setSpringId(String springId) {
        this.springId = springId;
    }

    public static SchedulingJob createSchedulingJob(TaskJob taskJob) throws ClassNotFoundException{
        SchedulingJob job=new SchedulingJob();
        job.setJobId(taskJob.getId().toString());
        job.setJobName(taskJob.getJobName());
        job.setCronExpression(taskJob.getJobCronExpression());
        job.setSpringId(createSpringId(taskJob));
        String className=taskJob.getJobClass().trim();
        Class clazz=Class.forName(className);
        job.setStateFulljobExecuteClass(clazz);
        job.setJobExecuteClass(clazz);
        return job;
    }

    public enum TASK_STATE{
        NONE("NONE","未知"),
        NORMAL("NORMAL", "正常运行"),
        PAUSED("PAUSED", "暂停状态"),
        COMPLETE("COMPLETE",""),
        ERROR("ERROR","错误状态"),
        BLOCKED("BLOCKED","锁定状态");

        TASK_STATE(String code,String name) {
            this.name = name;
            this.code = code;
        }

        public static String getName(String code) {
            for (TASK_STATE c : TASK_STATE.values()) {
                if (c.getCode().equals(code)) {
                    return c.name;
                }
            }
            return null;
        }
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
    }

    public static String createSpringId(TaskJob taskJob){
        String className=taskJob.getJobClass().substring(taskJob.getJobClass().lastIndexOf(".")+1,taskJob.getJobClass().length());
        Pattern p=Pattern.compile("[A-Z]");
        if(className==null ||className.equals("")){
            return "";
        }
        StringBuilder builder=new StringBuilder(className);
        java.util.regex.Matcher mc=p.matcher(className);
        if(mc.find()){
            builder.replace(mc.start()+0, mc.end(), mc.group().toLowerCase());
        }
        return builder.toString();
    }
}
