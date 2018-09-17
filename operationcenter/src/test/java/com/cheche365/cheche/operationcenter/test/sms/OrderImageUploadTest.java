package com.cheche365.cheche.operationcenter.test.sms;

import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import org.junit.Test;

/**
 * Created by xu.yelong on 2016/8/26.
 */
public class OrderImageUploadTest extends BaseTest {
    @Test
    public void test() throws InterruptedException {
        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.RECOMMENDED_ORDER_IMAGE_UPLOAD.getId().toString());
        paramMap.put(SmsCodeConstant.INSURANCE_COMPANY_NAME, "安心保险");
        paramMap.put(SmsCodeConstant.M_ORDER_LINK, "I20150516000016");
        paramMap.put(SmsCodeConstant.ORDER_ORDER_NO, "I20150516000016");
        process();

        Thread.sleep(10000000l);
    }
}
