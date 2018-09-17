package com.cheche365.cheche.scheduletask.integration.handle;

import com.cheche365.cheche.scheduletask.service.task.TelMarketingCenterMarketingDataImportService;
import com.cheche365.cheche.web.integration.IIntegrationHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarketingSuccessHandler implements IIntegrationHandler {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TelMarketingCenterMarketingDataImportService dataImportService;

    //TODO:这个message不是更新的对象么?!
    @Override
    public Object handle(Object message) {
        logger.info("--- 活动数据实时进电销start ---");
        dataImportService.importMarketingData();
        logger.info("--- 活动数据实时进电销end ---");
        return null;
    }
}
