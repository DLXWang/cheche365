package com.cheche365.cheche.scheduletask.task.tide;

import com.cheche365.cheche.scheduletask.ScheduleTaskBaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by yinJianBin on 2018/6/1.
 */
public class TideContractRebateBackupTaskTest extends ScheduleTaskBaseTest {

    @Autowired
    private TideContractRebateBackupTask backupTask;


    @Test
    public void process() {
        backupTask.process();
    }
}
