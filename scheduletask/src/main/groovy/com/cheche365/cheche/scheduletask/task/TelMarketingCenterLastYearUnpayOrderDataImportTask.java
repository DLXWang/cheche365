package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterLastYearUnpayOrderDataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wangshaobin on 2016/8/12.
 */
@Service
public class TelMarketingCenterLastYearUnpayOrderDataImportTask extends BaseTask {
    @Autowired
    private TelMarketingCenterLastYearUnpayOrderDataImportService telMarketingCenterLastYearUnpayOrderDataImportService;

    @Override
    protected void doProcess() throws Exception {
        //导入上年未成单订单数据
        telMarketingCenterLastYearUnpayOrderDataImportService.importLastYearUnpayOrderData();
    }
}
