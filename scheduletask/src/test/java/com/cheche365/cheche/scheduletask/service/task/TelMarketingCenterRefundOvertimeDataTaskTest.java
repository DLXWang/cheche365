package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class TelMarketingCenterRefundOvertimeDataTaskTest {

    @Autowired
    private TelMarketingCenterRefundOvertimeDataService telMarketingCenterRefundOvertimeDataService;

    @Test
    public void doProcess() throws Exception {
        //申请退款48小时仍未处理数据,直接设置为退款成功
        telMarketingCenterRefundOvertimeDataService.processOvertimeRefundOrderData();
    }
}
