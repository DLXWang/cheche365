package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterRefundDataImportService;
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
public class TelMarketingCenterRefundDataImportTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(TelMarketingCenterRefundDataImportTask.class);

    @Autowired
    private TelMarketingCenterRefundDataImportService telMarketingCenterRefundDataImportService;

    @Override
    protected void doProcess() throws Exception {
        //申请退款数据
        telMarketingCenterRefundDataImportService.importRefundOrderData();
    }
}
