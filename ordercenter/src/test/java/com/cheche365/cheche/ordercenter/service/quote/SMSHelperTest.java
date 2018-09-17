package com.cheche365.cheche.ordercenter.service.quote;

import com.cheche365.cheche.manage.common.service.sms.SMSHelper;
import com.cheche365.cheche.ordercenter.OrderCenterBaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by yinJianBin on 2018/1/25.
 */
public class SMSHelperTest extends OrderCenterBaseTest {

    @Autowired
    SMSHelper smsHelper;

    @Test
    public void sendQuoteDetailMsg() {
    }

    @Test
    public void sendCommitOrderMsg() {
        String orderNo = "T20170607000274";
        smsHelper.sendCommitOrderMsg(orderNo);
    }

    @Test
    public void sendOrderImageUploadMsg() {
    }

    @Test
    public void sendAmendQuoteMsg() {
    }
}
