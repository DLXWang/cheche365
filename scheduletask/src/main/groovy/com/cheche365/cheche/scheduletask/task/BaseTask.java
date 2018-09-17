package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.constants.ExceptionConstants;
import com.cheche365.cheche.scheduletask.exception.TaskException;
import com.cheche365.cheche.scheduletask.model.AttachmentData;
import com.cheche365.cheche.scheduletask.model.EmailConfig;
import com.cheche365.cheche.scheduletask.model.ExcelAttachmentConfig;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.service.common.IAttachmentService;
import com.cheche365.cheche.scheduletask.service.common.IMessageService;
import com.cheche365.cheche.scheduletask.service.common.TaskRunningService;
import com.cheche365.cheche.scheduletask.util.ParameterUtil;
import com.cheche365.cheche.scheduletask.util.VelocityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guoweifu on 2015/11/11.
 */
public abstract class BaseTask {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("emailMessageService")
    private IMessageService emailMessageService;

    @Autowired
    @Qualifier("smsMessageService")
    private IMessageService smsMessageService;

    @Autowired
    private TaskRunningService taskRunningService;

    @Autowired
    @Qualifier("excelAttachmentService")
    private IAttachmentService excelAttachmentService;

    private EmailConfig emailConfig;
    private static final String EMAIL_CONFIG_ERROR_PATH = "/emailconfig/task_error.yml";
    protected boolean send = true;
    protected Integer dataSize = 0;
    /**
     * 信息list
     */
    protected List<MessageInfo> messageInfoList = new ArrayList<>();

    public boolean process() {
        String className = this.getClass().getName();
        //判断此任务是否可以运行
        boolean runSuccess = true;
        if (!Boolean.TRUE.equals(TaskRunningService.taskRunningMap.putIfAbsent(className, true))) {
            try {
                logger.info("定时任务：{}开始运行。。。", className);
                taskRunningService.setRedisRunningFlag(className);
                //执行任务详情
                doProcess();
            } catch (Exception e) {
                runSuccess = false;
                logger.error("定时任务：{}运行错误", className, e);
                //任务执行异常发送邮件提醒
                sendErrorMessage(e.getMessage(), EMAIL_CONFIG_ERROR_PATH);
            }
            sendOnOff();
            try {
                //发送信息
                sendMessage();
            } catch (Exception e) {
                runSuccess = false;
                logger.error("定时任务：{}运行错误", className, e);
                //任务执行异常发送邮件提醒
                sendErrorMessage(e.getMessage(), EMAIL_CONFIG_ERROR_PATH);
            } finally {
                //移除当前任务运行中标志
                TaskRunningService.taskRunningMap.put(className, false);
                taskRunningService.removeRedisRunningFlag(className);
            }
            if (runSuccess) {
                logger.info("定时任务：{}运行完成", className);
            }
        }
        return runSuccess;
    }

    /**
     * 执行任务详细内容
     */
    protected abstract void doProcess() throws Exception;

    /**
     * 发送信息
     */
    private void sendMessage() {
        if (!CollectionUtils.isEmpty(messageInfoList)) {
            if (send) {
                messageInfoList.forEach(messageInfo -> {
                    if (messageInfo != null) {
                        //发送消息
                        if (messageInfo.getEmailInfo() != null) {
                            //发送邮件信息
                            emailMessageService.sendMessage(messageInfo);
                        }
                        if (messageInfo.getSmsInfo() != null) {
                            //发送短信信息
                            smsMessageService.sendMessage(messageInfo);
                        }
                    }
                });
            }
            messageInfoList.clear();
        }
    }

    /**
     * 发送异常邮件
     */
    protected void sendErrorMessage(String errorMessage, String emailConfigPath) {
        /*
        为SP12-job自动化测试临时处理
         */
        if (System.getProperty("sendMail") != null) {
            return;
        }
        //邮件内容
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("env", RuntimeUtil.getEvnProfile());
        paramMap.put("taskName", this.getClass().getName());//任务名称
        paramMap.put("errorMessage", errorMessage == null ? "" : errorMessage);//异常信息
        paramMap.put("dateTime", DateUtils.getCurrentDateString(DateUtils.DATE_LONGTIME24_PATTERN));
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setEmailInfo(assembleEmailInfo(emailConfigPath, paramMap));
        //发送邮件
        emailMessageService.sendMessage(messageInfo);
    }


