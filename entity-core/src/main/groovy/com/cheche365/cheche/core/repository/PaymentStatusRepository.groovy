package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.PaymentStatus
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentStatusRepository extends PagingAndSortingRepository<PaymentStatus, Long> {
    PaymentStatus findFirstByStatus(String name)
}
