package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterUnPayOrderDataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 未支付订单用户导入电销定时任务
 * 9-17时5分执行，每小时一次
 * Created by wangshaobin on 2016/12/21.
 */
@Service
public class TelMarketingCenterUnPayOrderDataImportTask extends BaseTask {
    @Autowired
    private TelMarketingCenterUnPayOrderDataImportService telMarketingCenterUnPayOrderDataImportService;
    @Override
    protected void doProcess() throws Exception {
        telMarketingCenterUnPayOrderDataImportService.importUnPayOrderData();
    }
}
