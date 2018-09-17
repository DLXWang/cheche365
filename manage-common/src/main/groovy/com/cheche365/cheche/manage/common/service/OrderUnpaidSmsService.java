package com.cheche365.cheche.manage.common.service;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterTaskType;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxy on 2018/3/1.
 */
@Service
@Slf4j
public class OrderUnpaidSmsService extends BaseService {
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;
    @Autowired
    private TelMarketingCenterChannelFilterService telMarketingCenterChannelFilterService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void sendUnpayOrderSMS() {
        Date now = new Date();
        String currentDateStr = DateUtils.getDateString(now, DateUtils.DATE_SHORTDATE_PATTERN);
        Integer time = Integer.parseInt(DateUtils.getDateString(now, "HH"));
        Date start;
        Date end;
        if (time == 11) {
            Date yesterday = DateUtils.calculateDateByDay(now, -1);
            start = DateUtils.getDate(DateUtils.getDateString(yesterday, DateUtils.DATE_SHORTDATE_PATTERN) + " 20:00:00", DateUtils.DATE_LONGTIME24_PATTERN);
            end = DateUtils.getDate(currentDateStr + " 09:59:59", DateUtils.DATE_LONGTIME24_PATTERN);
        } else if (time == 15) {
            start = DateUtils.getDate(currentDateStr + " 10:00:00", DateUtils.DATE_LONGTIME24_PATTERN);
            end = DateUtils.getDate(currentDateStr + " 13:59:59", DateUtils.DATE_LONGTIME24_PATTERN);
        } else {
            start = DateUtils.getDate(currentDateStr + " 14:00:00", DateUtils.DATE_LONGTIME24_PATTERN);
            end = DateUtils.getDate(currentDateStr + " 19:59:59", DateUtils.DATE_LONGTIME24_PATTERN);
        }
        List<PurchaseOrder> orderList = purchaseOrderRepository.findUnpayByDate(start, end);
        logger.debug("sending unpaid SMS num is " + orderList.size());
        List<Channel> excludeChannelList = telMarketingCenterChannelFilterService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.ORDER_PAYMENT_SMS_TASK);
        for (PurchaseOrder purchaseOrder : orderList) {
            if (excludeChannelList.contains(purchaseOrder.getSourceChannel())) {
                logger.debug("订单id:[{}],orderNo:[{}],对应的渠道:[{}]在过滤渠道配置中,跳过发送短信", purchaseOrder.getId(), purchaseOrder.getOrderNo(), purchaseOrder.getSourceChannel().getName());
                continue;
            }
            sendMessage(purchaseOrder);
        }
    }

    private void sendMessage(PurchaseOrder order) {
        Map paramMap = new HashMap();
        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.NO_PAYMENT_REMIND_TWICE.getId().toString());
        paramMap.put(SmsCodeConstant.MOBILE, order.getApplicant().getMobile());
        paramMap.put(SmsCodeConstant.M_PAYMENT_LINK, order.getOrderNo());
        conditionTriggerHandler.process(paramMap);
    }
}

