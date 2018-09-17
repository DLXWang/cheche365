package com.cheche365.cheche.manage.common.web.model.sms;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.SmsTemplate;

import javax.validation.constraints.NotNull;

/**
 * Created by guoweifu on 2015/10/8.
 */
public class SmsTemplateViewModel {

    private long id;
    @NotNull
    private String name;
    @NotNull
    private String zucpCode;
    private String yxtCode;
    private Integer disable;
    @NotNull
    private String content;
    private String comment;
    private String createTime;
    private String updateTime;
    private String operator;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getDisable() {
        return disable;
    }

    public void setDisable(Integer disable) {
        this.disable = disable;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public static SmsTemplateViewModel createViewData(SmsTemplate smsTemplate) {
        SmsTemplateViewModel viewDate = new SmsTemplateViewModel();
        viewDate.setId(smsTemplate.getId());
        viewDate.setName(smsTemplate.getName());
        viewDate.setComment(smsTemplate.getComment());
        viewDate.setContent(smsTemplate.getContent());
        viewDate.setZucpCode(smsTemplate.getZucpCode());
        viewDate.setYxtCode(smsTemplate.getYxtCode());
        viewDate.setDisable(smsTemplate.isDisable() ? 1 : 0);
        viewDate.setCreateTime(DateUtils.getDateString(smsTemplate.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewDate.setUpdateTime(DateUtils.getDateString(smsTemplate.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewDate.setOperator(smsTemplate.getOperator() == null ? "" : smsTemplate.getOperator().getName());

        return viewDate;
    }
}
