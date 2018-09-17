package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.OrderSubStatus
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Created by Administrator on 2017/12/20.
 */
@Repository
interface OrderSubStatusRepository extends PagingAndSortingRepository<OrderSubStatus, Long> {
}
