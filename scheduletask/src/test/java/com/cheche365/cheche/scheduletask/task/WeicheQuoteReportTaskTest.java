package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.model.WeicheQuoteEmailInfo;
import com.cheche365.cheche.scheduletask.service.task.WeicheQuoteReportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by yinJianBin on 2017/2/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
public class WeicheQuoteReportTaskTest {

    @Autowired
    private WeicheQuoteReportTask weicheQuoteReportTask;

    @Autowired
    private WeicheQuoteReportService weicheQuoteReportService;

    @Test
    public void testDoProcess() throws Exception {
        weicheQuoteReportTask.process();
    }

    @Test
    public void testGetDataList() {
        List<WeicheQuoteEmailInfo> emailInfoList = weicheQuoteReportService.getEmailInfoList();
        System.out.println(emailInfoList.size());
    }
}
