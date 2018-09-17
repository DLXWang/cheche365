package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.core.model.InternalUser;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wangshaobin on 2016/8/24.
 */
@Entity
public class TelMarketingCenterChannelFilter {
    private long id;
    private String excludeChannels;
    private int taskType;
    private Date createTime;
    private Date updateTime;
    private InternalUser operator;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    @Column
    public String getExcludeChannels() {
        return excludeChannels;
    }

    @Column
    public int getTaskType() {
        return taskType;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setExcludeChannels(String excludeChannels) {
        this.excludeChannels = excludeChannels;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
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
    @JoinColumn(foreignKey=@ForeignKey(name="FK_TASK_EXCULDE_CHANNEL_SETTING_OPERATOR_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }
}
