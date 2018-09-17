package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yinJianBin on 2018/4/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ScheduleTaskConfig.class)
public class ChebaoyiWithdrawalReportTaskTest {

    @Autowired
    private ChebaoyiWithdrawalReportTask chebaoyiWithdrawalReportTask;

    @Test
    public void process() {
         chebaoyiWithdrawalReportTask.process();
    }

}
