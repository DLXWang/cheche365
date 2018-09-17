package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.FanhuaOverdueStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 泛华quotevliadtime过期自动退款
 * 定时时间：每天
 * Created by chenxiangyin on 2017/5/4.
 */
@Service
public class FanhuaOrderRefundChgTask extends BaseTask {
    Logger logger = LoggerFactory.getLogger(FanhuaOrderRefundChgTask.class);
    @Autowired
    FanhuaOverdueStatusService fanhuaOverdueStatusService;

    @Override
    protected void doProcess() throws Exception {
        fanhuaOverdueStatusService.chgQuoteValidTimeOverdue();
    }
}
