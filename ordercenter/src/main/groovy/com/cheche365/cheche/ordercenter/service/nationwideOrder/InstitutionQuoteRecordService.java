package com.cheche365.cheche.ordercenter.service.nationwideOrder;

import com.cheche365.cheche.core.model.InstitutionQuoteRecord;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.InstitutionQuoteRecordRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by wangfei on 2015/11/17.
 */
@Service
@Transactional
public class InstitutionQuoteRecordService {
    private Logger logger = LoggerFactory.getLogger(InstitutionQuoteRecordService.class);

    @Autowired
    private InstitutionQuoteRecordRepository institutionQuoteRecordRepository;

    public InstitutionQuoteRecord getById(Long id) {
        return institutionQuoteRecordRepository.findOne(id);
    }

    public InstitutionQuoteRecord getByPurchaseOrder(PurchaseOrder purchaseOrder) {
        return institutionQuoteRecordRepository.findFirstByPurchaseOrder(purchaseOrder);
    }

    public InstitutionQuoteRecord updateInsuranceInfo(String commercialPolicyNo, String compulsoryPolicyNo, PurchaseOrder purchaseOrder) {
        InstitutionQuoteRecord institutionQuoteRecord = institutionQuoteRecordRepository.findFirstByPurchaseOrder(purchaseOrder);
        institutionQuoteRecord.setCommercialPolicyNo(StringUtils.trimToEmpty(commercialPolicyNo));
        institutionQuoteRecord.setCompulsoryPolicyNo(StringUtils.trimToEmpty(compulsoryPolicyNo));
        institutionQuoteRecord.setUpdateTime(new Date());
        return institutionQuoteRecordRepository.save(institutionQuoteRecord);
    }

    public InstitutionQuoteRecord save(InstitutionQuoteRecord institutionQuoteRecord){
        return institutionQuoteRecordRepository.save(institutionQuoteRecord);
    }

}
