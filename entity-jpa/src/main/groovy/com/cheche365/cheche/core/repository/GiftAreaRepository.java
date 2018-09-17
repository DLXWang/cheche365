package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.GiftArea;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mahong on 2016/2/16.
 */
@Repository
public interface GiftAreaRepository extends PagingAndSortingRepository<GiftArea, Long> {

    @Query(value = "SELECT * FROM gift_area where source_type = 2 and source = ?1 ORDER BY area" ,nativeQuery = true)
    List<GiftArea> findGiftAreaByMarketingId(Long id);
}
