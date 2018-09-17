package com.cheche365.cheche.scheduletask.task.chebaoyi;

import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.scheduletask.ScheduleTaskBaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by yinJianBin on 2018/8/1.
 */
public class ChebaoyiLevelOneQuoteDataReportTaskTest extends ScheduleTaskBaseTest {

    @Autowired(required = false)
    ChebaoyiLevelOneQuoteDataReportTask chebaoyiLevelOneQuoteDataReportTask;
    @Autowired(required = false)
    MoApplicationLogRepository moApplicationLogRepository;

    @Test
    public void testProcess() {
        chebaoyiLevelOneQuoteDataReportTask.process();
    }

}