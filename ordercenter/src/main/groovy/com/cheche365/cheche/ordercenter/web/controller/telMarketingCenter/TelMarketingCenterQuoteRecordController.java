package com.cheche365.cheche.ordercenter.web.controller.telMarketingCenter;

import com.cheche365.cheche.ordercenter.service.telMarketingCenter.TelMarketingCenterQuoteService;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.QuoteHistoryDetailJsonObject;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/orderCenter/telMarketingCenter/quote")
public class TelMarketingCenterQuoteRecordController {

    @Autowired
    private TelMarketingCenterQuoteService telMarketingCenterQuoteService;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<QuoteHistoryDetailJsonObject> list(@RequestParam String repeatId) {
        Long telRepeatId = NumberUtils.toLong(repeatId);

        List<QuoteHistoryDetailJsonObject> dataList = telMarketingCenterQuoteService.getQuoteList(telRepeatId);

        return dataList;
    }

}
