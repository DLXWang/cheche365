package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterHistory;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public interface TelMarketingCenterHistoryRepository extends PagingAndSortingRepository<TelMarketingCenterHistory, Long>, JpaSpecificationExecutor<TelMarketingCenterHistory> {
    List<TelMarketingCenterHistory> findByTelMarketingCenterOrderByCreateTimeDesc(TelMarketingCenter telMarketingCenter);

    @Query(value = "select * from tel_marketing_center_history t where t.tel_marketing_center = ?1 ORDER BY t.create_time DESC LIMIT 1", nativeQuery = true)
    TelMarketingCenterHistory findLastTelMarketingCenterHistory(Long id);

    @Query(value = "select min(tmch.id) from tel_marketing_center_history tmch, tel_marketing_center tmc " +
        "where tmch.tel_marketing_center = tmc.id " +
        "and tmch.create_time >= ?1 and tmch.create_time <= ?2 " +
        "and (case when ?3 = 1 then tmch.type in (1, 4) " +
        "when ?3 = 2 then tmch.type = 2 " +
        "when ?3 = 3 then tmch.type = 3 " +
        "when ?3 = 4 then tmch.type = 4 and tmc.status = ?4 else 1 = 1 end) " +
        "and (case when ?5 is not null then tmch.operator = ?5 else 1 = 1 end) " +
        "group by tmch.tel_marketing_center", nativeQuery = true)
    List<BigInteger> findMinHistoryIdByGroup(Date startTime, Date endTime, Integer type, Long statusId, Long operatorId);

    @Query(value = "select count(distinct tmch.id) " +
        "from tel_marketing_center_history tmch, tel_marketing_center tmc " +
        "where tmch.tel_marketing_center = tmc.id " +
        "and tmch.type between 1 and 4 " +
        "and (case when ISNULL(?1) then 1 = 1 else tmc.mobile = ?1 end) " +
        "and (case when ISNULL(?2) then 1 = 1 else tmch.operator = ?2 end) " +
        "order by tmch.id DESC", nativeQuery = true)
    Long countHistoryDataForMobile(String mobile, Long operator);

    @Query(value = "select distinct tmch.* " +
        "from tel_marketing_center_history tmch, tel_marketing_center tmc " +
        "where tmch.tel_marketing_center = tmc.id " +
        "and tmch.type between 1 and 4 " +
        "and (case when ISNULL(?1) then 1 = 1 else tmc.mobile = ?1 end) " +
        "and (case when ISNULL(?2) then 1 = 1 else tmch.operator = ?2 end) " +
        "order by tmch.id DESC " +
        "limit ?3, ?4", nativeQuery = true)
    List<TelMarketingCenterHistory> findHistoryDataForMobile(String mobile, Long operator, Integer firstResult, Integer maxResult);

    @Query(value = "SELECT r1.tel_marketing_center, iu.name AS operator,r1.deal_result,r1.COMMENT, r1.create_time " +
        " FROM ( SELECT it.* FROM                                                                                  " +
        " 	( SELECT min(tmch.id) AS id, tmch.tel_marketing_center, min(tmch.create_time) AS create_time           " +
        " 		FROM tel_marketing_center_history tmch                                                             " +
        "		WHERE tmch.deal_result  not in ('报价','客服转报价','修改跟进人')                                     " +
        " 		GROUP BY tmch.tel_marketing_center ) r                                                             " +
        " 	JOIN tel_marketing_center_history it ON r.id = it.id                                                   " +
        " 	WHERE r.create_time BETWEEN ?1 AND ?2 ) r1                                                             " +
        " JOIN tel_marketing_center tc ON tc.id = r1.tel_marketing_center                                          " +
        " JOIN internal_user iu ON tc.operator = iu.id                                                             " +
        " ORDER BY r1.tel_marketing_center                                                                         "  , nativeQuery = true)
    List<Object[]> findHistoryDataByCreateTimeBetween(Date yesterdayTimePoint, Date todayTimePoint);

    @Query(value = "SELECT it.* " +
        " FROM ( SELECT min(tmch.id) AS id, tmch.tel_marketing_center, min(tmch.create_time) AS create_time " +
        " FROM  tel_marketing_center_history tmch " +
        " WHERE  tmch.tel_marketing_center in ?1 " +
        " and tmch.deal_result not in ('修改跟进人','客服转报价') and (tmch.`status` not in (?2) or tmch.`status` is null)" +
        " GROUP BY tmch.tel_marketing_center ) r " +
        " JOIN tel_marketing_center_history it ON r.id = it.id	", nativeQuery = true)
    List<TelMarketingCenterHistory> findTelMarketingCenterHistoryByCenterIds(List centerIds, List invalidStatus);

    @Query(value = "select * from tel_marketing_center_history t where t.tel_marketing_center = ?1 and t.`status` in (?2) order by id desc limit 1", nativeQuery = true)
    TelMarketingCenterHistory findByTelMarketingCenterAndStatusOrderByCreateTimeDesc(TelMarketingCenter telMarketingCenter, List<TelMarketingCenterStatus> triggerStatus);
}
