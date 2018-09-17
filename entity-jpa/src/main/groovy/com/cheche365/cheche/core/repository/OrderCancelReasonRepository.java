package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.OrderCancelReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by zhaozhong on 2015/9/2.
 */
@Repository
public interface OrderCancelReasonRepository extends JpaRepository<OrderCancelReason, Long> {

}
