package com.cheche365.cheche.operationcenter.test.sms;

import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import org.junit.Test;

/**
 * Created by xu.yelong on 2016-06-13.
 * 短信6位验证码
 */
public class VerifyCodeTest extends BaseTest {
    @Test
    public void test() {
        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.REQUEST_VERIFY_CODE.getId().toString());
        paramMap.put(SmsCodeConstant.VERIFY_CODE, "110110");
        paramMap.put(SmsCodeConstant.MINUTE, "10");
        process();
    }
}
