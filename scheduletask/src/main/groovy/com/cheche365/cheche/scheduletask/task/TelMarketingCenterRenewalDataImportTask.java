package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterRenewalDataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 续保数据导入电销定时任务
 * Created by chenxy on 2018/05/11.
 */
@Service
public class TelMarketingCenterRenewalDataImportTask extends BaseTask {
    @Autowired
    private TelMarketingCenterRenewalDataImportService importService;
    @Override
    protected void doProcess() throws Exception {
        importService.renewalIntoOrdercenter();
        importService.renewalIntoOrdercenterTwice();
    }
}
