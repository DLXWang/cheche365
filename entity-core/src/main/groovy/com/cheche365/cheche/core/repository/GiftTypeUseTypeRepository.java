package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.GiftTypeUseType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by gaochengchun on 2015/7/29.
 */
@Repository
public interface GiftTypeUseTypeRepository extends PagingAndSortingRepository<GiftTypeUseType, Long> {
    GiftTypeUseType findFirstByName(String name);
}
