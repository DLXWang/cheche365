package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.AdhocMessage;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lyh on 2015/10/7.
 */
@Repository
public interface AdhocMessageRepository extends PagingAndSortingRepository<AdhocMessage, Long> , JpaSpecificationExecutor<AdhocMessage> {
}
