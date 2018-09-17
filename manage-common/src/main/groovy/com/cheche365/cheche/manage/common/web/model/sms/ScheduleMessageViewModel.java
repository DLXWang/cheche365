package com.cheche365.cheche.manage.common.web.model.sms;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.ScheduleMessage;

import javax.validation.constraints.NotNull;

public class ScheduleMessageViewModel {
    private Long id;
    @NotNull
    private Long smsTemplateId;
    private String smsTemplateName;
    @NotNull
    private Long conditionId;
    private String conditionName;
    private String zucpCode;
    private String yxtCode;
    private String content;
    private boolean disable;
    private String comment;
    private String createTime;
    private String updateTime;
    private String operator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
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

    /**
     * 组建短信对象，返回到前端显示
     *
     * @param scheduleMessage
     * @return
     */
    public static ScheduleMessageViewModel createViewData(ScheduleMessage scheduleMessage) {
        ScheduleMessageViewModel viewModel = new ScheduleMessageViewModel();
        viewModel.setId(scheduleMessage.getId());//序号
        viewModel.setSmsTemplateId(scheduleMessage.getSmsTemplate().getId());//模板编码
        viewModel.setSmsTemplateName(scheduleMessage.getSmsTemplate().getName());//模板名称
        viewModel.setZucpCode(scheduleMessage.getSmsTemplate().getZucpCode());//漫道模板号
        viewModel.setYxtCode(scheduleMessage.getSmsTemplate().getYxtCode());//盈信通模板号
        viewModel.setContent(scheduleMessage.getSmsTemplate().getContent());//短信内容
        viewModel.setConditionId(scheduleMessage.getScheduleCondition().getId());//触发条件id
        viewModel.setConditionName(scheduleMessage.getScheduleCondition().getDescription());//触发条件
        viewModel.setDisable(scheduleMessage.isDisable());//启禁用
        viewModel.setComment(scheduleMessage.getComment());//备注
        viewModel.setCreateTime(DateUtils.getDateString(scheduleMessage.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));//创建时间
        viewModel.setUpdateTime(DateUtils.getDateString(scheduleMessage.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));//更新时间
        viewModel.setOperator(scheduleMessage.getOperator() == null ? "" : scheduleMessage.getOperator().getName());//操作人
        return viewModel;
    }
}
