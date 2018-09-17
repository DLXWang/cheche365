package com.cheche365.cheche.core.service.sms;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.cheche365.cheche.core.model.Channel.Enum.PARTNER_JD;
import static com.cheche365.cheche.core.model.Channel.Enum.PARTNER_TUHU_203;
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000;

/**
 * Created by mahong on 2015/11/11.
 */
public class ConditionTriggerUtil {

    private static Logger logger = LoggerFactory.getLogger(ConditionTriggerUtil.class);

    /**
     * 此方法调用短信中心接口发送短信验证码
     * 默认验证码长度为6位
     * 默认有效时间为20分钟
     */
    public static String sendValidateCodeMessage(ConditionTriggerHandler handler, String mobile, Channel channel) {
        return sendValidateCodeMessage(handler, mobile, 6, "20", channel);
    }

    /**
     * 此方法调用短信中心接口发送短信验证码
     * 默认有效时间为20分钟
     */
    public static String sendValidateCodeMessage(ConditionTriggerHandler handler, String mobile, Integer codeLength, Channel channel) {
        return sendValidateCodeMessage(handler, mobile, codeLength, "20", channel);
    }

    /**
     * 此方法调用短信中心接口发送短信验证码
     */
    public static String sendValidateCodeMessage(ConditionTriggerHandler handler, String mobile, Integer codeLength, String minute, Channel channel) {
        try {
            if (codeLength == null || codeLength.intValue() <= 0) {
                codeLength = 6;
            }

            if (StringUtils.isBlank(minute)) {
                minute = "20";
            }

            Map<String, String> parameterMap = new HashMap<>();
            getThirdPartnerParams(channel.getId(), parameterMap);
            parameterMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.getVerifyCode(channel).getId().toString());
            parameterMap.put(SmsCodeConstant.MOBILE, mobile);
            parameterMap.put(SmsCodeConstant.VERIFY_CODE, RandomStringUtils.randomNumeric(codeLength));
            parameterMap.put(SmsCodeConstant.MINUTE, minute);

            handler.process(parameterMap);
        } catch (Exception e) {
            logger.error("手机号" + mobile + "发送验证码失败." + ExceptionUtils.getStackTrace(e));
        }
        return StringUtils.EMPTY;
    }

    /**
     * 除手机号外无别的触发条件参数，可用此方法调用短信中心接口发送短信
     *
     * @param handler
     * @param condition
     * @param mobile
     */
    public static void sendSimpleMessage(ConditionTriggerHandler handler, ScheduleCondition condition, String mobile) {
        try {
            Map<String, String> parameterMap = new HashMap<>();
            parameterMap.put(SmsCodeConstant.TYPE, condition.getId().toString());
            parameterMap.put(SmsCodeConstant.MOBILE, mobile);
            handler.process(parameterMap);
        } catch (Exception e) {
            logger.error("调用短信中心接口发送短信异常，手机号:" + mobile + "." + ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 触发条件参数:手机号 + 红包金额，可用此方法调用短信中心接口发送短信
     *
     * @param handler
     * @param condition
     * @param mobile
     * @param giftAmount
     */
    public static void sendSimpleMessage(ConditionTriggerHandler handler, ScheduleCondition condition, String mobile, Double giftAmount) {
        try {
            Map<String, String> parameterMap = new HashMap<>();
            parameterMap.put(SmsCodeConstant.TYPE, condition.getId().toString());
            parameterMap.put(SmsCodeConstant.MOBILE, mobile);
            if (giftAmount != null && giftAmount > 0) {
                parameterMap.put("giftAmount", String.valueOf(giftAmount));
            }
            handler.process(parameterMap);
        } catch (Exception e) {
            logger.error("调用短信中心接口发送短信异常，手机号:" + mobile + "." + ExceptionUtils.getStackTrace(e));
        }

    }

    /**
     * 第三方及普通的短信发送方法，区别在于增加了第三方的名称和第三方的支持电话的参数
     *
     * @param handler
     * @param condition
     * @param mobile
     * @param channelId
     */
    public static void sendSimpleMessage(ConditionTriggerHandler handler, ScheduleCondition condition, String mobile, Long channelId) {
        try {
            Map<String, String> parameterMap = new HashMap<>();
            parameterMap.put(SmsCodeConstant.TYPE, condition.getId().toString());
            parameterMap.put(SmsCodeConstant.MOBILE, mobile);
            getThirdPartnerParams(channelId, parameterMap);
            handler.process(parameterMap);
        } catch (Exception e) {
            logger.error("调用短信中心接口发送短信异常，手机号:" + mobile + "." + ExceptionUtils.getStackTrace(e));
        }
    }

    public static Boolean sendMsgNotAllowed(QuoteRecord quoteRecord) {
        return (ANSWERN_65000 == quoteRecord.getInsuranceCompany() && PARTNER_TUHU_203 == quoteRecord.getChannel());
    }

    /**
     * 可用此方法调用短信中心接口发送短信
     */
    public static void sendMessage(ConditionTriggerHandler handler, Map<String, String> parameterMap) {
        try {
            handler.process(parameterMap);
        } catch (Exception e) {
            logger.error("调用短信中心接口发送短信异常." + ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 提交订单后用此方法调用短信中心接口发送短信
     */
    public static void sendOrderCommitMessage(ConditionTriggerHandler handler, PurchaseOrder order, InsuranceCompany insuranceCompany) {
        try {
            if (!needSendOrderCommit(order)) return;

            ScheduleCondition condition;
            QuoteRecord quoteRecord = ApplicationContextHolder.getApplicationContext().getBean(QuoteRecordRepository.class).findOne(order.getObjId());
            //京东正常的提交订单短信模板有支付链接，所以如果京东模糊报价发另一个短信模板不包含支付链接
            if (order.getSourceChannel().getParent().getId().equals(PARTNER_JD.getId()) && quoteRecord.getType().getId().equals(QuoteSource.Enum.RULEENGINE2_8.getId())){
                condition = ScheduleCondition.Enum.JD_ORDER_COMMIT_NOT_ALLOW_PAY;
            } else {
                condition = ScheduleCondition.Enum.getCommitOrder(order.getSourceChannel());
            }

            Map<String, String> parameterMap = new HashMap<>();
            getThirdPartnerParams(order.getSourceChannel().getId(), parameterMap);
            parameterMap.put(SmsCodeConstant.ORDER_ORDER_NO, order.getOrderNo());
            parameterMap.put(SmsCodeConstant.M_PAYMENT_LINK, order.getOrderNo());
            parameterMap.put(SmsCodeConstant.M_ORDER_DETAIL, order.getOrderNo());
            parameterMap.put(SmsCodeConstant.TYPE, condition.getId().toString());
            parameterMap.put(SmsCodeConstant.MOBILE, order.getApplicant().getMobile());
            parameterMap.put(SmsCodeConstant.AMOUNT, String.valueOf(DoubleUtils.displayDoubleValue(order.getPaidAmount())));
            parameterMap.put(SmsCodeConstant.CODE, insuranceCompany.getName());
            parameterMap.put(SmsCodeConstant.INSURANCE_COMPANY_NAME, insuranceCompany.getName());
            handler.process(parameterMap);
        } catch (Exception e) {
            logger.error("提交订单后调用短信中心接口发送短信异常." + ExceptionUtils.getStackTrace(e));
        }

    }

    private static boolean needSendOrderCommit(PurchaseOrder order) {
        if (!OrderStatus.Enum.PENDING_PAYMENT_1.getId().equals(order.getStatus().getId())) {
            return false;
        }

        if (null == order.getSourceChannel()) {
            return false;
        }

        if (null != order.getSourceChannel() && order.getSourceChannel().equals(Channel.Enum.PARTNER_RRYP_40)) {
            return false;
        }

        return !order.getSourceChannel().isOrderCenterChannel();
    }

    public static void sendPaymentSuccessMessage(ConditionTriggerHandler handler, QuoteRecord quoteRecord, PurchaseOrder purchaseOrder) {
        try {
            ScheduleCondition scheduleCondition = ScheduleCondition.Enum.getPaymentSuccess(purchaseOrder.getSourceChannel());
            if (purchaseOrder.getSourceChannel().equals(Channel.Enum.PARTNER_RRYP_40)) {
                return;
            }

            Map<String, String> parameterMap = new HashMap<>();
            parameterMap.put(SmsCodeConstant.ORDER_ORDER_NO, purchaseOrder.getOrderNo());
            parameterMap.put(SmsCodeConstant.TYPE, scheduleCondition.getId().toString());
            parameterMap.put(SmsCodeConstant.MOBILE, purchaseOrder.getApplicant().getMobile());
            parameterMap.put(SmsCodeConstant.INSURANCE_COMPANY_NAME, quoteRecord.getInsuranceCompany().getName());
            getThirdPartnerParams(purchaseOrder.getSourceChannel().getId(), parameterMap);
            handler.process(parameterMap);
        } catch (Exception e) {
            logger.error("支付成功后调用短信中心接口发送短信异常." + ExceptionUtils.getStackTrace(e));
        }
    }

    public static void sendOrderCancelMessage(ConditionTriggerHandler handler, PurchaseOrder purchaseOrder, QuoteRecord quoteRecord){

        ScheduleCondition condition = ScheduleCondition.Enum.getCancelOrder(purchaseOrder.getSourceChannel());
        String mobile = purchaseOrder.getApplicant().getMobile();
        try {
            Map<String, String> parameterMap = new HashMap<>();
            parameterMap.put(SmsCodeConstant.TYPE, condition.getId().toString());
            parameterMap.put(SmsCodeConstant.MOBILE, mobile);
            parameterMap.put(SmsCodeConstant.INSURANCE_COMPANY_NAME, quoteRecord.getInsuranceCompany().getName());
            getThirdPartnerParams(purchaseOrder.getSourceChannel().getId(), parameterMap);
            handler.process(parameterMap);
        } catch (Exception e) {
            logger.error("订单取消后调用短信中心接口发送短信异常，手机号:" + mobile + "." + ExceptionUtils.getStackTrace(e));
        }

    }

    public static String getThirdPartnerName(Long channelId) {
        Channel channel = Channel.toChannel(channelId);
        if (channel.isThirdPartnerChannel()) {
            return channel.getApiPartner().getDescription();
        }
        return WebConstants.SHORT_COMPANY_NAME;
    }

    public static String getThirdPartnerMobile(Long channelId) {
        String partnerMobile;
        if (Channel.isInGroup(channelId, Channel.Enum.PARTNER_BAIDU_15)) {
            partnerMobile = WebConstants.THIRD_PARTNER_MOBILE_BAIDU;
        } else {
            partnerMobile = WebConstants.CHECHE_CUSTOMER_SERVICE_MOBILE;
        }

        return partnerMobile;
    }

    public static Map<String, String> getThirdPartnerParams(Long channelId, Map<String, String> paramsMap) {
        if (null == paramsMap) paramsMap = new HashMap<>();
        paramsMap.put(SmsCodeConstant.THIRD_PARTNER_NAME, getThirdPartnerName(channelId));
        paramsMap.put(SmsCodeConstant.THIRD_PARTNER_MOBILE, getThirdPartnerMobile(channelId));
        return paramsMap;
    }
}
