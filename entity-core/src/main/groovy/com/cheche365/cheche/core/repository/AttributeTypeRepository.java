package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.AttributeType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeTypeRepository extends PagingAndSortingRepository<AttributeType, Long> {
}
