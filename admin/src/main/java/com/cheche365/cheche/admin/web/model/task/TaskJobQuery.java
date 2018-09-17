package com.cheche365.cheche.admin.web.model.task;

import com.cheche365.cheche.manage.common.model.PublicQuery;

/**
 * Created by wangshaobin on 2017/7/6.
 */
public class TaskJobQuery extends PublicQuery {
    private Long taskJobId;
    private String jobName;
    private String status;

    public Long getTaskJobId() {
        return taskJobId;
    }

    public void setTaskJobId(Long taskJobId) {
        this.taskJobId = taskJobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
