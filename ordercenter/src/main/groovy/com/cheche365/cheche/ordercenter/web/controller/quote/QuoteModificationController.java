package com.cheche365.cheche.ordercenter.web.controller.quote;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.QuoteModificationService;
import com.cheche365.cheche.ordercenter.service.quote.DefaultQuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangfei on 2016/5/5.
 */
@RestController
@RequestMapping(value = "/orderCenter/quoteModifications")
public class QuoteModificationController {
    private Logger logger = LoggerFactory.getLogger(QuoteModificationController.class);

    @Autowired
    private QuoteModificationService quoteModificationService;

    @RequestMapping(value = "/{quoteSource}/{quoteSourceId}",method = RequestMethod.GET)
    public Map<String, Object> getQuoteModification(@PathVariable String quoteSource,@PathVariable Long quoteSourceId) {
        Map<String, Object> objectMap = new HashMap<>();
        OcQuoteSource source = DefaultQuoteService.QuoteSource.formatVal(quoteSource);
        objectMap.put("quoteModification", quoteModificationService.getByQuoteSourceAndQuoteSourceId(source, quoteSourceId));
        return objectMap;
    }

    @RequestMapping(value = "/insurancePackage/compare",method = RequestMethod.POST)
    public Map<String, Boolean> isSampleInsurancePackage(@RequestBody QuoteRecordCache quoteRecordCache) {
        Map<String, Boolean> booleanMap = new HashMap<>();
        InsurancePackage insurancePackage = quoteRecordCache.getQuoteModification().getInsurancePackage();
        insurancePackage.calculateUniqueString();

        boolean result = true;

        OcQuoteSource source = DefaultQuoteService.QuoteSource.formatVal(quoteRecordCache.getStrQuoteSource());
        QuoteModification quoteModificationDb = quoteModificationService.getByQuoteSourceAndQuoteSourceId(source,
            quoteRecordCache.getQuoteModification().getQuoteSourceId());
        if (null != quoteModificationDb) {
            //套餐发生变化给出提示，没有预选套餐直接跳过
            if (null != quoteModificationDb.getInsurancePackage()
                && !insurancePackage.getUniqueString().equals(quoteModificationDb.getInsurancePackage().getUniqueString())) {
                result = false;
            }
        }

        booleanMap.put("compareResult", result);
        return booleanMap;
    }



}
