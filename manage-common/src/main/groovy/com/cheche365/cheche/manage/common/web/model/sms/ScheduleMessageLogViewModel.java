package com.cheche365.cheche.manage.common.web.model.sms;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.ScheduleMessageLog;
import com.cheche365.cheche.core.model.SmsTemplate;
import com.cheche365.cheche.manage.common.constants.SMSMessageConstants;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleMessageLogViewModel {
    private Long id;
    private String zucpCode;
    private String yxtCode;
    private String content;//内容
    private String condition;
    private String mobile;//号码
    private String sendTime;//发送时间
    private String status;//发送状态

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 组建短信对象，返回到前端显示
     *
     * @param scheduleMessageLog
     * @return
     */
    public static ScheduleMessageLogViewModel createViewData(ScheduleMessageLog scheduleMessageLog) {
        ScheduleMessageLogViewModel viewModel = new ScheduleMessageLogViewModel();
        viewModel.setId(scheduleMessageLog.getId());//序号
        viewModel.setZucpCode(scheduleMessageLog.getScheduleMessage().getSmsTemplate().getZucpCode());
        viewModel.setYxtCode(scheduleMessageLog.getScheduleMessage().getSmsTemplate().getYxtCode());
        String content = scheduleMessageLog.getParameter();
        if (content.contains("|")) {
            viewModel.setContent(getMessageContent(scheduleMessageLog.getScheduleMessage().getSmsTemplate(), content));
        } else {
            viewModel.setContent(content);
        }
        viewModel.setCondition(scheduleMessageLog.getScheduleMessage().getScheduleCondition().getDescription());
        viewModel.setMobile(scheduleMessageLog.getMobile());
        viewModel.setSendTime(DateUtils.getDateString(scheduleMessageLog.getSendTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        if (scheduleMessageLog.getStatus() == 1) {
            viewModel.setStatus("成功");
        } else if (scheduleMessageLog.getStatus() == 2) {
            viewModel.setStatus("失败");
        } else {
            viewModel.setStatus("发送中");
        }
        return viewModel;
    }

    /**
     * 获取真正发送给用户的短信内容
     *
     * @param smsTemplate
     * @param parameter
     * @return
     */
    private static String getMessageContent(SmsTemplate smsTemplate, String parameter) {
        String smsTemplateContent = smsTemplate.getContent();
        Pattern pattern = Pattern.compile(SMSMessageConstants.MESSAGE_PATTERN);
        Matcher matcher = pattern.matcher(smsTemplateContent);
        if (StringUtils.isNotEmpty(parameter)) {
            String[] parameters = parameter.split("\\|");
            int i = 1;
            while (matcher.find()) {
                if (i < parameters.length) {
                    smsTemplateContent = smsTemplateContent.replace(matcher.group(0), parameters[i]);
                }
                i++;
            }
        }
        return smsTemplateContent;
    }
}
