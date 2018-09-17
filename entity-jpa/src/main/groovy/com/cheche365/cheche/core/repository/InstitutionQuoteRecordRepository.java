package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.InstitutionQuoteRecord;
import com.cheche365.cheche.core.model.PurchaseOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Repository
public interface InstitutionQuoteRecordRepository extends PagingAndSortingRepository<InstitutionQuoteRecord, Long> {
    InstitutionQuoteRecord findFirstByPurchaseOrder(PurchaseOrder purchaseOrder);

    @Query(value = "select distinct ins.purchase_order from institution_quote_record ins where ins.commercial_policy_no like ?1 or ins.compulsory_policy_no like ?1", nativeQuery = true)
    List<BigInteger> listOrderByPolicyNo(String policyNo);
}
