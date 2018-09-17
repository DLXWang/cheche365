package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.InternalUserLoginLog;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xu.yelong on 2016-05-16.
 */
@Repository
public interface InternalUserLoginLogRepository extends PagingAndSortingRepository<InternalUserLoginLog, Long> {

}
