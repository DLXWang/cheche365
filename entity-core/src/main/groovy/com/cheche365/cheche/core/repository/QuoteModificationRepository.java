package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.OcQuoteSource;
import com.cheche365.cheche.core.model.QuoteModification;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wangfei on 2016/5/5.
 */
@Repository
public interface QuoteModificationRepository extends PagingAndSortingRepository<QuoteModification, Long> {

    QuoteModification findFirstByQuoteSourceAndQuoteSourceId(OcQuoteSource quoteSource, Long quoteSourceId);
}
