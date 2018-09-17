package com.cheche365.cheche.operationcenter.test.sms;

import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xu.yelong on 2016-06-21.
 * 通知上门取款
 */
public class NotifyCustomerPaymentTest extends BaseTest {
    @Test
    public void test() {
        Map paramMap = new HashMap();
        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.NOTIFY_CUSTOMER_PAYMENT.getId().toString());
        paramMap.put(SmsCodeConstant.ORDER_ORDER_NO, "36853");
        process();
    }
}
