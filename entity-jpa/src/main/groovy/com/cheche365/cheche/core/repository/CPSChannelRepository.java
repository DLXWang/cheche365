package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.CPSChannel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CPSChannelRepository extends PagingAndSortingRepository<CPSChannel, Long>, JpaSpecificationExecutor<CPSChannel> {
    @Query(value = "select u.* from cps_channel u where end_date >= ?1 or datediff(?1, u.end_date) <= 7 order by id desc", nativeQuery = true)
    List<CPSChannel> findValidCPSChannelByEndDate(Date endDate);

    @Query(value = "select avg(rebate), max(rebate), min(rebate) from cps_channel", nativeQuery = true)
    List findAvgAndMaxAndMinRebate();

    CPSChannel findFirstByChannelNo(String channelNo);
}