    /**
     * load 邮件配置
     *
     * @param emailConfigPath 配置路径
     */
    void loadEmailConfig(String emailConfigPath) {
        Yaml yaml = new Yaml();
        InputStream inputStream = RestartTask.class.getResourceAsStream(emailConfigPath);
        emailConfig = yaml.loadAs(inputStream, EmailConfig.class);
        if (RuntimeUtil.isProductionEnv()) {
            emailConfig.setTos(emailConfig.getProduction_tos());
            emailConfig.setCcs(emailConfig.getProduction_ccs());
        }
    }

    /**
     * 封装邮件信息
     *
     * @param emailConfigPath 任务邮件配置路径
     * @param paramMap        任务参数 用于生成邮件内容和附件
     * @return EmailInfo
     */
    protected EmailInfo assembleEmailInfo(String emailConfigPath, Map<String, Object> paramMap) {
        if (emailConfig == null) {
            loadEmailConfig(emailConfigPath);
        }
        EmailInfo emailInfo = new EmailInfo();
        // 邮件标题
        String title = ParameterUtil.replaceParamForStr(paramMap, emailConfig.getTitle());
        if (!RuntimeUtil.isProductionEnv()) {
            title = "(" + RuntimeUtil.getEvnProfile() + ")" + title;
        }
        emailInfo.setSubject(title);
        //邮件接收人
        emailInfo.setTo(emailConfig.getTos());
        emailInfo.setCc(emailConfig.getCcs());
        try {
            // 邮件内容
            String content = VelocityUtil.getInstance().parseVelocityTemplate(emailConfig.getTemplate(), paramMap);
            emailInfo.setContent(content);
        } catch (Exception e) {
            logger.error("parse message template error.", e);
            throw new TaskException(ExceptionConstants.EXCEPTION_PARSE_TEMPLATE, ExceptionConstants.EXCEPTION_PARSE_TEMPLATE_MESSAGE);
        }

        return emailInfo;
    }

    protected void addSimpleAttachment(EmailInfo emailInfo, String emailConfigPath, Map<String, Object> paramMap, List<? extends AttachmentData> attachmentDataList) throws IOException {
        if (emailConfig == null) {
            loadEmailConfig(emailConfigPath);
        }
        this.dataSize = attachmentDataList.size();
        //获取配置的列数据
        ExcelAttachmentConfig excelAttachmentConfig = emailConfig.getExcelAttachmentConfig();
        if (excelAttachmentConfig != null) {
            Map<String, String> attachmentFileMap = excelAttachmentService.createSimpleAttachment(attachmentDataList, paramMap, excelAttachmentConfig);
            // 邮件附件
            if (attachmentFileMap != null && !attachmentFileMap.isEmpty()) {
                emailInfo.addAttachment(attachmentFileMap.get("fileName"), attachmentFileMap.get("filePath"));
            }
        }
    }

    protected void addAttachment(EmailInfo emailInfo, String emailConfigPath, Map<String, Object> paramMap, Map<String, ? extends List<? extends AttachmentData>> sheetDataMaps) throws IOException {
        if (emailConfig == null) {
            loadEmailConfig(emailConfigPath);
        }
        //获取配置的列数据
        ExcelAttachmentConfig excelAttachmentConfig = emailConfig.getExcelAttachmentConfig();
        if (excelAttachmentConfig != null) {
            Map<String, String> attachmentFileMap = excelAttachmentService.createAttachment(excelAttachmentConfig, paramMap, sheetDataMaps);
            // 邮件附件
            if (attachmentFileMap != null && !attachmentFileMap.isEmpty()) {
                emailInfo.addAttachment(attachmentFileMap.get("fileName"), attachmentFileMap.get("filePath"));
            }
        }
    }

    protected void sendOnOff() {
        send = true;
    }
}
