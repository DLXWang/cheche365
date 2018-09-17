package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.OrderType
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderTypeRepository extends PagingAndSortingRepository<OrderType, Long> {
    OrderType findFirstByName(String name)
}
