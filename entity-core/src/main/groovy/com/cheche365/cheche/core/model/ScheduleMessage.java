package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ScheduleMessage {
    private Long id;
    private SmsTemplate smsTemplate;//短信模板，外键，关联sms_template表
    private ScheduleCondition scheduleCondition;//条件，外键，关联schedule_condition表
    private boolean disable = true;//启禁用
    private String comment;//备注
    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//操作人，关联internal_user表

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @ManyToOne
    @JoinColumn(name = "smsTemplate", foreignKey=@ForeignKey(name="FK_SCHEDULE_MESSAGE_REF_SMS_TEMPLATE", foreignKeyDefinition="FOREIGN KEY (sms_template) REFERENCES sms_template(id)"))
    public SmsTemplate getSmsTemplate() {
        return smsTemplate;
    }

    public void setSmsTemplate(SmsTemplate smsTemplate) {
        this.smsTemplate = smsTemplate;
    }
    @ManyToOne
    @JoinColumn(name = "scheduleCondition", foreignKey=@ForeignKey(name="FK_SCHEDULE_MESSAGE_REF_CONDITION", foreignKeyDefinition="FOREIGN KEY (condition) REFERENCES schedule_condition(id)"))
    public ScheduleCondition getScheduleCondition() {
        return scheduleCondition;
    }

    public void setScheduleCondition(ScheduleCondition scheduleCondition) {
        this.scheduleCondition = scheduleCondition;
    }
    @Column(columnDefinition = "tinyint(1)")
    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_SCHEDULE_MESSAGE_REF_OPERATOR", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
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


}
