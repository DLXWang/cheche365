package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.OrderStatus
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderStatusRepository extends PagingAndSortingRepository<OrderStatus, Long> {
    OrderStatus findFirstByStatus(String status)
}
