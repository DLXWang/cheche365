package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.QuoteSupplementInfo;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.repository.QuoteSupplementInfoRepository;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * Created by wangfei on 2016/4/13.
 */
@Service
public class QuoteSupplementInfoService {

    private Logger logger = LoggerFactory.getLogger(QuoteSupplementInfoService.class);

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private QuoteSupplementInfoRepository supplementInfoRepository;

    public Iterable<QuoteSupplementInfo> save(List<QuoteSupplementInfo> quoteSupplementInfo) {
        return supplementInfoRepository.save(quoteSupplementInfo);
    }

    public List<QuoteSupplementInfo> getSupplementInfosByPurchaseOrder(PurchaseOrder purchaseOrder) {
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        return supplementInfoRepository.findByQuoteRecord(quoteRecord);
    }

    public List<Map> findByQuoteRecordForThirdPartner(Long quoteRecordId) {
        String sql = " SELECT DISTINCT field_path name , field_label displayName, " +
            "(CASE WHEN value_name IS NOT NULL THEN value_name ELSE value END) value FROM quote_supplement_info where quote_record = " + quoteRecordId;
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.getResultList();
    }

}
