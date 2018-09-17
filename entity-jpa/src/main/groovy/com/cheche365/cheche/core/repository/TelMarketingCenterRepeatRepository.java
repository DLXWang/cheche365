package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TelMarketingCenterRepeatRepository extends PagingAndSortingRepository<TelMarketingCenterRepeat, Long>, JpaSpecificationExecutor<TelMarketingCenterRepeat> {
    @Query(value = "SELECT count(tmcr.id) FROM tel_marketing_center_repeat tmcr WHERE tmcr.mobile = ?1 AND tmcr.source = ?2 AND DATE_ADD(DATE(tmcr.create_time),INTERVAL ?3 DAY) >= CURRENT_DATE()", nativeQuery = true)
    Integer countByMobileAndCreateTime(String mobile, Long telMarketingCenterSourceId, int delayDays);

    @Query(value = "select tmcr.* from tel_marketing_center_repeat tmcr  WHERE (tmcr.user = ?1 or tmcr.mobile = ?2) AND tmcr.source = ?3 AND tmcr.source_id = ?4 and tmcr.source_table = ?5 order by tmcr.id desc limit 1", nativeQuery = true)
    TelMarketingCenterRepeat countByUserAndMobileAndSourceData(Long userId, String mobile, Long telMarketingCenterSourceId, Long sourceId, String sourceTable);

    @Query(value = "select * from tel_marketing_center_repeat tmcr WHERE (tmcr.user = ?1 or tmcr.mobile = ?2) AND tmcr.source = ?3 and tmcr.channel = ?4 order by tmcr.id desc limit 1 ", nativeQuery = true)
    TelMarketingCenterRepeat findByUserAndMobileAndChannel(Long userId, String mobile, Long telMarketingCenterSourceId, Channel channel);

    List<TelMarketingCenterRepeat> findByMobileOrderByCreateTimeDesc(String mobile);

    Page<TelMarketingCenterRepeat> findAllByMobile(String mobile, Pageable pageable);

    TelMarketingCenterRepeat findFirstByMobileOrderByCreateTimeDesc(String mobile);

    @Query(value = "select c.description,count(DISTINCT tmcr.user) as count from tel_marketing_center_repeat tmcr join channel c on tmcr.channel=c.id where tmcr.create_time between ?1 and ?2 group by tmcr.channel", nativeQuery = true)
    List<Object[]> findInputAmountByChannel(Date startTime, Date endTime);

    @Query(value = "select c.description,count(DISTINCT tmcr.user) as count from tel_marketing_center_repeat tmcr join tel_marketing_center tmc on tmc.user=tmcr.user join tel_marketing_center_history tmch on tmc.id=tmch.tel_marketing_center join channel c on tmcr.channel=c.id where tmcr.create_time between ?1 and ?2 and tmch.create_time > tmcr.create_time group by tmcr.channel", nativeQuery = true)
    List<Object[]> findCallAmountByChannel(Date startTime, Date endTime);

    @Query(value = "select case when a.name is null then '未知' else a.name end ,count(DISTINCT tmcr.mobile) as count from tel_marketing_center_repeat tmcr left join mobile_area ma on tmcr.mobile=ma.mobile left join area a on ma.area=a.id where tmcr.create_time between ?1 and ?2 group by ma.area", nativeQuery = true)
    List<Object[]> findInputAmountByArea(Date startTime, Date endTime);

    @Query(value = "select case when a.name is null then '未知' else a.name end ,count(DISTINCT tmcr.mobile) as count from tel_marketing_center_repeat tmcr join tel_marketing_center tmc on tmc.user=tmcr.user join tel_marketing_center_history tmch on tmch.tel_marketing_center=tmc.id left join mobile_area ma on tmcr.mobile=ma.mobile left join area a on ma.area=a.id where tmcr.create_time between ?1 and ?2 and tmch.create_time >tmcr.create_time group by ma.area", nativeQuery = true)
    List<Object[]> findCallAmountByArea(Date startTime, Date endTime);

    @Query(value = "select tmcs.description ,count(*) from tel_marketing_center_repeat tmcr join tel_marketing_center_source tmcs on tmcr.source=tmcs.id where tmcr.create_time between ?1 and ?2 group by tmcs.id", nativeQuery = true)
    List<Object[]> findInputAmountByBehavior(Date startTime, Date endTime);

    @Query(value = "select tmcs.description,count(DISTINCT tmcr.id) from tel_marketing_center_repeat tmcr join tel_marketing_center_source tmcs on tmcr.source=tmcs.id join tel_marketing_center tmc on tmc.user=tmcr.user join tel_marketing_center_history tmch on tmch.tel_marketing_center =tmc.id where tmcr.create_time between ?1 and ?2 and tmch.create_time >tmcr.create_time and tmcs.id !=122 group by tmcs.id UNION select tmcs.description,count(DISTINCT tmcr.id) from tel_marketing_center_repeat tmcr join tel_marketing_center_source tmcs on tmcr.source=tmcs.id join tel_marketing_center tmc on tmc.mobile=tmcr.mobile join tel_marketing_center_history tmch on tmch.tel_marketing_center =tmc.id where tmcr.create_time between ?1 and ?2 and tmch.create_time >tmcr.create_time group by tmcs.id", nativeQuery = true)
    List<Object[]> findCallAmountByBehavior(Date startTime, Date endTime);

    @Query(value = "select tmcs.description ,count(*) from tel_marketing_center tmc join tel_marketing_center_source tmcs on tmc.source=tmcs.id where tmc.create_time between ?1 and ?2 group by tmcs.id", nativeQuery = true)
    List<Object[]> findInputAmountByNewUserBehavior(Date startTime, Date endTime);

    @Query(value = "select tmcs.description,count(DISTINCT tmc.id) from tel_marketing_center tmc join tel_marketing_center_source tmcs on tmc.source=tmcs.id join tel_marketing_center_history tmch on tmch.tel_marketing_center =tmc.id where tmc.create_time between ?1 and ?2 and tmch.create_time >tmc.create_time group by tmcs.id", nativeQuery = true)
    List<Object[]> findCallAmountByNewUserBehavior(Date startTime, Date endTime);

    @Query(value = "SELECT count(tmcr.id) FROM tel_marketing_center_repeat tmcr WHERE tmcr.mobile = ?1 AND tmcr.source = ?4 AND tmcr.create_time between ?2 and ?3", nativeQuery = true)
    Integer countByMobileAndSource(String mobile, Date startTime, Date endTime, Long id);

    @Query(value = "select mobile from tel_marketing_center_repeat ORDER BY create_time DESC LIMIT 1", nativeQuery = true)
    String getCertainNeedRandom();

    @Query(value = "SELECT " +
        " IFNULL(count(distinct  tmch.id),0) " +
        " FROM " +
        "  tel_marketing_center_repeat tmcr " +
        " JOIN tel_marketing_center tmc ON tmc. USER = tmcr. USER " +
        " JOIN tel_marketing_center_history tmch ON tmc.id = tmch.tel_marketing_center " +
        " JOIN channel c ON tmcr.channel = c.id " +
        " WHERE " +
        "  c.`description` like ('小米车险%')" +
        " AND tmch.type = 1 " +
        " AND tmch.deal_result not in ('修改跟进人') " +
        " AND tmc.update_time BETWEEN ?1 AND ?2 ", nativeQuery = true)
    Integer countXiaomiCall(Date startTime, Date endTime);

    @Query(value = " select * from tel_marketing_center_repeat  " +
        " WHERE mobile = ?1 " +
        " AND source = 206 " +
        " and create_time BETWEEN ?2 and ?3 ", nativeQuery = true)
    List<TelMarketingCenterRepeat> findRenewalCountByMobileAndDate(String s, Date latestMonth, Date date);


    @Query(value = " select * from tel_marketing_center_repeat  " +
        " WHERE mobile = ?1 " +
        " and create_time BETWEEN ?2 and ?3 ", nativeQuery = true)
    List<TelMarketingCenterRepeat> findRepeatByMobile(String s, Date yearFirstDay, Date yearLastDay);
}
