package com.cheche365.cheche.admin.web.model.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.model.SchedulingJob;
import com.cheche365.cheche.manage.common.model.TaskJob;

import javax.validation.constraints.NotNull;


/**
 * Created by xu.yelong on 2016-04-13.
 */
public class TaskJobViewModel {

    private Long id;
    @NotNull
    private String jobClass;//定时任务执行类
    @NotNull
    private String jobName;
    @NotNull
    private String jobCronExpression;//时间表达式
    private String jobServiceBean;
    private String paramKey1;
    private String paramValue1;
    private String paramKey2;
    private String paramValue2;
    private String paramKey3;
    private String paramValue3;
    private String comment;
    private Boolean status;
    private String createTime;
    private String updateTime;
    private String operator;
    private String nextTime;
    private String previousTime;
    private String runningStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobCronExpression() {
        return jobCronExpression;
    }

    public void setJobCronExpression(String jobCronExpression) {
        this.jobCronExpression = jobCronExpression;
    }

    public String getJobServiceBean() {
        return jobServiceBean;
    }

    public void setJobServiceBean(String jobServiceBean) {
        this.jobServiceBean = jobServiceBean;
    }

    public String getParamKey1() {
        return paramKey1;
    }

    public void setParamKey1(String paramKey1) {
        this.paramKey1 = paramKey1;
    }

    public String getParamValue1() {
        return paramValue1;
    }

    public void setParamValue1(String paramValue1) {
        this.paramValue1 = paramValue1;
    }

    public String getParamKey2() {
        return paramKey2;
    }

    public void setParamKey2(String paramKey2) {
        this.paramKey2 = paramKey2;
    }

    public String getParamValue2() {
        return paramValue2;
    }

    public void setParamValue2(String paramValue2) {
        this.paramValue2 = paramValue2;
    }

    public String getParamKey3() {
        return paramKey3;
    }

    public void setParamKey3(String paramKey3) {
        this.paramKey3 = paramKey3;
    }

    public String getParamValue3() {
        return paramValue3;
    }

    public void setParamValue3(String paramValue3) {
        this.paramValue3 = paramValue3;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getNextTime() {
        return nextTime;
    }

    public void setNextTime(String nextTime) {
        this.nextTime = nextTime;
    }

    public String getPreviousTime() {
        return previousTime;
    }

    public void setPreviousTime(String previousTime) {
        this.previousTime = previousTime;
    }

    public String getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(String runningStatus) {
        this.runningStatus = runningStatus;
    }

    public static TaskJobViewModel createViewModel(TaskJob taskJob){
        return createViewModel(taskJob,null);
    }

    public static TaskJobViewModel createViewModel(TaskJob taskJob,SchedulingJob schedulingJob){
        if(taskJob==null){
            return null;
        }
        TaskJobViewModel viewModel=new TaskJobViewModel();
        String[] properties = new String[]{
            "id", "jobClass", "jobName", "jobCronExpression",
            "paramKey1", "paramValue1", "paramKey2", "paramValue2",
            "paramKey3", "paramValue3", "comment", "status"
        };
        BeanUtil.copyPropertiesContain(taskJob, viewModel, properties);
        viewModel.setCreateTime(DateUtils.getDateString(taskJob.getCreateTime(),DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(taskJob.getUpdateTime(),DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOperator(taskJob.getOperator().getName());
        if(schedulingJob!=null){
            viewModel.setRunningStatus(SchedulingJob.TASK_STATE.getName(schedulingJob.getJobStatus()));
            viewModel.setPreviousTime(DateUtils.getDateString(schedulingJob.getPreviousTime(),DateUtils.DATE_LONGTIME24_PATTERN));
            viewModel.setNextTime(DateUtils.getDateString(schedulingJob.getNextTime(),DateUtils.DATE_LONGTIME24_PATTERN));
        }
        return viewModel;
    }


}
