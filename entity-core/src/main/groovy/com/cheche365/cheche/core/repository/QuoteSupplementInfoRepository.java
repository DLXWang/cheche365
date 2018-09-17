package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.QuoteSupplementInfo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mahong on 2015/12/21.
 */
@Repository
public interface QuoteSupplementInfoRepository extends PagingAndSortingRepository<QuoteSupplementInfo, Long> {
    List<QuoteSupplementInfo> findByQuoteRecord(QuoteRecord quoteRecord);
}
