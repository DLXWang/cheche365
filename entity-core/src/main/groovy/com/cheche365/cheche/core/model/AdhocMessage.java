package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class AdhocMessage {
    private Long id;
    private String mobile;//单一用户手机号
    private FilterUser filterUser;//筛选用户，外键，关联filter_user表
    private SmsTemplate smsTemplate;//短信模板，外键，关联message_template表
    private String parameter;//短信内容参数，以,分割，{变量=参数值}
    private Integer sendFlag;//发送短信类型，0-立即发送，1-定时发送
    private Date sendTime;//发送短信时间
    private MessageStatus status;//发送状态，外键，关联message_status表
    private String comment;//备注
    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//操作人，关联internal_user表
    private Integer sentCount;//已发送短信数量
    private Integer totalCount;//总发送短信数量

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(11)")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @ManyToOne
    @JoinColumn(name = "filterUser", foreignKey=@ForeignKey(name="FK_ADHOC_MESSAGE_REF_FILTER_USER", foreignKeyDefinition="FOREIGN KEY (filter_user) REFERENCES filter_user(id)"))
    public FilterUser getFilterUser() {
        return filterUser;
    }

    public void setFilterUser(FilterUser filterUser) {
        this.filterUser = filterUser;
    }

    @ManyToOne
    @JoinColumn(name = "smsTemplate", foreignKey=@ForeignKey(name="FK_ADHOC_MESSAGE_REF_SMS_TEMPLATE", foreignKeyDefinition="FOREIGN KEY (message_template) REFERENCES sms_template(id)"))
    public SmsTemplate getSmsTemplate() {
        return smsTemplate;
    }

    public void setSmsTemplate(SmsTemplate smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    @Column(columnDefinition = "VARCHAR(1000)")
    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Column(columnDefinition = "TINYINT(1)")
    public Integer getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(Integer sendFlag) {
        this.sendFlag = sendFlag;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey=@ForeignKey(name="FK_ADHOC_MESSAGE_REF_MESSAGE_STATUS", foreignKeyDefinition="FOREIGN KEY (status) REFERENCES message_status(id)"))
    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_OPERATOR", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
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

    @Column(columnDefinition = "INT(10)")
    public Integer getSentCount() {
        return sentCount;
    }

    public void setSentCount(Integer sentCount) {
        this.sentCount = sentCount;
    }
    @Column(columnDefinition = "INT(10)")
    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
