package com.cheche365.cheche.manage.common.service.sms;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.serializer.SerializerUtil;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.QuoteRecordService;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangfei on 2016/1/12.
 */
@Component
public class SMSHelper {
    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private Logger logger = LoggerFactory.getLogger(SMSHelper.class);

    public void sendQuoteDetailMsg(QuoteRecord quoteRecord) {
        Map<String, String> parameterMap = new HashMap<>();
        Long channelId = quoteRecord.getChannel().getId();
        if (Channel.isInGroup(channelId, Channel.Enum.PARTNER_JD)){
            logger.info("京东渠道跳过报价详情短信发送");
            return;
        }
        //支付宝
        if (Channel.isInGroup(channelId, Channel.Enum.ALIPAY_21)) {
            parameterMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_DETAIL_ALIPAY.getId().toString());
        }
        //惠保
        else if (Channel.isInGroup(channelId, Channel.Enum.PARTNER_HUIBAO_75)) {
            parameterMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_DETAIL_HUIBAO.getId().toString());
            ConditionTriggerUtil.getThirdPartnerParams(channelId, parameterMap);
        }
        //第三方合作
        else if (quoteRecord.getChannel().isThirdPartnerChannel()) {
            parameterMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_DETAIL_THIRDPARTNER.getId().toString());
            ConditionTriggerUtil.getThirdPartnerParams(channelId, parameterMap);
        }
        //普通
        else {
            parameterMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_DETAIL_NORMAL.getId().toString());
        }
        parameterMap.put(SmsCodeConstant.MOBILE, quoteRecord.getApplicant().getMobile());
        parameterMap.put(SmsCodeConstant.INSURANCE_COMPANY_NAME, quoteRecord.getInsuranceCompany().getName());
        parameterMap.put(SmsCodeConstant.CUSTOMER_QUOTE_DETAIL, SerializerUtil.generateQuoteDetail(quoteRecord));
        conditionTriggerHandler.process(parameterMap);
    }

    public void sendCommitOrderMsg(String orderNo) {
        //临时功能 : 出单中心推送的支付链接存储至redis,web根据redis标记判断能否支付
        CacheUtil.putToSetWithDayExpire(stringRedisTemplate, WebConstants.ALLOW_ORDER_PAY, orderNo, 1);
        conditionTriggerHandler.process(generateParamMap(orderNo));
    }

    private Map<String, String> generateParamMap(String orderNo) {
        PurchaseOrder purchaseOrder = purchaseOrderService.getFirstPurchaseOrderByNo(orderNo);
        AssertUtil.notNull(purchaseOrder, "can not find purchaseOrder by orderNo : " + orderNo);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put(SmsCodeConstant.MOBILE, purchaseOrder.getApplicant().getMobile());

        Channel channel = purchaseOrder.getSourceChannel();
        PaymentChannel paymentChannel = PaymentChannel.Enum.format(purchaseOrder.getChannel());
        QuoteRecord quoteRecord = this.quoteRecordRepository.findOne(purchaseOrder.getObjId());
        parameterMap.put(SmsCodeConstant.ORDER_ORDER_NO, purchaseOrder.getOrderNo());
        //支付宝
        if (Channel.isInGroup(channel.getId(), Channel.Enum.ALIPAY_21)) {
            parameterMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_ORDER_ALIPAY.getId().toString());
        }
        //第三方合作
        else if (channel.isThirdPartnerChannel()) {
            ScheduleCondition condition;
            if (channel.getParent() != null && channel.getParent().getId().equals(Channel.Enum.PARTNER_JD.getId())) {
                condition = ScheduleCondition.Enum.JD_ORDER_COMMIT;
                parameterMap.put(SmsCodeConstant.M_ORDER_DETAIL, purchaseOrder.getOrderNo());
                parameterMap.put(SmsCodeConstant.INSURANCE_COMPANY_NAME, quoteRecord.getInsuranceCompany().getName());
                parameterMap.put(SmsCodeConstant.AMOUNT, String.valueOf(DoubleUtils.displayDoubleValue(purchaseOrder.getPaidAmount())));
            } else {
                condition = ScheduleCondition.Enum.CUSTOMER_QUOTE_ORDER_THIRDPARTNER;
            }
            parameterMap.put(SmsCodeConstant.TYPE, condition.getId().toString());

            ConditionTriggerUtil.getThirdPartnerParams(channel.getId(), parameterMap);
            if (purchaseOrder.getStatus().equals(OrderStatus.Enum.INSURE_FAILURE_7)) {
                parameterMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.INSURE_FAILURE_ORDER_DETAIL.getId().toString());
                parameterMap.put(SmsCodeConstant.M_ORDER_LINK, purchaseOrder.getOrderNo());
            } else {
                parameterMap.put(SmsCodeConstant.M_PAYMENT_LINK, purchaseOrder.getOrderNo());
            }
        }
        //普通
        else {
            parameterMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_ORDER_NORMAL_ONLINE.getId().toString());
            if (purchaseOrder.getStatus().equals(OrderStatus.Enum.INSURE_FAILURE_7)) {
                parameterMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.INSURE_FAILURE_ORDER_DETAIL.getId().toString());
                parameterMap.put(SmsCodeConstant.M_ORDER_LINK, purchaseOrder.getOrderNo());
            } else {
                parameterMap.put(SmsCodeConstant.M_PAYMENT_LINK, purchaseOrder.getOrderNo());
            }
        }
        parameterMap = ConditionTriggerUtil.getThirdPartnerParams(purchaseOrder.getSourceChannel().getId(), parameterMap);
        return parameterMap;
    }

    public void sendOrderImageUploadMsg(PurchaseOrder purchaseOrder) {
        User applicant = purchaseOrder.getApplicant();
        Map<String, String> parameterMap = new HashMap<>();
        QuoteRecord quoteRecord = quoteRecordService.getById(purchaseOrder.getObjId());
        InsuranceCompany insuranceCompany = quoteRecord.getInsuranceCompany();
        parameterMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.getUploadOrderImage(purchaseOrder.getSourceChannel()).getId().toString());
        parameterMap.put(SmsCodeConstant.ORDER_ORDER_NO, purchaseOrder.getOrderNo());
        parameterMap.put(SmsCodeConstant.MOBILE, applicant.getMobile());
        parameterMap.put(SmsCodeConstant.M_ORDER_LINK, purchaseOrder.getOrderNo());
        parameterMap.put(SmsCodeConstant.INSURANCE_COMPANY_NAME, insuranceCompany.getName());
        parameterMap.put(SmsCodeConstant.M_ORDER_DETAIL, purchaseOrder.getOrderNo());
        conditionTriggerHandler.process(parameterMap);
    }

    public void sendAmendQuoteMsg(QuoteRecord quoteRecord) {
        Map<String, String> parameterMap = new HashMap<>();
        PurchaseOrder purchaseOrder = purchaseOrderService.findByQuoteRecordId(quoteRecord.getId());
        parameterMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.getAmend(purchaseOrder.getSourceChannel()).getId().toString());
        parameterMap.put(SmsCodeConstant.ORDER_ORDER_NO, purchaseOrder.getOrderNo());
        parameterMap.put(SmsCodeConstant.MOBILE, quoteRecord.getApplicant().getMobile());
        parameterMap.put(SmsCodeConstant.M_ORDER_DETAIL, purchaseOrder.getOrderNo());
        CacheUtil.putToSetWithDayExpire(stringRedisTemplate, WebConstants.ALLOW_ORDER_PAY, purchaseOrder.getOrderNo(), 1);
        conditionTriggerHandler.process(parameterMap);
    }
}
