package com.cheche365.cheche.operationcenter.test.sms;

import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xu.yelong on 2016-06-14.
 * 支付提醒短信
 */
public class PaymentRemindTest extends BaseTest {
    @Test
    public void test() {
        Map paramMap = new HashMap();
        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.NO_PAYMENT_REMIND.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_BDMAP_NO_PAYMENT_REMIND.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_AUTOHOME_NO_PAYMENT_REMIND.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_TUHU_NO_PAYMENT_REMIND.getId().toString());
        paramMap.put(SmsCodeConstant.ORDER_ORDER_NO, "T20160620000019");
        process();
    }
}
