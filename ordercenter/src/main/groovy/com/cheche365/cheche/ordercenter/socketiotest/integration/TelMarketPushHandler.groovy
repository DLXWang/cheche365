
package com.cheche365.cheche.ordercenter.socketiotest.integration

import com.cheche365.cheche.ordercenter.socketiotest.PushSocketioService
import com.cheche365.cheche.web.integration.IIntegrationHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class TelMarketPushHandler implements IIntegrationHandler {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PushSocketioService pushSocketioService;

    @Override
    def handle(Object message) {
//        logger.info("--- 数据实时推送电销start ---");
//        pushSocketioService.pushArr();
//        logger.info("--- 数据实时推送电销end ---");
//        return null
    }
}

