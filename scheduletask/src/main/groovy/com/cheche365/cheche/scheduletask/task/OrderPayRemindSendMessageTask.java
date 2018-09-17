package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterTaskType;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterChannelFilterService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by xu.yelong on 2015/12/29.
 * 发送前8小时至前半小时范围类订单支付提醒短信
 */
@Service
public class OrderPayRemindSendMessageTask extends BaseTask {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String ORDER_PAYMENT_REMIND_KEY = "schedules:task:payment:remind:order:id";

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;
    @Autowired
    private TelMarketingCenterChannelFilterService telMarketingCenterChannelFilterService;

    @Override
    protected void doProcess() throws Exception {
        List<PurchaseOrder> orders = getNoPayOrders();
        if (!CollectionUtils.isEmpty(orders)) {
            sendMessage(orders);
        }
    }

    //查询前8小时到前半小时未支付且未发送短信订单
    private List getNoPayOrders() {
        int maxId = 0;
        String id = redisTemplate.opsForValue().get(ORDER_PAYMENT_REMIND_KEY);
        if (!StringUtils.isEmpty(id)) {
            maxId = Integer.parseInt(id);
        }
        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.MINUTE, -30);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(currentTime);
        calendar2.add(Calendar.HOUR_OF_DAY, -8);
        List<PurchaseOrder> orders = purchaseOrderService.findUnPayOrderByCreateTimeBetween(calendar2.getTime(), calendar.getTime(), maxId);
        return orders;
    }


    private void sendMessage(List<PurchaseOrder> orders) {
        Map<String, String> parameterMap = new HashMap<>();
        Long orderId = orders.get(0).getId();
        List<Channel> excludeChannelList = telMarketingCenterChannelFilterService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.ORDER_PAY_REMIND_SEND_MESSAGE_TASK);
        for (PurchaseOrder purchaseOrder : orders) {
            if (purchaseOrder.getSourceChannel().equals(Channel.Enum.PARTNER_RRYP_40)) {
                continue;
            }
            String mobile = purchaseOrder.getApplicant() == null ? null : purchaseOrder.getApplicant().getMobile();
            if (mobile == null) {
                continue;
            }
            if (excludeChannelList.contains(purchaseOrder.getSourceChannel())) {
                logger.debug("订单id:[{}],orderNo:[{}],对应的渠道:[{}]在过滤渠道配置中,跳过发送短信", purchaseOrder.getId(), purchaseOrder.getOrderNo(), purchaseOrder.getSourceChannel().getName());
                continue;
            }
            QuoteRecord quoteRecord = purchaseOrderService.findQuoteRecord(purchaseOrder);
            if (quoteRecord == null || ConditionTriggerUtil.sendMsgNotAllowed(quoteRecord) || (quoteRecord.getType() != null && quoteRecord.getType().equals(QuoteSource.Enum.RULEENGINE2_8))) {
                continue;
            }
            parameterMap.put(SmsCodeConstant.MOBILE, mobile);
            ScheduleCondition condition = ScheduleCondition.Enum.getPaymentRemind(purchaseOrder.getSourceChannel());
            parameterMap.put(SmsCodeConstant.TYPE, condition.getId().toString());
            parameterMap.put(SmsCodeConstant.ORDER_ORDER_NO, purchaseOrder.getOrderNo());
            parameterMap.put(SmsCodeConstant.M_PAYMENT_LINK, purchaseOrder.getOrderNo());
            parameterMap.put(SmsCodeConstant.M_ORDER_DETAIL, purchaseOrder.getOrderNo());
            //第三方未支付提醒参数
            if (ScheduleCondition.Enum.PARTNER_NO_PAYMENT_REMIND.getId().equals(condition.getId())
                    || ScheduleCondition.Enum.CUSTOMER_QUOTE_PARTNER_NO_PAYMENT_REMIND.getId().equals(condition.getId())) {
                ConditionTriggerUtil.getThirdPartnerParams(purchaseOrder.getSourceChannel().getId(), parameterMap);
            }
            logger.debug("send payment remind message conditionName:{} ,mobile :{},orderNo :{}", condition.getName(), mobile, purchaseOrder.getOrderNo());
            conditionTriggerHandler.process(parameterMap);
        }
        redisTemplate.opsForValue().set(ORDER_PAYMENT_REMIND_KEY, String.valueOf(orderId));
    }
}
