package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.OrderTransmissionStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTransmissionStatusRepository extends PagingAndSortingRepository<OrderTransmissionStatus, Long> {
    OrderTransmissionStatus findFirstByStatus(String status);
}
