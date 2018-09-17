package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.task.BaseTask;
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
public class TelMarketingCenterQuoteDataImportTaskTest extends BaseTask {

    @Autowired
    private TelMarketingCenterQuoteDataImportService telMarketingCenterQuoteDataImportService;

    @Autowired
    private TelMarketingCenterToAQuoteDataImportService telMarketingCenterToAQuoteDataImportService;

    @Test
    public void doProcess() throws Exception {
//        telMarketingCenterQuoteDataImportService.importQuoteData();
        telMarketingCenterToAQuoteDataImportService.importQuoteData();
    }
}
