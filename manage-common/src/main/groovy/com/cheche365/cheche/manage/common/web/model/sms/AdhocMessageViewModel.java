package com.cheche365.cheche.manage.common.web.model.sms;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 主动短信
 * Created by lyh on 2015/10/9.
 */
public class AdhocMessageViewModel {
    private Long id;
    private String zucpCode;//漫道
    private String yxtCode;//盈信通
    private String mobile;//单一用户手机号
    private Long filterUserId;
    private String filterUserName;//筛选用户
    @NotNull
    private Long smsTemplateId;
    private String smsTemplateName;//短信模板
    private String parameter;//短信内容参数，以,分割，{变量=参数值}
    private String smsContent;//短信内容
    private String smsContentView;//预览内容
    private int sendFlag;//发送短信类型，0-立即发送，1-定时发送
    private String sendTime;//发送短信时间
    private Long statusId;//发送状态
    private String status;//发送状态，关联message_status表
    private String comment;//备注
    private String createTime;//创建时间
    private String updateTime;//修改时间
    private String operator;//操作人，关联internal_user表
    private Integer sentCount;//已发送短信数量
    private Integer totalCount;//总发送短信数量
    private String sendRate;//送达率
    private List variable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZucpCode() {
        return zucpCode;
    }

    public void setZucpCode(String zucpCode) {
        this.zucpCode = zucpCode;
    }

    public String getYxtCode() {
        return yxtCode;
    }

    public void setYxtCode(String yxtCode) {
        this.yxtCode = yxtCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getFilterUserId() {
        return filterUserId;
    }

    public void setFilterUserId(Long filterUserId) {
        this.filterUserId = filterUserId;
    }

    public String getFilterUserName() {
        return filterUserName;
    }

    public void setFilterUserName(String filterUserName) {
        this.filterUserName = filterUserName;
    }

    public Long getSmsTemplateId() {
        return smsTemplateId;
    }

    public void setSmsTemplateId(Long smsTemplateId) {
        this.smsTemplateId = smsTemplateId;
    }

    public String getSmsTemplateName() {
        return smsTemplateName;
    }

    public void setSmsTemplateName(String smsTemplateName) {
        this.smsTemplateName = smsTemplateName;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public String getSmsContentView() {
        return smsContentView;
    }

    public void setSmsContentView(String smsContentView) {
        this.smsContentView = smsContentView;
    }

    public int getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(int sendFlag) {
        this.sendFlag = sendFlag;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Integer getSentCount() {
        return sentCount;
    }

    public void setSentCount(Integer sentCount) {
        this.sentCount = sentCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getSendRate() {
        return sendRate;
    }

    public void setSendRate(String sendRate) {
        this.sendRate = sendRate;
    }

    public List getVariable() {
        return variable;
    }

    public void setVariable(List variable) {
        this.variable = variable;
    }
}
