package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Created by wangshaobin on 2016/7/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class TelMarketingCenterRefundDataImportTaskTest {

    @Autowired
    TelMarketingCenterRefundDataImportService telMarketingCenterRefundDataImportService;

    @Test
    public void doProcess() {
        //导入申请退款的数据
        telMarketingCenterRefundDataImportService.importRefundOrderData();
    }


}
