package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.core.model.InternalUser;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xu.yelong on 2016-04-12.
 */
@Entity
public class TaskJob {

    private Long id;
    private String jobClass;//定时任务执行类
    private String jobName;
    private String jobCronExpression;//时间表达式
    private String paramKey1;
    private String paramValue1;
    private String paramKey2;
    private String paramValue2;
    private String paramKey3;
    private String paramValue3;
    private String comment;
    private Boolean status = false;
    private Date createTime;
    private Date updateTime;
    private InternalUser operator;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    public String getJobCronExpression() {
        return jobCronExpression;
    }

    public void setJobCronExpression(String jobCronExpression) {
        this.jobCronExpression = jobCronExpression;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    public String getParamKey1() {
        return paramKey1;
    }

    public void setParamKey1(String paramKey1) {
        this.paramKey1 = paramKey1;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getParamValue1() {
        return paramValue1;
    }

    public void setParamValue1(String paramValue1) {
        this.paramValue1 = paramValue1;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    public String getParamKey2() {
        return paramKey2;
    }

    public void setParamKey2(String paramKey2) {
        this.paramKey2 = paramKey2;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getParamValue2() {
        return paramValue2;
    }

    public void setParamValue2(String paramValue2) {
        this.paramValue2 = paramValue2;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    public String getParamKey3() {
        return paramKey3;
    }

    public void setParamKey3(String paramKey3) {
        this.paramKey3 = paramKey3;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getParamValue3() {
        return paramValue3;
    }

    public void setParamValue3(String paramValue3) {
        this.paramValue3 = paramValue3;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne
    @JoinColumn(foreignKey=@ForeignKey(name="FK_TASK_JOB_OPERATOR_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

}
