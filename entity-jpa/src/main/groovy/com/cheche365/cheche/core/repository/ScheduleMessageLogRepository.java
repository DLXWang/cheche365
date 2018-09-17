package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ScheduleMessageLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lyh on 2015/10/14.
 */
@Repository
public interface ScheduleMessageLogRepository extends PagingAndSortingRepository<ScheduleMessageLog, Long> , JpaSpecificationExecutor<ScheduleMessageLog> {
}
