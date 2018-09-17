package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterExpireTimeDataImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商业险、交强险到期日导入电销定时任务
 * Created by wangshaobin on 2016/12/21.
 */
@Service
public class TelMarketingCenterExpireTimeDataImportTask extends BaseTask {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterExpireTimeDataImportTask.class);
    @Autowired
    private TelMarketingCenterExpireTimeDataImportService telMarketingCenterExpireTimeDataImportService;

    @Override
    protected void doProcess() throws Exception {
        //商业险、交强险到期日在7天、8-90天的
        importExpireTimeData();
    }

    /**
     * 导入商业险、交强险在一周、8-90天到期的数据到电销表.
     */
    private void importExpireTimeData() {
        //90天内数据导入
        telMarketingCenterExpireTimeDataImportService.importInsuranceDataByDate(TaskConstants.COMMERCIAL_INSURANCE_EXPIRE_DATE);
        telMarketingCenterExpireTimeDataImportService.importInsuranceDataByDate(TaskConstants.COMPULSORY_INSURANCE_EXPIRE_DATE);
    }
}
