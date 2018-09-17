package com.cheche365.cheche.scheduletask.task.tide;

import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yinJianBin on 2018/5/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {ScheduleTaskConfig.class}
)
public class TideStatusNoChangeSmsRemindByHourTaskTest {

    @Autowired
    TideStatusNoChangeSmsRemindByDayTask tideStatusNoChangeSmsRemindByDayTask;

    @Test
    public void doProcess() throws InterruptedException {
//        while (true) {
//            tideStatusNoChangeSmsRemindByDayTask.process();
//
//            Thread.sleep(1000 * 60);
//        }
    }
}