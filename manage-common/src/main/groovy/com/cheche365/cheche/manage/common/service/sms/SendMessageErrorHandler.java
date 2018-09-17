package com.cheche365.cheche.manage.common.service.sms;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.AdhocMessage;
import com.cheche365.cheche.core.model.ScheduleMessage;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.email.exception.EmailException;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.email.service.IEmailService;
import com.cheche365.cheche.manage.common.constants.SMSMessageConstants;
import com.cheche365.cheche.manage.common.constants.SmsContants;
import com.cheche365.cheche.manage.common.util.VelocityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 短信发送失败后需要向负责人发送邮件，报告手机号，错误原因等
 * Created by sunhuazhong on 2015/10/13.
 */
@Component
public class SendMessageErrorHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IEmailService emailService;

    @Autowired
    private SendMessageService sendMessageService;

    public void conditionHandler(Integer result, ScheduleMessage scheduleMessage, String mobile, String messageContent) {
        if (RuntimeUtil.isDevEnv()) {
            return;
        }
        logger.debug("send message error email for schedule:{}, mobile:{}, content:{}, result:{}",
                scheduleMessage.getId(), mobile, messageContent, result);

        try {
            // 短信发送结果描述信息
            String resultMessage = result == null ? "发生异常" : sendMessageService.getSmsResultDetail(result);
            // 接收人
            String[] tos = SmsContants.ERROR_SMS_REMIND_EMAIL.split(",");
            // 主题
            String subject = getTitle();
            // 内容
            String emailContent = getConditionEmailContent(scheduleMessage, mobile, messageContent, resultMessage);
            EmailInfo emailInfo = new EmailInfo();
            emailInfo.setTo(tos);
            emailInfo.setSubject(subject);// 消息标题
            emailInfo.setContent(emailContent);// 消息内容
            emailService.sender(emailInfo);
        } catch (EmailException ex) {
            logger.error("send email error for error message.", ex);
        }
    }

    public void adhocHandler(int result, AdhocMessage adhocMessage) {
        if (RuntimeUtil.isDevEnv()) {
            return;
        }
        logger.debug("send message error for adhoc message:{}, result:{}",
                adhocMessage.getId(), result);

        try {
            // 短信发送结果描述信息
            String resultMessage = sendMessageService.getSmsResultDetail(result);
            // 接收人
            String[] tos = SmsContants.ERROR_SMS_REMIND_EMAIL.split(",");
            // 主题
            String subject = getTitle();
            // 内容
            String emailContent = getAdhocEmailContent(adhocMessage, resultMessage);
            EmailInfo emailInfo = new EmailInfo();
            emailInfo.setTo(tos);
            emailInfo.setSubject(subject);// 消息标题
            emailInfo.setContent(emailContent);// 消息内容
            emailService.sender(emailInfo);
        } catch (EmailException ex) {
            logger.error("send email error for error message.", ex);
        }
    }

    private String getTitle() {
        return "【提醒】" + DateUtils.getCurrentDateString(DateUtils.DATE_LONGTIME24_PATTERN) + "短信发送失败";
    }

    private String getAdhocEmailContent(AdhocMessage adhocMessage, String resultMessage) {
        try {
            Map<String, String> params = new HashMap<>();
            String templateFile = getTemplateFile(1);
            if (StringUtils.isNotEmpty(adhocMessage.getMobile())) {
                params.put("type", "1");
                params.put("mobile", adhocMessage.getMobile());
            } else {
                params.put("type", "2");
                params.put("filterUserName", adhocMessage.getFilterUser().getName());
            }
            params.put("content", getAdhocMessageContent(adhocMessage));
            params.put("result", resultMessage);
            return VelocityUtil.getInstance().parseVelocityTemplate(templateFile, params);
        } catch (Exception ex) {
            logger.error("parse template error for send sms error message.", ex);
        }
        return null;
    }

    private String getAdhocMessageContent(AdhocMessage adhocMessage) {
        String smsContentView = adhocMessage.getSmsTemplate().getContent();
        Pattern pattern = Pattern.compile(SMSMessageConstants.MESSAGE_PATTERN);
        Matcher matcher = pattern.matcher(smsContentView);
        if (StringUtils.isNotEmpty(adhocMessage.getParameter())) {
            String[] parameters = adhocMessage.getParameter().split(",");
            int i = 0;
            while (matcher.find()) {
                if (i < parameters.length) {
                    smsContentView = smsContentView.replace(matcher.group(0), parameters[i]);
                }
                i++;
            }
        }
        return smsContentView;
    }

    private String getConditionEmailContent(ScheduleMessage scheduleMessage,
                                            String mobile, String messageContent, String resultMessage) {
        try {
            Map<String, String> params = new HashMap<>();
            String templateFile = getTemplateFile(2);
            params.put("condition", scheduleMessage.getScheduleCondition().getDescription());
            params.put("mobile", mobile);
            params.put("content", messageContent);
            params.put("result", resultMessage);
            return VelocityUtil.getInstance().parseVelocityTemplate(templateFile, params);
        } catch (Exception ex) {
            logger.error("parse template error for send sms error message.", ex);
        }
        return null;
    }

    private String getConditionMessageContent(ScheduleMessage scheduleMessage, String messageContent) {
        String smsContentView = scheduleMessage.getSmsTemplate().getContent();
        Pattern pattern = Pattern.compile(SMSMessageConstants.MESSAGE_PATTERN);
        Matcher matcher = pattern.matcher(smsContentView);
        if (StringUtils.isNotEmpty(messageContent)) {
            String[] parameters = messageContent.split("\\|");
            int i = 1;
            while (matcher.find()) {
                if (i < parameters.length) {
                    smsContentView = smsContentView.replace(matcher.group(0), parameters[i]);
                }
                i++;
            }
        }
        return smsContentView;
    }

    /**
     * 获取模板文件
     *
     * @return
     */
    private String getTemplateFile(int type) {
        String templateFile = "/velocity/errorAdhocMessageRemind.vm";
        if (type == 2) {
            templateFile = "/velocity/errorScheduleMessageRemind.vm";
        }
        return templateFile;
    }
}
