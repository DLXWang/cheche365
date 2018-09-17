package com.cheche365.cheche.manage.common.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wangshaobin on 2017/7/6.
 */
@Entity
public class TaskJobDetail {
    private Long id;
    private TaskJob taskJob;        //定时任务
    private Date startTime;         //开始时间
    private Date endTime;           //结束时间
    private Boolean status;         //0：执行失败 1：执行成功
    private Integer recordAmount;   //处理的记录条数
    private Long executeDuration; //执行时长

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "taskJob", foreignKey=@ForeignKey(name="FK_TASK_JOB_DETAIL_REF_TASK_JOB", foreignKeyDefinition="FOREIGN KEY (taskJob) REFERENCES task_job(id)"))
    public TaskJob getTaskJob() {
        return taskJob;
    }

    public void setTaskJob(TaskJob taskJob) {
        this.taskJob = taskJob;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(columnDefinition = "INT(10)")
    public Integer getRecordAmount() {
        return recordAmount;
    }

    public void setRecordAmount(Integer recordAmount) {
        this.recordAmount = recordAmount;
    }

    @Column(columnDefinition = "TINYINT(1)")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public Long getExecuteDuration() {
        return executeDuration;
    }

    public void setExecuteDuration(Long executeDuration) {
        this.executeDuration = executeDuration;
    }
}
