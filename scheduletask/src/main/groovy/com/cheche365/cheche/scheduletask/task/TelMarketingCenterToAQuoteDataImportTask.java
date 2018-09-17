package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterToAQuoteDataImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelMarketingCenterToAQuoteDataImportTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(TelMarketingCenterToAQuoteDataImportTask.class);

    @Autowired
    private TelMarketingCenterToAQuoteDataImportService telMarketingCenterToAQuoteDataImportService;

    @Override
    protected void doProcess() throws Exception {
        //日志中的 (ToA) 报价数据
        telMarketingCenterToAQuoteDataImportService.importQuoteData();
    }
}
