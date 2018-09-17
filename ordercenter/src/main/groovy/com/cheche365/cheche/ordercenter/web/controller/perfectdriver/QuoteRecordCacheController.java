package com.cheche365.cheche.ordercenter.web.controller.perfectdriver;

import com.cheche365.cheche.core.model.InsurancePackage;
import com.cheche365.cheche.core.model.QuoteRecordCache;
import com.cheche365.cheche.core.serializer.ArrayQRSerializer;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.ordercenter.service.perfectdriver.QuoteRecordCacheExpandService;
import com.cheche365.cheche.ordercenter.service.quote.QuoteProcessorFactory;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangfei on 2016/3/21.
 */
@RestController
@RequestMapping("/orderCenter/quoteRecordCaches")
public class QuoteRecordCacheController {
    private Logger logger = LoggerFactory.getLogger(QuoteRecordCacheController.class);

    @Autowired
    private QuoteRecordCacheExpandService quoteRecordCacheExpandService;

    @Autowired
    private QuoteProcessorFactory quoteProcessorFactory;

    public static final int quoteRecordCacheType_1 = 1;// 报价类型，1-电销报价
    public static final int quoteRecordCacheType_2 = 2;// 报价类型，2-传统报价
    public static final int quoteRecordCacheType_3 = 3;// 报价类型，2-修改报价

    @RequestMapping(value = "/default",method = RequestMethod.POST)
    public void saveDefaultQuoteRecordCache(@RequestBody QuoteRecordCache[] quoteRecordCaches) {
        for (QuoteRecordCache quoteRecordCache : quoteRecordCaches) {
            AssertUtil.notNull(quoteRecordCache.getQuoteModification().getQuoteSourceId(), "缺少来源数据ID参数，无法缓存报价");

            if (quoteRecordCacheType_1 != quoteRecordCache.getType() && quoteRecordCacheType_3 != quoteRecordCache.getType()) {
                throw new RuntimeException("无法缓存报价类型为" + quoteRecordCache.getType() + "的报价");
            }
        }

        quoteRecordCacheExpandService.saveOrUpdateModificationQuoteRecordCache(quoteRecordCaches);
    }

    @RequestMapping(value = "/{quoteSourceId}/{companyId}",method = RequestMethod.GET)
    public Map<String, String> getRecordCache(@PathVariable Long quoteSourceId,@PathVariable Long companyId, Integer type,
                                              String quoteSource,String licensePlateNo) {
        Map<String, String> objectMap = new HashMap<>();

        QuoteRecordCache quoteRecordCache = quoteProcessorFactory.getProcessor(quoteSource).getQuoteRecordCache(quoteSource, quoteSourceId,
            companyId, type);
        if (null != quoteRecordCache && quoteRecordCache.getQuoteRecord().getAuto().getLicensePlateNo().equals(licensePlateNo)) {
            String strQuoteJson = CacheUtil.doJacksonSerialize(quoteRecordCache.getQuoteRecord(), new ArrayQRSerializer());
            logger.debug("获取数据来源ID[{}]数据来源[{}]在保险公司[{}]类型为[{}]的缓存报价: {}", quoteSourceId, quoteSource, companyId, type, strQuoteJson);
            objectMap.put("quoteRecord", strQuoteJson);
            objectMap.put("policyDescription", quoteRecordCache.getPolicyDescription());
        }

        return objectMap;
    }

    @RequestMapping(value = "/insurancePackage/compare",method = RequestMethod.POST)
    public Map<String, Boolean> isSampleInsurancePackage(@RequestBody QuoteRecordCache quoteRecordCache) {
        Map<String, Boolean> booleanMap = new HashMap<>();
        InsurancePackage insurancePackage = quoteRecordCache.getQuoteRecord().getInsurancePackage();
        insurancePackage.calculateUniqueString();

        boolean result = true;

        QuoteRecordCache quoteRecordCacheDb = quoteProcessorFactory.getProcessor(quoteRecordCache.getStrQuoteSource())
            .getQuoteRecordCache(quoteRecordCache.getStrQuoteSource(), quoteRecordCache.getQuoteModification().getQuoteSourceId(),
                quoteRecordCache.getInsuranceCompany().getId(), quoteRecordCache.getType());
        if (null != quoteRecordCacheDb) {
            if (!insurancePackage.getUniqueString().equals(quoteRecordCacheDb.getQuoteRecord().getInsurancePackage().getUniqueString())) {
                result = false;
            }
        } else {
            result = false;
        }

        booleanMap.put("compareResult", result);
        return booleanMap;
    }

}
