package com.cheche365.cheche.scheduletask;

import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.DailyInsuranceBasicReportTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by chenxiangyin on 2017/4/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
public class DailyInsuranceBasicReportTaskTest {

    @Autowired
    private DailyInsuranceBasicReportTask dailyInsuranceBasicReportTask;
    @Test
    public void test(){
        dailyInsuranceBasicReportTask.process();
    }
}
