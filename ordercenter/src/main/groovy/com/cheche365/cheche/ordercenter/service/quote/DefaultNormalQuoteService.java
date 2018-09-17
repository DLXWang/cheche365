package com.cheche365.cheche.ordercenter.service.quote;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository;
import com.cheche365.cheche.core.service.QuoteModificationService;
import com.cheche365.cheche.core.service.QuoteRecordCacheService;
import com.cheche365.cheche.core.service.QuoteRecordService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.ordercenter.model.QuoteQuery;
import com.cheche365.cheche.ordercenter.serializer.QuoteRecordViewExtendSerializer;
import com.cheche365.cheche.ordercenter.service.perfectdriver.QuoteRecordCacheExpandService;
import com.cheche365.cheche.ordercenter.web.controller.perfectdriver.QuoteRecordCacheController;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wangfei on 2016/5/5.
 */
public class DefaultNormalQuoteService extends DefaultQuoteService {
    private Logger logger = LoggerFactory.getLogger(DefaultNormalQuoteService.class);

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    private QuoteRecordCacheExpandService quoteRecordCacheExpandService;

    @Autowired
    private QuoteModificationService quoteModificationService;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private QuoteProcessorFactory quoteProcessorFactory;

    @Autowired
    private QuoteRecordCacheService quoteRecordCacheService;

    @Override
    public String quote(CloseableHttpClient httpClient, Long companyId, QuoteQuery quoteQuery, String userAgent,String uniqueId) {
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findOne(companyId);
        if (null != quoteQuery.getInsurancePackage()) {
            quoteQuery.getInsurancePackage().calculateUniqueString();
        }
        OcQuoteSource quoteSource = QuoteSource.formatVal(quoteQuery.getSource());

        QuoteModification quoteModification = quoteModificationService.getByQuoteSourceAndQuoteSourceId(quoteSource, quoteQuery.getSourceId());
        //没有预选套餐或是发生变更需要调用接口算费
        if (null == quoteModification || null == quoteModification.getInsurancePackage()
            || !quoteQuery.getInsurancePackage().getUniqueString().equals(quoteModification.getInsurancePackage().getUniqueString())) {
            logger.debug("数据来源[{}]来源ID[{}]在保险公司[{}]无quote_modification记录或险种套餐发生变化，调用web接口重新获取报价", quoteSource.getDescription(),
                quoteQuery.getSourceId(), insuranceCompany.getName());
            return super.quote(httpClient, companyId, quoteQuery, userAgent,uniqueId);
        }

        QuoteRecordCache quoteRecordCache = quoteRecordCacheExpandService.getQuoteRecordCache(companyId, QuoteRecordCacheController.quoteRecordCacheType_1,
            quoteQuery.getSource(), quoteQuery.getSourceId());
        //存在缓存报价
        if (null != quoteRecordCache && quoteRecordCache.getQuoteRecord().getAuto().getLicensePlateNo().equals(quoteQuery.getAuto().getLicensePlateNo())) {
            QuoteRecord quoteRecord=quoteRecordCache.getQuoteRecord();
            String strQuoteJson = CacheUtil.doJacksonSerialize(quoteRecord, new QuoteRecordViewExtendSerializer());
            logger.debug("数据来源[{}]来源ID[{}]在保险公司[{}]存在quote_recode_cache记录，直接返回缓存报价: {}", quoteSource.getDescription(), quoteQuery.getSourceId(),
                insuranceCompany.getName(), strQuoteJson);
            return "{\"code\":200,\"debugMessage\":null,\"data\":" + strQuoteJson + "}";
        } else {
            logger.debug("数据来源[{}]来源ID[{}]在保险公司[{}]不存在quote_recode_cache记录，调用web接口获取报价", quoteSource.getDescription(), quoteQuery.getSourceId(),
                insuranceCompany.getName());
            return super.quote(httpClient, companyId, quoteQuery, userAgent,uniqueId);
        }
    }

    @Override
    public QuoteRecordCache getQuoteRecordCache(String quoteSource, Long quoteSourceId, Long companyId, Integer type) {
        return quoteRecordCacheExpandService.getQuoteRecordCache(companyId, type, quoteSource, quoteSourceId);
    }

    @Override
    public void sendQuoteMsg(String source, Long sourceId, Long companyId, QuoteRecord quoteRecord) {
        QuoteRecordCache quoteRecordCache = this.getQuoteRecordCache(source, sourceId, companyId, QuoteRecordCacheController.quoteRecordCacheType_3);
        if (null != quoteRecordCache) {
            logger.debug("数据来源[{}]来源ID[{}]在保险公司[{}]存在quote_recode_cache记录，使用缓存报价发送短信", source, sourceId, companyId);
            super.sendQuoteMsg(source, sourceId, companyId, quoteRecordCache.getQuoteRecord());
        } else {
            logger.debug("数据来源[{}]来源ID[{}]在保险公司[{}]不存在quote_recode_cache记录，使用默认报价发送短信", source, sourceId, companyId);
            super.sendQuoteMsg(source, sourceId, companyId, quoteRecord);
        }
    }

    @Override
    public String saveQuote(String source, Long sourceId, Long companyId, CloseableHttpClient client, QuoteRecord quoteRecord, String userAgent,String uniqueId) {
        QuoteRecordCache quoteRecordCacheOld = this.getQuoteRecordCache(source, sourceId, companyId, QuoteRecordCacheController.quoteRecordCacheType_3);
        if (null != quoteRecordCacheOld && quoteRecordCacheOld.getQuoteRecord().getAuto().getLicensePlateNo().equals(quoteRecord.getAuto().getLicensePlateNo())) {
            QuoteRecord quoteRecordNew = quoteRecordService.reGenerateQuoteRecord(quoteRecordCacheOld.getQuoteRecord());
            logger.debug("数据来源[{}]来源ID[{}]保险公司[{}]为旧quote_record->{} 生成新quote_record->{}", source, sourceId, companyId,
                quoteRecordCacheOld.getQuoteRecord().getId(), quoteRecordNew.getId());
            //过户车补充信息
            if(!"quoteRecord".equals(source) && !"renewInsurance".equals(source))
                saveQuoteSupplementInfo(quoteProcessorFactory.getProcessor(source).getSupplementInfo(sourceId), quoteRecordNew);
            return "{\"code\":200,\"message\":null,\"debugMessage\":null,\"data\":{\"quoteRecordId\":"
                + quoteRecordNew.getId() + ",\"totalPremium\":" + quoteRecordNew.getTotalPremium()
                + ",\"skipInsure\":1}}";
        } else {
            //比较完成之后将auto从报价移除，否则保存报价会对车辆信息做校验
            quoteRecord.setAuto(null);
            logger.debug("数据来源[{}]来源ID[{}]在保险公司[{}]不存在quote_recode_cache记录，提交默认报价", source, sourceId, companyId);
            return super.saveQuote(source, sourceId, companyId, client, quoteRecord, userAgent,uniqueId);
        }
    }
}
