package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.FanhuaOverdueStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 泛华提交退款3天后自动生效
 * 定时时间：每小时
 * Created by chenxiangyin on 2017/4/11.
 */
@Service
public class FanhuaOverdueStatusChgTask extends BaseTask {
    Logger logger = LoggerFactory.getLogger(FanhuaOverdueStatusChgTask.class);
    @Autowired
    FanhuaOverdueStatusService fanhuaOverdueStatusService;

    @Override
    protected void doProcess() throws Exception {
        fanhuaOverdueStatusService.chgFullRefundOverdue();//全部退款
        fanhuaOverdueStatusService.chgPartialRefundOverdue();//部分退款
    }
}
