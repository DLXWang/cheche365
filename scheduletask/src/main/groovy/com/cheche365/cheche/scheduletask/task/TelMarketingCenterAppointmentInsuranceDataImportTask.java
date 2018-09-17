package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterAppointmentInsuranceDataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 主动预约数据导入电销定时任务
 * Created by wangshaobin on 2016/12/21.
 */
@Service
public class TelMarketingCenterAppointmentInsuranceDataImportTask extends BaseTask {
    @Autowired
    private TelMarketingCenterAppointmentInsuranceDataImportService telMarketingCenterAppointmentInsuranceDataImportService;
    @Override
    protected void doProcess() throws Exception {
        telMarketingCenterAppointmentInsuranceDataImportService.importAppointmentInsuranceData();
    }
}
