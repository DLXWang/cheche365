package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterQuoteDataImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelMarketingCenterQuoteDataImportTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(TelMarketingCenterQuoteDataImportTask.class);

    @Autowired
    private TelMarketingCenterQuoteDataImportService telMarketingCenterQuoteDataImportService;

    @Override
    protected void doProcess() throws Exception {
        //日志中的报价数据
        telMarketingCenterQuoteDataImportService.importQuoteData();
    }
}
