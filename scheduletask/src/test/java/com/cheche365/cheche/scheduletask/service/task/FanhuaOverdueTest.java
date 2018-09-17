package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.FanhuaOverdueStatusChgTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Created by chenxiangyin on 2017/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
@TransactionConfiguration
public class FanhuaOverdueTest {

    @Autowired
    private FanhuaOverdueStatusChgTask fanhuaOverdueStatusChgTask;

    @Test
    public void test(){
        fanhuaOverdueStatusChgTask.process();
    }
}
