package com.cheche365.cheche.manage.common.repository;
import com.cheche365.cheche.manage.common.model.QuoteFlowConfigOperateLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yellow on 2017/7/6.
 */
@Repository
public interface QuoteFlowConfigOperateLogRepository extends PagingAndSortingRepository<QuoteFlowConfigOperateLog, Long>, JpaSpecificationExecutor<QuoteFlowConfigOperateLog> {

    @Query(value = "SELECT * FROM quote_flow_config_operate_log where quote_flow_config=?1", nativeQuery = true)
    List<QuoteFlowConfigOperateLog> findByQuoteFlowConfig(Long quoteFlowConfig);
    @Query(value = " select * from quote_flow_config_operate_log where id in(  SELECT max(id) AS id  FROM quote_flow_config_operate_log " +
        " WHERE  TO_DAYS(execution_time) = TO_DAYS(now()) AND operation_type in(0,1) " +
        "GROUP BY operation_type,quote_flow_config " +
        "ORDER BY quote_flow_config )", nativeQuery = true)
    List<QuoteFlowConfigOperateLog> findLogCurrenDay();
}
