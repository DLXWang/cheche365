package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.DailyInsuranceStatusRefreshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by Luly on 2016/12/1.
 */
@Service
public class DailyInsuranceStatusRefreshTask extends BaseTask {
    @Autowired
    private DailyInsuranceStatusRefreshService dailyInsuranceStatusRefreshService;

    @Override
    protected void doProcess() throws Exception {
        //根据当前日期和"申请停驶"状态更新数据
        dailyInsuranceStatusRefreshService.updateDataStatusToStoped();
        //根据当前日期和"已停驶"状态更新数据
        dailyInsuranceStatusRefreshService.updateDataStatusToRestart();
    }
}
