package com.cheche365.cheche.core.service.sms

import com.cheche365.cheche.core.constants.SmsConstants
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.ScheduleConditionRepository
import com.cheche365.cheche.core.repository.ScheduleMessageLogRepository
import com.cheche365.cheche.core.repository.ScheduleMessageRepository
import com.cheche365.cheche.core.service.spi.ISystemUrlGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.yaml.snakeyaml.util.UriEncoder

import java.util.regex.Matcher
import java.util.regex.Pattern

import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.JD_AMEND_QUOTE_ORDER
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.JD_NO_PAYMENT_REMIND
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.JD_ORDER_CANCEL
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.JD_ORDER_COMMIT
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.JD_ORDER_COMMIT_NOT_ALLOW_PAY
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.JD_PAYMENT_SUCCESS
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.JD_RECOMMENDED_ORDER_IMAGE_UPLOAD
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.JD_REQUEST_VERIFY_CODE
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.NCI_NO_PAYMENT_REMIND
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.NCI_ORDER_CANCEL
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.NCI_ORDER_COMMIT
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.NCI_PAYMENT_SUCCESS
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.NCI_REQUEST_VERIFY_CODE
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.PARTNER_REQUEST_VERIFY_CODE
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.REQUEST_VERIFY_CODE

/**
 * Created by yinJianBin on 2017/7/25.
 */
@Service
class SmsGenerator {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String MESSAGE_PATTERN = '\\$\\{((\\w*)\\.?(\\w*))}';

    @Autowired
    ScheduleConditionRepository scheduleConditionRepository

    @Autowired
    ScheduleMessageRepository scheduleMessageRepository

    @Autowired
    ScheduleMessageLogRepository scheduleMessageLogRepository

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Autowired(required = false)
    ISystemUrlGenerator systemUrlGenerator


    SmsInfo generateSmsInfo(Map paramMap) {
        ScheduleCondition condition = scheduleConditionRepository.findOne(paramMap.get(SmsCodeConstant.TYPE) as Long);
        ScheduleMessage scheduleMessage = scheduleMessageRepository.findFirstByScheduleConditionAndDisableOrderByUpdateTimeDesc(condition, false);
        if (null != scheduleMessage) {
            // 获取短信内容
            def content = getContent(scheduleMessage, paramMap)

            SmsInfo smsInfo = new SmsInfo()
            smsInfo.setMobile(paramMap.get(SmsCodeConstant.MOBILE) as String)
            smsInfo.setContent(content)
            smsInfo.setSmsChannel(getSmsChannel(condition, paramMap))
            smsInfo.setSmsType(getSmsType(scheduleMessage, paramMap))
            smsInfo.setScheduleMessageLogId(saveScheduleMessageLog(scheduleMessage, paramMap.get(SmsCodeConstant.MOBILE) as String, new Date(), 0, content).getId())
            smsInfo.setVerifyCode(paramMap.get(SmsCodeConstant.VERIFY_CODE) as String)

            logger.info("生成短信内容成功,smsInfo-->{}", smsInfo.toString())
            return smsInfo
        } else {
            logger.warn("触发条件（{}）缺少条件触发短信，无法发送短信，请去运营中心配置！", condition.getDescription());
        }
        return null;
    }

    static def getSmsType(ScheduleMessage scheduleMessage, paramMap) {
        if (isVerifyCodeSmsType(scheduleMessage.scheduleCondition)) {
            return SmsInfo.Enum.SMS_TYPE_VERIFY_CODE
        } else if (paramMap.get(SmsCodeConstant.VERIFY_CODE)) {
            return SmsInfo.Enum.SMS_TYPE_VERIFY_CODE
        }
        return SmsInfo.Enum.SMS_TYPE_MESSAGE
    }

    static boolean isVerifyCodeSmsType(ScheduleCondition condition){
        condition.id in [NCI_REQUEST_VERIFY_CODE.id, REQUEST_VERIFY_CODE.id, PARTNER_REQUEST_VERIFY_CODE.id, JD_REQUEST_VERIFY_CODE.id]
    }

