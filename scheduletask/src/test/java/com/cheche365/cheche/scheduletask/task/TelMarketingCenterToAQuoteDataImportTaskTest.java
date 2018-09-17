package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yinJianBin on 2017/7/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ScheduleTaskConfig.class)
public class TelMarketingCenterToAQuoteDataImportTaskTest {

    @Autowired
    private TelMarketingCenterToAQuoteDataImportTask telMarketingCenterToAQuoteDataImportTask;

    @Test
    public void testDoProcess() throws Exception {

    }

    @Test
    public void testProcess() throws Exception {
        telMarketingCenterToAQuoteDataImportTask.process();
    }
}