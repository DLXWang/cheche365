package com.cheche365.cheche.admin.web.model.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.manage.common.model.TaskJobDetail;

/**
 * Created by wangshaobin on 2017/7/6.
 */
public class TaskJobDetailViewModel {
    private Long id;
    private Long taskJob;
    private String startTime;
    private String endTime;
    private String recordAmount;
    private String executeDuration;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskJob() {
        return taskJob;
    }

    public void setTaskJob(Long taskJob) {
        this.taskJob = taskJob;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRecordAmount() {
        return recordAmount;
    }

    public void setRecordAmount(String recordAmount) {
        this.recordAmount = recordAmount;
    }

    public String getExecuteDuration() {
        return executeDuration;
    }

    public void setExecuteDuration(String executeDuration) {
        this.executeDuration = executeDuration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static TaskJobDetailViewModel createViewModel(TaskJobDetail detail){
        if(detail == null)
            return null;
        TaskJobDetailViewModel model = new TaskJobDetailViewModel();
        model.setId(detail.getId());
        model.setTaskJob(detail.getId());
        model.setStartTime(DateUtils.getDateString(detail.getStartTime(),DateUtils.DATE_LONGTIME24_PATTERN));
        model.setEndTime(DateUtils.getDateString(detail.getEndTime(),DateUtils.DATE_LONGTIME24_PATTERN));
        model.setRecordAmount(detail.getRecordAmount()!=null?detail.getRecordAmount().toString():"");
        String executeDuration = "1秒";//不足一秒的，展示一秒
        Long stampBetween = detail.getExecuteDuration();
        //如果对应的时长为null，可能数据异常，展示为空
        if(stampBetween == null )
            executeDuration = "";
        else if(stampBetween/1000 > 0L)//转换秒且大于1秒后，进行展示
            executeDuration = DateUtils.getDaysBetweenFromStampToString(stampBetween/1000);
        model.setExecuteDuration(executeDuration);
        model.setStatus(detail.getStatus() ? "执行成功":"执行失败");
        return model;
    }
}
