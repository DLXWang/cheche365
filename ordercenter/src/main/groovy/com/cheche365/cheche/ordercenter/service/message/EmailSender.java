package com.cheche365.cheche.ordercenter.service.message;

import com.cheche365.cheche.email.exception.EmailException;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.email.service.IEmailService;
import com.cheche365.cheche.ordercenter.constants.OrderCenterConstants;
import com.cheche365.cheche.ordercenter.exception.OrderCenterException;
import com.cheche365.cheche.ordercenter.util.VelocityUtil;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 邮件发送器实现类
 * Created by sunhuazhong on 2015/4/27.
 */
@Service("emailSender")
@Transactional
public class EmailSender implements ISender {

    private Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private IEmailService emailService;

    @Override
    public void sender(String status, Map<String, String> params, String... to) {
        String title = getTitle(status, params);// 消息标题
        String content = getContent(status, params);// 消息内容

        logger.info("标题：" + title);
        logger.info("内容：" + content);
        try {
            EmailInfo emailInfo = new EmailInfo();
            emailInfo.setTo(to);
            emailInfo.setSubject(title);
            emailInfo.setContent(content);
            emailService.sender(emailInfo);
            logger.info("发送邮件成功.");
        } catch(EmailException ex) {
            logger.error("发送邮件错误.", ex);
            throw new OrderCenterException(ex.getCode(), ex.getMessage());
        }
    }

    private String getContent(String status, Map params) {
        try {
            String templateFile = getTemplateFile(status);
            String content = VelocityUtil.getInstance().parseVelocityTemplate(templateFile, params);
            return content;
        } catch (ResourceNotFoundException e) {
            logger.error("parse template error.");
            throw new OrderCenterException(OrderCenterConstants.EXCEPTION_PARSE_TEMPLATE, OrderCenterConstants.EXCEPTION_PARSE_TEMPLATE_MESSAGE);
        } catch (ParseErrorException e) {
            logger.error("parse template error.");
            throw new OrderCenterException(OrderCenterConstants.EXCEPTION_PARSE_TEMPLATE, OrderCenterConstants.EXCEPTION_PARSE_TEMPLATE_MESSAGE);
        } catch (MethodInvocationException e) {
            logger.error("parse template error.");
            throw new OrderCenterException(OrderCenterConstants.EXCEPTION_PARSE_TEMPLATE, OrderCenterConstants.EXCEPTION_PARSE_TEMPLATE_MESSAGE);
        }
    }


    /**
     * 根据操作状态获取主题
     * 操作状态：1-未付款新订单；2-已付款新订单；3-出单提醒；4-核保完成提醒；5-订单取消；6-派送完成；7-收款；8-派送
     * @param status
     * @return
     */
    private String getTitle(String status, Map params) {
        String title = "";

        // 未付款新订单
        if(OrderCenterConstants.OPERATE_STATUS_EMAIL_NEW_ORDER_WITHOUT_PAYMENT.equals(status)) {
            title = "未付款新订单提醒";
        }
        // 已付款新订单
        if(OrderCenterConstants.OPERATE_STATUS_EMAIL_NEW_ORDER_WITH_PAYMENT.equals(status)) {
            title = "已付款新订单提醒";
        }
        // 出单提醒
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_INSURE_REMIND.equals(status)) {
            title = "有订单需要去出单";
        }
        // 核保完成提醒
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_UNDERWRITING_COMPLETE_REMIND.equals(status)) {
            title = "有订单核保完成";
        }
        // 录入保单
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_INPUT_INSURANCE.equals(status)) {
            title = "录入保单提醒";
        }
        // 订单取消
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_ORDER_CANCEL.equals(status)) {
            title = "订单" + params.get("orderNo") + "取消";
        }
        // 派送完成
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_DELIVERY_COMPLETE.equals(status)) {
            title = "订单" + params.get("orderNo") + "完成派送";
        }
        // 订单打回
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_RETURN.equals(status)) {
            title = "有订单被打回";
        }
        // 订单完成
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_ORDER_COMPLETE.equals(status)) {
            title = "有订单完成";
        }
        // 线上付款
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_PAYMENT.equals(status)) {
            title = "【付款订单】有订单核保完成";
        }

        return title;
    }

    /**
     * 根据操作状态获取模板文件
     * 操作状态：1-未付款新订单；2-已付款新订单；3-出单提醒；4-核保完成提醒；5-订单取消；6-派送完成；7-收款；8-派送；9-派送
     * @param status
     * @return
     */
    private String getTemplateFile(String status) {
        String templateFile = "";

        // 未付款新订单
        if(OrderCenterConstants.OPERATE_STATUS_EMAIL_NEW_ORDER_WITHOUT_PAYMENT.equals(status)) {
            templateFile = "/velocity/newOrderWithoutPayment.vm";
        }
        // 已付款新订单
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_NEW_ORDER_WITH_PAYMENT.equals(status)) {
            templateFile = "/velocity/newOrderWithPayment.vm";
        }
        // 出单提醒
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_INSURE_REMIND.equals(status)) {
            templateFile = "/velocity/insureRemind.vm";
        }
        // 核保完成提醒
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_UNDERWRITING_COMPLETE_REMIND.equals(status)) {
            templateFile = "/velocity/underwritingCompleteRemind.vm";
        }
        // 录入保单
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_INPUT_INSURANCE.equals(status)) {
            templateFile = "/velocity/inputInsurance.vm";
        }
        // 订单取消
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_ORDER_CANCEL.equals(status)) {
            templateFile = "/velocity/orderCancel.vm";
        }
        // 派送完成
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_DELIVERY_COMPLETE.equals(status)) {
            templateFile = "/velocity/deliveryComplete.vm";
        }
        // 订单打回
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_RETURN.equals(status)) {
            templateFile = "/velocity/return.vm";
        }
        // 订单完成
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_ORDER_COMPLETE.equals(status)) {
            templateFile = "/velocity/orderComplete.vm";
        }
        // 线上收款
        else if(OrderCenterConstants.OPERATE_STATUS_EMAIL_PAYMENT.equals(status)) {
            templateFile = "/velocity/onlinePayment.vm";
        }

        return templateFile;
    }
}
