package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.netflix.discovery.converters.Auto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yellow on 2017/10/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = ScheduleTaskConfig.class
)
public class AnswernReportTest {

    @Autowired
    private AnswernUltimoInsuranceReportTask answernUltimoInsuranceReportTask;

    @Autowired
    private AnswernOrderReportByDayTask answernOrderReportByDayTask;

    @Test
    public void testDoProcess() throws Exception {
       // answernUltimoInsuranceReportTask.process();
        answernOrderReportByDayTask.process();
    }

}
