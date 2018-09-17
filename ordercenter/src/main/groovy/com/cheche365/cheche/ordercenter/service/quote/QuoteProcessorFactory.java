package com.cheche365.cheche.ordercenter.service.quote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by wangfei on 2016/5/3.
 */
@Component
public class QuoteProcessorFactory {
    @Autowired
    private DefaultQuotePhoneService defaultQuotePhoneService;

    @Autowired
    private DefaultQuotePhotoService defaultQuotePhotoService;

    public DefaultQuoteService getProcessor(String source) {
        return getProcessor(DefaultQuoteService.QuoteSource.format(source));
    }

    public DefaultQuoteService getProcessor(DefaultQuoteService.QuoteSource quoteSource) {
        switch (quoteSource) {
            case SOURCE_PHOTO:
                return defaultQuotePhotoService;
            case SOURCE_PHONE:
                return defaultQuotePhoneService;
            case SOURCE_ORDER:
                return defaultQuotePhotoService;
            case SOURCE_RECORD:
                return defaultQuotePhotoService;
            case SOURCE_RENEW_INSURANCE:
                return defaultQuotePhotoService;
            default:
                throw new IllegalArgumentException("unknown quote source -> " + quoteSource);
        }
    }
}
