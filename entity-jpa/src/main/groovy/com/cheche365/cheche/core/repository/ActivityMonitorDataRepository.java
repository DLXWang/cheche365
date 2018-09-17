package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ActivityMonitorData;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.BusinessActivity;
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
public interface ActivityMonitorDataRepository extends PagingAndSortingRepository<ActivityMonitorData, Long>, JpaSpecificationExecutor<ActivityMonitorData> {
    List<ActivityMonitorData> findByBusinessActivityAndMonitorTimeLessThanEqual(BusinessActivity businessActivity, Date monitorTime);

    List<ActivityMonitorData> findByBusinessActivityAndAreaAndMonitorTimeGreaterThanEqualAndMonitorTimeLessThanEqualOrderByMonitorTime(
        BusinessActivity businessActivity, Area area, Date startMonitorTime, Date endMonitorTime);

    List<ActivityMonitorData> findByBusinessActivityAndMonitorTimeGreaterThanEqualAndMonitorTimeLessThanEqualOrderByMonitorTime(
        BusinessActivity businessActivity, Date startMonitorTime, Date endMonitorTime);

    @Query(value = "select data.* from activity_monitor_data data where data.business_activity = ?1 and monitor_time >= ?2 and monitor_time <= ?3 and (data.area is null or data.area not in (select aa.area from activity_area aa where aa.business_activity = ?1)) order by data.monitor_time", nativeQuery = true)
    List<ActivityMonitorData> listUnknownSourceMonitorData(Long businessActivityId, Date startMonitorTime, Date endMonitorTime);

    ActivityMonitorData findFirstByBusinessActivityOrderByMonitorTime(BusinessActivity businessActivity);

    ActivityMonitorData findFirstByBusinessActivityAndAreaOrderByMonitorTime(BusinessActivity businessActivity, Area area);

    ActivityMonitorData findFirstByBusinessActivityAndAreaAndMonitorTimeOrderByMonitorTimeDesc(BusinessActivity businessActivity, Area area, Date monitorTime);

    @Query(value = "select data.* from activity_monitor_data data where data.business_activity = ?1 and (data.area is null or data.area not in (select aa.area from activity_area aa where aa.business_activity = ?1)) order by data.monitor_time limit 1", nativeQuery = true)
    ActivityMonitorData getFirstUnknownSourceMonitorData(Long businessActivityId);

    ActivityMonitorData findFirstByBusinessActivityOrderByMonitorTimeDesc(BusinessActivity businessActivity);

    @Query(value = "select po.area area,po.order_source_id business_activity,count(po.id) submit_count,sum(po.paid_amount) submit_amount, 1 sum_type " +
        "from purchase_order po, payment pay, user applicant " +
        "where pay.purchase_order = po.id and po.applicant = applicant.id " +
        "and po.type = 1 and po.status in (1, 2, 3, 4, 5) " +
        "and po.channel in (1, 2, 3, 4, 5, 6, 7) " +
        "and pay.channel in (1, 2, 3, 4, 5, 6, 7) " +
        "and po.order_source_id is not null and po.order_source_type is not null " +
        "and po.order_source_type = 1 " +
        "and pay.create_time >= ?1 and pay.create_time < ?2 " +
        "and po.order_source_id = ?3 " +
        "group by po.area",nativeQuery = true)
    List<Object[]> sumSubmitInfoGroupByArea(Date beginDate, Date endDate, Long activityId);

    @Query(value = "select po.area area,po.order_source_id business_activity,count(po.id) payment_count,sum(po.paid_amount) payment_amount, 2 sum_type ,sum(po.paid_amount - q.auto_tax) no_auto_tax_amount, po.payable_amount customerField1 " +
        "from purchase_order po, quote_record q, payment pay, user applicant, order_operation_info oop " +
        "where pay.purchase_order = po.id and po.applicant = applicant.id and oop.purchase_order = po.id and po.obj_id = q.id " +
        "and po.type = 1 and po.status in (3, 4, 5) " +
        "and oop.current_status in (7, 8, 9, 10, 11) " +
        "and po.channel in (1, 2, 3, 4, 5, 6, 7) " +
        "and pay.channel in (1, 2, 3, 4, 5, 6, 7) " +
        "and pay.status = 2 " +
        "and po.order_source_id is not null and po.order_source_type is not null " +
        "and po.order_source_type = 1 " +
        "and pay.update_time >= ?1 and pay.update_time < ?2 " +
        "and po.order_source_id = ?3 " +
        "group by po.area ",nativeQuery = true)
    List<Object[]> sumPaymentInfoGroupByArea(Date beginDate, Date endDate, Long activityId);

    @Query(value = "SELECT business_activity,COALESCE(sum(pv),0) AS pv,COALESCE(sum(uv),0) AS uv " +
        "FROM activity_monitor_data " +
        "WHERE business_activity IN(?1) " +
        "AND monitor_time BETWEEN ?2 AND ?3 " +
        "GROUP BY business_activity ",nativeQuery = true)
    List<Object[]> sumPvUvByActivity(List<Long> activityId, Date beginDate, Date endDate);

    //数据查询 没有选统计时间时
    @Query(value = "SELECT business_activity,COALESCE(sum(pv),0) AS pv,COALESCE(sum(uv),0) AS uv " +
        "FROM activity_monitor_data " +
        "WHERE business_activity  IN(?1) " +
        "GROUP BY business_activity  ",nativeQuery = true)
    List<Object[]> sumPvUvByActivity(List<Long> activityId);

    @Query(value = "select p.order_source_id,count(p.id),COALESCE(sum(p.paid_amount),0) from purchase_order p " +
        "where p.order_source_type=1 and p.order_source_id in(?1) AND p.status in (3,4,5) " +
        "group by p.order_source_id  ",nativeQuery = true)
    List<Object[]> sumPaymentActivity(List<Long> activityId);

    @Query(value = "select p.order_source_id,count(p.id),COALESCE(sum(p.paid_amount),0) from purchase_order p " +
        "where p.order_source_type=1 and p.order_source_id in(?1) AND p.status in (3,4,5) " +
        "and p.create_time between ?2 and ?3 " +
        "group by p.order_source_id  ",nativeQuery = true)
    List<Object[]> sumPaymentActivity(List<Long> activityId, Date beginDate, Date endDate);
}
