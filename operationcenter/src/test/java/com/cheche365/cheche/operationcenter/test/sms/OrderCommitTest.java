package com.cheche365.cheche.operationcenter.test.sms;

import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import org.junit.Test;

/**
 * Created by xu.yelong on 2016-06-21.
 * 订单提交短信
 */
public class OrderCommitTest extends BaseTest {
    @Test
    public void test() throws InterruptedException {
        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.ORDER_COMMIT.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.ALIPAY_ORDER_COMMIT.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.AUTOHOME_ORDER_COMMIT.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.BDMAP_ORDER_COMMIT.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.NEW_YEAR_PURCHASE_ORDER_PAY.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.NEW_YEAR_PURCHASE_ORDER_PAY_NORMAL.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.TUHU_ORDER_COMMIT.getId().toString());
//        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.OK619_ORDER_COMMIT.getId().toString());
        paramMap.put(SmsCodeConstant.ORDER_ORDER_NO, "36853");
        paramMap.put(SmsCodeConstant.AMOUNT, "8888");
        paramMap.put(SmsCodeConstant.CODE, InsuranceCompany.Enum.PICC_10000.getName());
        process();
        Thread.sleep(10000000L);
    }
}