    def getContent(ScheduleMessage scheduleMessage, Map paramMap) {
        SmsTemplate smsTemplate = scheduleMessage.getSmsTemplate()
        String message = smsTemplate.getContent();
        logger.debug("短信模版内容: {}", message)

        Pattern pattern = Pattern.compile(MESSAGE_PATTERN);
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String variableName = matcher.group(0) as String
            String value = paramMap.get(variableName)
            value = dealSpecialValue(variableName, value)
            message = message.replace(variableName, value)
        }
        message
    }

    def getSmsChannel(ScheduleCondition condition, Map parameterMap) {
        String orderNo = parameterMap.get(SmsCodeConstant.ORDER_ORDER_NO)
        PurchaseOrder purchaseOrder = orderNo ? purchaseOrderRepository.findFirstByOrderNo(orderNo) : null
        if (isNciSmsChannel(condition, purchaseOrder)){
            return SmsConstants._SMS_VENDOR_NCI
        } else if (isJdSmsChannel(condition, purchaseOrder)){
            return SmsConstants._SMS_VENDOR_JD
        }
        return SmsConstants._SMS_VENDOR_ZUCP
    }

    static boolean isJdSmsChannel(ScheduleCondition condition, PurchaseOrder purchaseOrder) {
        (condition.id in [JD_REQUEST_VERIFY_CODE.id, JD_ORDER_COMMIT.id, JD_PAYMENT_SUCCESS.id, JD_ORDER_CANCEL.id, JD_NO_PAYMENT_REMIND.id, JD_AMEND_QUOTE_ORDER.id, JD_RECOMMENDED_ORDER_IMAGE_UPLOAD.id, JD_ORDER_COMMIT_NOT_ALLOW_PAY.id]) || (purchaseOrder && Channel.isInGroup(purchaseOrder.getSourceChannel().getId(), Channel.Enum.PARTNER_JD))
    }

    static boolean isNciSmsChannel(ScheduleCondition condition, PurchaseOrder purchaseOrder){
        (condition.id in [NCI_REQUEST_VERIFY_CODE.id, NCI_ORDER_COMMIT.id, NCI_PAYMENT_SUCCESS.id, NCI_ORDER_CANCEL.id, NCI_NO_PAYMENT_REMIND.id]) || (purchaseOrder && Channel.isInGroup(purchaseOrder.getSourceChannel().getId(), Channel.Enum.PARTNER_NCI_25))

    }


    def saveScheduleMessageLog(ScheduleMessage scheduleMessage, String mobile, Date sendTime, Integer status, String content) {
        ScheduleMessageLog scheduleMessageLog = new ScheduleMessageLog();
        scheduleMessageLog.setScheduleMessage(scheduleMessage);//条件触发的短信
        scheduleMessageLog.setMobile(mobile);//手机号
        scheduleMessageLog.setSendTime(sendTime);
        scheduleMessageLog.setStatus(status);//发送状态1-成功，2-失败 , 0-发送中
        scheduleMessageLog.setParameter(content);//短信参数
        scheduleMessageLogRepository.save(scheduleMessageLog);
    }


    String dealSpecialValue(String key, String value) {
        String url = value
        if (SmsCodeConstant.M_PAYMENT_LINK.equals(key)) {
            url = systemUrlGenerator.toPaymentUrl(value);
        } else if (SmsCodeConstant.M_ORDER_LINK.equals(key)) {
            url = systemUrlGenerator.toImageUrl(value);
        } else if (SmsCodeConstant.SUSPEND_BILL_LINK.equals(key)) {
            url = systemUrlGenerator.toSuspendBillUrlOriginal(value)
        } else if (SmsCodeConstant.RENEWAL_PAY_LINK.equals(key)) {
            url = UriEncoder.decode(systemUrlGenerator.renewalOrder(value))
        } else if (SmsCodeConstant.M_ORDER_DETAIL.equals(key)){
            url = systemUrlGenerator.toOrderDetailUrl(value)
        }
        return url ? UriEncoder.decode(url) : ''
    }

}
