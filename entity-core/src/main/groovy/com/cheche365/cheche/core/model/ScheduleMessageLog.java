package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ScheduleMessageLog {
    private Long id;
    private ScheduleMessage scheduleMessage;//短信模板，外键，关联sms_template表
    private String mobile;//条件，外键，关联schedule_condition表
    private Date sendTime;//发送时间
    private Integer status;//发送状态1-成功，2-失败
    private String parameter;//发送给用户的短信参数，以|分割，第一个为短信模板编号

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "scheduleMessage", foreignKey = @ForeignKey(name = "FK_SCHEDULE_MESSAGE_LOG_REF_SCHEDULE_MESSAGE", foreignKeyDefinition = "FOREIGN KEY (schedule_message) REFERENCES schedule_message(id)"))
    public ScheduleMessage getScheduleMessage() {
        return scheduleMessage;
    }

    public void setScheduleMessage(ScheduleMessage scheduleMessage) {
        this.scheduleMessage = scheduleMessage;
    }

    @Column(columnDefinition = "VARCHAR(11)")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(columnDefinition = "VARCHAR(1000)")
    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
