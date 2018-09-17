package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PaymentType;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface PaymentTypeRepository  extends PagingAndSortingRepository<PaymentType,Long> {
}
