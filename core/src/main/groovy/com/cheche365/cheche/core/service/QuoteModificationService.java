package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.InsurancePackage;
import com.cheche365.cheche.core.model.OcQuoteSource;
import com.cheche365.cheche.core.model.QuoteModification;
import com.cheche365.cheche.core.repository.QuoteModificationRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by wangfei on 2016/5/5.
 */
@Service
public class QuoteModificationService {
    private Logger logger = LoggerFactory.getLogger(QuoteModificationService.class);

    @Autowired
    private QuoteModificationRepository quoteModificationRepository;

    public QuoteModification getByQuoteSourceAndQuoteSourceId(OcQuoteSource quoteSource, Long quoteSourceId) {
        return quoteModificationRepository.findFirstByQuoteSourceAndQuoteSourceId(quoteSource, quoteSourceId);
    }

    public List<String> getCompanyIdsAsList(QuoteModification quoteModification) {
        String strCompany = quoteModification.getInsuranceCompanyIds();
        if (StringUtils.isBlank(strCompany)) {
            return null;
        }
        return Arrays.asList(strCompany.split(","));
    }

    public QuoteModification createNew(OcQuoteSource quoteSource, Long quoteSourceId, InsurancePackage insurancePackage,
                                       InsuranceCompany insuranceCompany) {
        QuoteModification quoteModification = new QuoteModification();
        quoteModification.setQuoteSource(quoteSource);
        quoteModification.setQuoteSourceId(quoteSourceId);
        quoteModification.setInsurancePackage(insurancePackage);
        quoteModification.setInsuranceCompanyIds(insuranceCompany.getId().toString());
        quoteModification.setCreateTime(new Date());
        quoteModification.setUpdateTime(new Date());
        return quoteModification;
    }



}
