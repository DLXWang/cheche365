package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterRefundOvertimeDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 申请退款订单用户
 * 每半小时执行一次
 * Created by yinjianbin on 2016/9/12.
 */
@Service
public class TelMarketingCenterRefundOvertimeDataTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(TelMarketingCenterRefundOvertimeDataTask.class);

    @Autowired
    private TelMarketingCenterRefundOvertimeDataService telMarketingCenterRefundOvertimeDataService;

    @Override
    protected void doProcess() throws Exception {
        //申请退款24小时仍未处理数据
        telMarketingCenterRefundOvertimeDataService.processOvertimeRefundOrderData();
    }
}
