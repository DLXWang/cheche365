package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.OrderSourceType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderSourceTypeRepository extends PagingAndSortingRepository<OrderSourceType, Long> {
    OrderSourceType findFirstByName(String name);
}
