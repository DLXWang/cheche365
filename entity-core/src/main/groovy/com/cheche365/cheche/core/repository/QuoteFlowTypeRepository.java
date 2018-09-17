package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.QuoteFlowType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mahong on 2015/10/26.
 */
@Repository
public interface QuoteFlowTypeRepository extends PagingAndSortingRepository<QuoteFlowType, Long> {
    QuoteFlowType findFirstByName(String name);
}
