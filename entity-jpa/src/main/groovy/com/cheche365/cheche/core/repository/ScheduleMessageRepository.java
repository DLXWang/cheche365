package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.model.ScheduleMessage;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lyh on 2015/10/13.
 */
@Repository
public interface ScheduleMessageRepository extends PagingAndSortingRepository<ScheduleMessage, Long> , JpaSpecificationExecutor<ScheduleMessage> {
    ScheduleMessage findFirstByScheduleConditionAndDisableOrderByUpdateTimeDesc(ScheduleCondition condition, boolean disable);
}
