package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.CooperationMode;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/8/25.
 */
@Repository
public interface BusinessActivityRepository extends PagingAndSortingRepository<BusinessActivity, Long> , JpaSpecificationExecutor<BusinessActivity> {
    @Query("from BusinessActivity ba where ba.refreshFlag = 1 or (ba.refreshFlag = 0 and (ba.refreshTime is null or ba.refreshTime <= ba.endTime))")
    List<BusinessActivity> ListEnableRefreshData();

    List<BusinessActivity> findByCooperationMode(CooperationMode cooperationMode);

    @Query("select id from BusinessActivity where cooperationMode = ?1")
    List<Long> findIdByCooperationMode(CooperationMode cooperationMode);

    BusinessActivity findFirstByCode(String code);

    BusinessActivity findFirstByLandingPage(String landingPage);

    @Query("select count(ba.id) from BusinessActivity ba where lower(ba.code) = ?1")
    Integer countByCode(String code);

    @Query(value = "select u.* from business_activity u where u.frequency <> 3 and u.email is not null and u.start_time <= ?1 and (u.end_time >= ?1 or datediff(?1, u.end_time) <= 7) order by u.id desc", nativeQuery = true)
    List<BusinessActivity> findSendEmailData(Date endTime);

    @Query(value = "select avg(rebate), max(rebate), min(rebate) from business_activity where cooperation_mode = ?1", nativeQuery = true)
    List findAvgAndMaxAndMinRebate(Long cpsCooperationMode);
}
