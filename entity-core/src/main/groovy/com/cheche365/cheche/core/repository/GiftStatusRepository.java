package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.GiftStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftStatusRepository extends PagingAndSortingRepository<GiftStatus, Long> {
    GiftStatus findFitstByStatus(String status);
}
