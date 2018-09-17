package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterMarketingDataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 营销活动汇总导入电销定时任务
 * Created by wangshaobin on 2016/12/21.
 */
@Service
public class TelMarketingCenterMarketingDataImportTask extends BaseTask {
    @Autowired
    private TelMarketingCenterMarketingDataImportService telMarketingCenterMarketingDataImportService;
    @Override
    protected void doProcess() throws Exception {
        telMarketingCenterMarketingDataImportService.importMarketingData();
    }
}
