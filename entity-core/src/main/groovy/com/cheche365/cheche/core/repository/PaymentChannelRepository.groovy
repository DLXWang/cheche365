package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.PaymentChannel
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentChannelRepository extends PagingAndSortingRepository<PaymentChannel, Long> {

    List<PaymentChannel> findByCustomerPay(boolean customerPay);
}
