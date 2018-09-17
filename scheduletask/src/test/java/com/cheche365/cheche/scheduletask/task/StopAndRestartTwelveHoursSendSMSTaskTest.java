package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Luly on 2017/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
public class StopAndRestartTwelveHoursSendSMSTaskTest {

    @Autowired
    private StopAndRestartTwelveHoursSendSMSTask stopAndRestartTwelveHoursSendSMSTask;

    @Test
    public void testProcess() throws Exception {
        stopAndRestartTwelveHoursSendSMSTask.doProcess();
    }
}
