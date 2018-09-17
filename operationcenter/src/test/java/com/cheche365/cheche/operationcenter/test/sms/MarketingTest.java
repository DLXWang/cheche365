package com.cheche365.cheche.operationcenter.test.sms;

import com.cheche365.cheche.operationcenter.service.marketingRule.MarketingRuleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xu.yelong on 2016-06-21.
 * 各种活动短信
 */
public class MarketingTest extends BaseTest {

    @Autowired
    private MarketingRuleService marketingRuleService;

    @Test
    public void testMarketingRulePublish() throws InterruptedException {
        String[] rules = new String[2];
        rules[0] = "795";
        rules[1] = "796";
        marketingRuleService.refreshMarketingRules(rules);
    }


    @Test
    public void test() {
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.RED_ACTIVITY_5500.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.AUTO_TAX_ACTIVITY.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.COUPON_ACTIVITY_200_SGS.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.MARKETING_RED_PACKET_DOUBLE_11.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.PAYMENT_SUCCESS.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.ALIPAY_PAYMENT_SUCCESS.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_ALIPAY_PAYMENT_SUCCESS.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.AUTOHOME_PAYMENT_SUCCESS.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_AUTOHOME_PAYMENT_SUCCESS.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.BDMAP_PAYMENT_SUCCESS.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_BDMAP_PAYMENT_SUCCESS.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.ORDER_CANCEL.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.ALIPAY_ORDER_CANCEL.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_ALIPAY_ORDER_CANCEL.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.AUTOHOME_ORDER_CANCEL.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_AUTOHOME_ORDER_CANCEL.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.BDMAP_ORDER_CANCEL.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_BDMAP_ORDER_CANCEL.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_ALIPAY_NO_PAYMENT_REMIND.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.COUPON_FUEL_CARD_100_BAIDU.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_TUHU_ORDER_CANCEL.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_TUHU_PAYMENT_SUCCESS.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.TUHU_PAYMENT_SUCCESS.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.TUHU_ORDER_CANCEL.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.OK619_PAYMENT_SUCCESS.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.OK619_ORDER_CANCEL.getId().toString());
        process();

    }
}
