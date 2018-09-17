package com.cheche365.cheche.operationcenter.test.sms;

import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xu.yelong on 2016-06-21.
 * 通知客户派送保单
 */
public class NotifyCustomerDeliveryTest extends BaseTest {
    @Test
    public void test() {
        Map paramMap = new HashMap();
        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.NOTIFY_CUSTOMER_DELIVERY.getId().toString());
        paramMap.put(SmsCodeConstant.CODE, "36853");
        process();
    }
}
