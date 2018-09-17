package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.LogType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by zhengwei on 5/5/15.
 */
@Repository
public interface LogTypeRepository extends PagingAndSortingRepository<LogType, Long> {

    LogType findFirstByName(String name);
}
