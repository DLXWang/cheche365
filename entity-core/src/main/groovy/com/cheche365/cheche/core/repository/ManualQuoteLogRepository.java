package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ManualQuoteLog;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wangshaobin on 2016/9/12.
 */
@Repository
public interface ManualQuoteLogRepository extends PagingAndSortingRepository<ManualQuoteLog, Long> {
}
