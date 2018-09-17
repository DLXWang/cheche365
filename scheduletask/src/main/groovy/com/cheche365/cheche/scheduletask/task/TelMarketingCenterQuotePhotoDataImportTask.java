package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterQuotePhotoDataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 拍照报价数据导入电销
 * Created by wangshaobin on 2016/12/21.
 */
@Service
public class TelMarketingCenterQuotePhotoDataImportTask extends BaseTask {
    @Autowired
    private TelMarketingCenterQuotePhotoDataImportService telMarketingCenterQuotePhotoDataImportService;
    @Override
    protected void doProcess() throws Exception {
        //拍照报价
        telMarketingCenterQuotePhotoDataImportService.importQuotePhotoData();
    }
}
