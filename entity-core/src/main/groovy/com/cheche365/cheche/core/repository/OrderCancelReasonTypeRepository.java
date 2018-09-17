package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.OrderCancelReasonType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by zhaozhong on 2015/9/2.
 */
@Repository
public interface OrderCancelReasonTypeRepository extends PagingAndSortingRepository<OrderCancelReasonType, Long>{
    OrderCancelReasonType findFirstByReason(String reason);

    //List<OrderCancelReasonType> findAllOrderByOrder();
}
