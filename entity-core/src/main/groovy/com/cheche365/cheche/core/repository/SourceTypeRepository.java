package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.SourceType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceTypeRepository extends PagingAndSortingRepository<SourceType, Long> {
    SourceType findFirstByName(String name);
}
