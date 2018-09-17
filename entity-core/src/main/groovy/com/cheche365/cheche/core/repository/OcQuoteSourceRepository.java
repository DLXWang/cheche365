package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.OcQuoteSource;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wangfei on 2016/5/4.
 */
@Repository
public interface OcQuoteSourceRepository extends PagingAndSortingRepository<OcQuoteSource, Long> {
}
