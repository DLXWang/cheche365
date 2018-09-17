package com.cheche365.cheche.marketing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Jason on 04/07/2016.
 */
@Component
public class MarketingServiceFactory {

    @Autowired
    private Map<String, MarketingService> services;

    public MarketingService getService(String code) {
        MarketingService service = this.services.get("service" + code);
        if (null == service)
            service = this.services.get("marketingService");
        return service;
    }

}
