package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.ScheduleTaskBaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by yinJianBin on 2018/6/27.
 */
public class ScheduleTaskProductTest extends ScheduleTaskBaseTest {

    @Autowired
    TelMarketingCenterGenerateOrderReportTask telMarketingCenterGenerateOrderReportTask;
    @Autowired
    TelMarketingCenterCallRecordReportTask telMarketingCenterCallRecordReportTask;
    @Autowired
    XiaomiReportTask xiaomiReportTask;

    @Test
    void process() {
        xiaomiReportTask.process();
    }
}