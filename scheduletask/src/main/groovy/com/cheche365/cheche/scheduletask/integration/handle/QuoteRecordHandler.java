package com.cheche365.cheche.scheduletask.integration.handle;

import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterQuoteDataImportService;
import com.cheche365.cheche.web.integration.IIntegrationHandler;
import com.cheche365.cheche.web.model.Message;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuoteRecordHandler implements IIntegrationHandler<Message<MoApplicationLog>> {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TelMarketingCenterQuoteDataImportService dataImportService;

    @Override
    public Message<MoApplicationLog> handle(Message<MoApplicationLog> message) {
        logger.info("--- 报价数据实时进电销start ---");

        //报价数据实时进电销
        dataImportService.realTimeImportQuoteData(message);

        logger.info("--- 报价数据实时进电销end ---");
        return null;
    }
}
