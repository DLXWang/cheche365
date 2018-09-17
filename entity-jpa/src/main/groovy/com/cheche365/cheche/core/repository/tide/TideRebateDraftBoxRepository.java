package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.tide.TideRebateDraftBox;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TideRebateDraftBoxRepository extends PagingAndSortingRepository<TideRebateDraftBox, Long>, JpaSpecificationExecutor<TideRebateDraftBox> {

    Iterable<TideRebateDraftBox> findAllByStatusAndOperator(Integer status, InternalUser operator);
}
