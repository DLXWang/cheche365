package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterRegisterNoOperationDataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 注册但无行为用户数据导入电销定时任务
 * Created by wangshaobin on 2016/12/21.
 */
@Service
public class TelMarketingCenterRegisterNoOperationDataImportTask extends BaseTask {
    @Autowired
    private TelMarketingCenterRegisterNoOperationDataImportService telMarketingCenterRegisterNoOperationDataImportService;
    @Override
    protected void doProcess() throws Exception {
        //注册但无行为用户数据
        telMarketingCenterRegisterNoOperationDataImportService.importRegisterNoOperationData();
    }
}
