package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.core.model.Insurance;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TelMarketingCenterRepository extends JpaSpecificationExecutor<TelMarketingCenter>, PagingAndSortingRepository<TelMarketingCenter, Long> {

    List<TelMarketingCenter> findByOperatorAndDisplayOrderByCreateTime(InternalUser operator, boolean display);

    @Query(value = "SELECT * FROM tel_marketing_center  WHERE operator=?1 AND display=?2 AND status=?3", nativeQuery = true)
    List<TelMarketingCenter> findDisplayByStatus(InternalUser operator, boolean display, long status);

    @Query(value = " SELECT * FROM tel_marketing_center " +
            "WHERE trigger_time < NOW()+INTERVAL 15 MINUTE AND trigger_time IS NOT NULL " +
            "AND operator =?1 ORDER BY trigger_time DESC", nativeQuery = true)
    List<TelMarketingCenter> findByInternalUserAndTriggerTime(Long operatorId);

    @Query(value = "select * from tel_marketing_center tmc where tmc.status = ?1 and DATE(tmc.update_time) = ?2", nativeQuery = true)
    List<TelMarketingCenter> findByStatusAndUpdateTime(Long status, Date updateTime);

    TelMarketingCenter findFirstByMobileOrderByUpdateTimeDesc(String mobile);

    TelMarketingCenter findFirstByUser(User user);

    @Query(value = "select count(*) from tel_marketing_center where operator=?1", nativeQuery = true)
    Integer countByOperator(Long operatorId);

    @Query(value = "select count(*) from tel_marketing_center where operator=?1 and status=?2", nativeQuery = true)
    Integer countByOperatorAndStatus(Long operatorId,Long status);

    @Query(value = "select count(distinct (t.id)) from tel_marketing_center t, tel_marketing_center_repeat tr where t.mobile = tr.mobile and operator=?1 and display = 1", nativeQuery = true)
    Integer countByOperatorAndDisplay(Long operatorId);

    @Query(value = "select * from tel_marketing_center where operator=?1 limit ?2", nativeQuery = true)
    List<TelMarketingCenter> findByOperator(InternalUser internalUser, Integer limit);

    List<TelMarketingCenter> findByMobile(String mobile);

    List<TelMarketingCenter> findByUser(User user);

    @Query(value = "select id from tel_marketing_center where create_time <= ?1 order by id desc limit 1", nativeQuery = true)
    Long findMaxIdByTime(Date createTime);

    @Query(value = "SELECT * " +
            " FROM tel_marketing_center it	" +
            " WHERE it.id IN (?1)", nativeQuery = true)
    List<TelMarketingCenter> findUserSourceByIds(List ids);

    @Query(value = " SELECT * " +
            " FROM tel_marketing_center t " +
            " WHERE t.mobile IN ?1  ", nativeQuery = true)
    List<TelMarketingCenter> findTelMarketingCenterByMobiles(List mobiles);

    @Query(value = " SELECT s.description, count(*)                       " +
            " FROM tel_marketing_center t                               " +
            " JOIN tel_marketing_center_source s ON t.source = s.id     " +
            " WHERE t.create_time BETWEEN ?1 AND ?2 GROUP BY t.source   ", nativeQuery = true)
    List<Object[]> findSourceInputAmount(Date startTime, Date endTime);


    @Query(value = " SELECT center.* " +
            " FROM tel_marketing_center center " +
            " LEFT JOIN mobile_area area " +
            " ON center.mobile = area.mobile " +
            " WHERE area.mobile IS NULL AND center.id>?1  LIMIT ?2 ", nativeQuery = true)
    List<TelMarketingCenter> findByAreaIsNull(Long id, Integer limit);

    @Query(value = " SELECT count(*) " +
            " FROM tel_marketing_center center " +
            " LEFT JOIN mobile_area area " +
            " ON center.mobile = area.mobile " +
            " WHERE area.mobile IS NULL   ", nativeQuery = true)
    Long countByAreaIsNull();

    @Query(value = " SELECT t.* FROM tel_marketing_center t                                                                         " +
            " where t.`status` not in (?5)                                                                      " +
            " and (((t.trigger_time < NOW() OR t.trigger_time IS NULL) and t.expire_time BETWEEN ?1 and ?2)                             " +
            " 	or (t.expire_time < NOW() and (DAYOFYEAR(t.trigger_time) < DAYOFYEAR(NOW()+INTERVAL -1 year) OR t.trigger_time IS NULL) " +
            " 		and DAYOFYEAR(t.expire_time) BETWEEN DAYOFYEAR(?1+INTERVAL -1 year) and DAYOFYEAR(?2+INTERVAL -1 year)))            " +
            " 		LIMIT ?4 OFFSET ?3                                                                                                  ", nativeQuery = true)
    List<TelMarketingCenter> findPushableData(Date startDate, Date endDate, int startIndex, int pageSize, List statusParams);

    @Query(value = "select * from tel_marketing_center where operator=?1 order by source ,update_time desc limit ?2, ?3", nativeQuery = true)
    List<TelMarketingCenter> findPageByOperatorId(Long operatorId, int startIndex, int pageSize);

    @Query(value = "select * from tel_marketing_center where operator=?1 and status=?2 order by source ,update_time desc limit ?3, ?4", nativeQuery = true)
    List<TelMarketingCenter> findPageByOperatorIdAndStatus(Long operatorId, Long status, int startIndex, int pageSize);

    @Query(value = "select * from tel_marketing_center t where t.id in ?1 order by t.id desc ", nativeQuery = true)
    List<TelMarketingCenter> findByIds(List<String> ids);

    @Query(value = "select count(distinct(t.id)) from tel_marketing_center t ,tel_marketing_center_repeat tr where t.mobile = tr.mobile and  t.operator is null and t.display = 1", nativeQuery = true)
    Integer countByOperatorIsNull();

    @Query(value="SELECT   tmc.id,   tmcr.source_id,   tmcr.source_table,   tmcr.channel,ci.create_time,ci.effective_date  FROM   tel_marketing_center tmc LEFT JOIN tel_marketing_center_repeat tmcr ON tmcr.mobile = tmc.mobile LEFT JOIN compulsory_insurance ci ON ci.id = tmcr.source_id LEFT JOIN auto a ON a.id = ci.auto WHERE   tmcr.source_table = 'compulsory_insurance' AND tmcr.source = 206 AND tmc.`status` IN (1,2,3,4,6,70,90,91,92) AND (((a.area='310000' AND ci.insurance_company IN(20000,250000) AND ci.expire_date IN ( date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY))) OR (a.area='310000' AND ci.insurance_company IN(10000,10500) AND ci.expire_date IN ( date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY))) OR (a.area='310000' AND (ci.insurance_company = 25000 )AND (ci.expire_date IN ( date_add(curdate(), INTERVAL 45 DAY), date_add(curdate(), INTERVAL 30 DAY), date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY)))) OR (a.area IN('430100','330100') and (ci.insurance_company IN (10000,10500,20000,250000,25000) )AND (ci.expire_date IN ( date_add(curdate(), INTERVAL 45 DAY), date_add(curdate(), INTERVAL 30 DAY), date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY)))) OR(a.area IN('320500','320100') AND ci.insurance_company IN (10000,10500,20000,250000,25000,40000) AND  ci.expire_date IN ( date_add(curdate(), INTERVAL 25 DAY), date_add(curdate(), INTERVAL 10 DAY), date_add(curdate(), INTERVAL 1 DAY))) OR(a.area IN('510100','330200','371000') AND ci.expire_date IN ( date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY))) OR(a.area='370100' AND (ci.insurance_company IN (10000,10500,20000,250000,25000,40000) )AND (ci.expire_date IN ( date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY)))) OR(a.area IN('330400','330300') AND (ci.expire_date IN ( date_add(curdate(), INTERVAL 45 DAY), date_add(curdate(), INTERVAL 30 DAY), date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY))))) OR  (( ci.expire_date IN ( date_add(curdate(), INTERVAL 75 DAY), date_add(curdate(), INTERVAL 60 DAY), date_add(curdate(), INTERVAL 45 DAY), date_add(curdate(), INTERVAL 30 DAY), date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY))) AND((a.area IN ('310000','430100','330100') AND ci.insurance_company NOT IN(20000,250000,10000,10500,25000)) OR (a.area IN ('320500','320100','370100') AND ci.insurance_company NOT IN(20000,250000,10000,10500,25000,4000)) OR (a.area NOT IN ('310000','430100','330100','320500','320100','370100','510100','330200','371000','330300','330400'))))) GROUP BY a.id", nativeQuery = true)
    List<Object[]>  findRemindByCompulsoryRenewal();

    @Query(value="SELECT   tmc.id, tmcr.source_id, tmcr.source_table, tmcr.channel,i.create_time,i.effective_date  FROM   tel_marketing_center tmc LEFT JOIN tel_marketing_center_repeat tmcr ON tmcr.mobile = tmc.mobile LEFT JOIN insurance i ON   i.id = tmcr.source_id LEFT JOIN auto a ON a.id =   i.auto WHERE   tmcr.source_table = 'insurance' AND tmcr.source = 206 AND tmc.`status` IN (   1,   2,   3,   4,   6,   70,   90,   91,   92 ) AND ((a.area='310000' AND   i.insurance_company IN(20000,250000) AND   i.expire_date IN ( date_add(curdate(), INTERVAL 50 DAY), date_add(curdate(), INTERVAL 35 DAY), date_add(curdate(), INTERVAL 20 DAY), date_add(curdate(), INTERVAL 5 DAY), date_add(curdate(), INTERVAL 1 DAY))) OR (a.area='310000' AND (  i.insurance_company = 25000)AND (  i.expire_date IN ( date_add(curdate(), INTERVAL 45 DAY), date_add(curdate(), INTERVAL 30 DAY), date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY)))) OR (a.area IN('430100','330100') and (   i.insurance_company IN (10000,10500,20000,250000,25000))AND (  i.expire_date IN ( date_add(curdate(), INTERVAL 45 DAY), date_add(curdate(), INTERVAL 30 DAY), date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY)))) OR (a.area='350000' AND   i.insurance_company IN (10000,10500,20000,250000,25000,40000) AND   i.expire_date IN ( date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY))) OR(a.area IN('320500','320100') AND (   i.insurance_company IN (10000,10500,20000,250000,25000,40000))AND (  i.expire_date IN ( date_add(curdate(), INTERVAL 25 DAY), date_add(curdate(), INTERVAL 10 DAY), date_add(curdate(), INTERVAL 1 DAY)))) OR(a.area IN('510100','330200','371000') AND (  i.expire_date IN ( date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY)))) OR(a.area='370100' AND (   i.insurance_company IN (10000,10500,20000,250000,25000,40000))AND (   i.expire_date IN ( date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY)))) OR(a.area IN('330400','330300') AND (  i.expire_date IN ( date_add(curdate(), INTERVAL 45 DAY), date_add(curdate(), INTERVAL 30 DAY), date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY)))) OR  ((  i.expire_date IN ( date_add(curdate(), INTERVAL 75 DAY), date_add(curdate(), INTERVAL 60 DAY), date_add(curdate(), INTERVAL 45 DAY), date_add(curdate(), INTERVAL 30 DAY), date_add(curdate(), INTERVAL 15 DAY), date_add(curdate(), INTERVAL 1 DAY)) AND((a.area IN ('310000') AND   i.insurance_company NOT IN(20000,250000,25000)) OR (a.area IN ('430100','330100') AND   i.insurance_company NOT IN(20000,250000,10000,10500,25000)) OR (a.area IN ('350000','320500','320100','370100') AND   i.insurance_company NOT IN(20000,250000,10000,10500,25000,4000)) OR (a.area NOT IN ('310000','430100','350000','320500','320100','330100','370100','510100','330200','371000','330400','330300')))))) GROUP BY a.id ", nativeQuery = true)
    List<Object[]>  findRemindByCommercialRenewal();


    @Query(value = " select COUNT(*) from tel_marketing_center t " +
        " WHERE status = 1 " +
        " and t.update_time BETWEEN ?1 and ?2 " +
        " and (source IN (60,147) " +
        " or source IN (SELECT id FROM tel_marketing_center_source where NAME LIKE 'MARKETING%')) " +
        " and operator =(SELECT id FROM internal_user WHERE email = ?3) ", nativeQuery = true)
    Integer findNeedPushedData(Date StartTime,Date endTime,String userEmail);

    @Query(value = " select * from tel_marketing_center t " +
        " where mobile = (select mobile from tel_marketing_center_repeat t1 ORDER BY t1.`id` DESC LIMIT 1) ", nativeQuery = true)
    TelMarketingCenter getCertainNeedRandom();

    @Query(value = "SELECT IFNULL(count(DISTINCT tmc.mobile), 0) FROM tel_marketing_center tmc LEFT JOIN tel_marketing_center_repeat tmcr ON tmcr.mobile = tmc.mobile JOIN channel c ON c.id = tmcr.channel  " +
        " WHERE c.`description` like ( '小米车险%' ) AND tmc.update_time BETWEEN ?1 AND ?2", nativeQuery = true)
    Integer countXiaomiAll(Date start,Date end);

    @Query(value = " SELECT  " +
        "  IFNULL(count(distinct tmch.id), 0)  " +
        " FROM  " +
        "  tel_marketing_center tmc  " +
        " LEFT JOIN tel_marketing_center_repeat tmcr ON tmcr.mobile = tmc.mobile  " +
        " JOIN channel c ON c.id = tmcr.channel  " +
        " JOIN tel_marketing_center_history tmch ON tmc.id = tmch.tel_marketing_center " +
        " WHERE  " +
        "  c.`description` like ('小米车险%')  " +
        " AND tmch.`status` NOT IN(?3)  " +
        " AND tmch.type = 1 " +
        " AND tmch.deal_result not in ('修改跟进人') " +
        " AND tmc.update_time BETWEEN ?1  " +
        " AND ?2 ", nativeQuery = true)
    Integer countXiaomiByStatus(Date start, Date end, List<TelMarketingCenterStatus> statusList);
}
