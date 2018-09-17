package com.cheche365.cheche.scheduletask.integration.handle;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterQuotePhotoDataImportService;
import com.cheche365.cheche.web.integration.IIntegrationHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuotePhotoHandler implements IIntegrationHandler {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TelMarketingCenterQuotePhotoDataImportService TelMarketingCenterQuotePhotoDataImportService;

    @Override
    public Object handle(Object message) {
        logger.info("--- 拍照报价数据实时进电销start ---");
        TelMarketingCenterQuotePhotoDataImportService.importQuotePhotoData();
        logger.info("--- 拍照报价数据实时进电销end ---");
        return null;
    }
}
