package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by sunhuazhong on 2016/3/16.
 */
@Repository
public interface QuoteRecordCacheRepository extends PagingAndSortingRepository<QuoteRecordCache, Long> {


    QuoteRecordCache findFirstByInsuranceCompanyAndTypeAndQuoteModificationOrderByCreateTimeDesc(InsuranceCompany insuranceCompany, int type, QuoteModification quoteModification);

    QuoteRecordCache findFirstByQuoteRecord(QuoteRecord quoteRecord);

    @Query(value = "select * from quote_record_cache qc where type = 1 and quote_record is not null " +
        "and exists (select 1 from `perfect_driver` pd where pd.channel = ?1 and pd.user = ?2 and qc.perfect_driver = pd.id) " +
        "and exists (select 1 from perfect_driver_process ps where ps.perfect_driver = qc.perfect_driver and  ps.status = 2) ", nativeQuery = true)
    List<QuoteRecordCache> findQuoteRecordCacheNeedQuote(Channel channel, User currentUser);

    @Query(value = "select * from quote_record_cache qc where type = 2 and quote_record is not null " +
        "and exists (select 1 from `perfect_driver` pd where pd.channel = ?1 and pd.user = ?2 and qc.perfect_driver = pd.id) " +
        "and exists (select 1 from perfect_driver_process ps where ps.perfect_driver = qc.perfect_driver and  ps.status = 3) " +
        "and qc.create_time  >= (NOW() - INTERVAL 2 HOUR) ", nativeQuery = true)
    List<QuoteRecordCache> findQuoteRecordCacheQuoted(Channel channel, User currentUser);

    @Query(value = "select * from quote_record_cache qc where type = 2 and quote_record is not null " +
        "and exists (select 1 from `perfect_driver` pd where pd.channel = ?1 and pd.user = ?2 and qc.perfect_driver = pd.id) " +
        "and exists (select 1 from perfect_driver_process ps where ps.perfect_driver = qc.perfect_driver and  ps.status = 3)" +
        "and qc.create_time  < (NOW() - INTERVAL 2 HOUR) ", nativeQuery = true)
    List<QuoteRecordCache> findQuoteRecordCacheQuotedExpired(Channel channel, User currentUser);

    @Query(value = "select * from quote_record_cache qc where type = 2 and quote_record is not null " +
        "and exists (select 1 from `perfect_driver` pd where pd.channel = ?1 and pd.user = ?2 and qc.perfect_driver = pd.id) " +
        "and exists (select 1 from perfect_driver_process ps where ps.perfect_driver = qc.perfect_driver and  ps.status = 4) ", nativeQuery = true)
    List<QuoteRecordCache> findQuoteRecordCacheOrderCommitted(Channel channel, User currentUser);

}
