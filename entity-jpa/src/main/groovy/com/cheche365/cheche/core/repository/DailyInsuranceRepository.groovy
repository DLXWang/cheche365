package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.*
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface DailyInsuranceRepository extends PagingAndSortingRepository<DailyInsurance, Long>, JpaSpecificationExecutor<DailyInsurance> {

    @Query(value = "select * from daily_insurance where purchase_order = ?1 and status <> 1 order by id desc", nativeQuery = true)
    List<DailyInsurance> findAllByPurchaseOrderOrderByIdDesc(PurchaseOrder order)

    Long countByPurchaseOrder(PurchaseOrder order);

    @Query(value = "select * from daily_insurance di where di.status = ?1 and di.begin_date <= curdate() order by di.id asc limit ?2", nativeQuery = true)
    List<DailyInsurance> findByStatusAndBeginDate(Long stopApplyStatus, Integer pageSize);

    @Query(value = "select * from daily_insurance di where di.status in (2,3) and (di.restart_date <= curdate() or di.end_date +1 <= curdate()) order by di.id asc limit ?1", nativeQuery = true)
    List<DailyInsurance> findByStatusAndRestartDate(Integer pageSize);

    @Query(value = "select * from daily_insurance where purchase_order = ?1 and status in (?2) order by id desc", nativeQuery = true)
    List<DailyInsurance> findByPurchaseOrderAndStatusByIdDesc(PurchaseOrder order, List<Long> status)

    @Query(value = "select * from daily_insurance di where di.status not in (1) and di.purchase_order=?1 group by di.bank_no", nativeQuery = true)
    List<DailyInsurance> findByPurchaseOrderAndStatusNotOne(PurchaseOrder purchaseOrder)

    @Query(value = "select di.* from daily_insurance di, purchase_order po where po.applicant=?1 and di.status in (2,3,7) and di.purchase_order=po.id", nativeQuery = true)
    List<DailyInsurance> findValidStopRecord(User user)

    @Query(value = "SELECT a.license_plate_no,concat(DATE_FORMAT(di.begin_date,'%Y年%c月%d日'),'-',DATE_FORMAT(di.end_date,'%Y年%c月%d日')) AS time,u.mobile ,p.order_no FROM daily_insurance di JOIN purchase_order p ON p.id=di.purchase_order JOIN user u ON p.applicant=u.id JOIN auto a ON p.auto=a.id WHERE di.status=2 AND di.begin_date = date_add(curdate(), INTERVAL 1 DAY)", nativeQuery = true)
    List<Object[]> findStopDataByTime()

    @Query(value = "SELECT p.order_no,di.begin_date,di.end_date,timestampdiff(DAY,di.begin_date,di.end_date) + 1 AS days,di.total_refund_amount,did.name,di.update_time FROM daily_insurance di JOIN purchase_order p ON di.purchase_order = p.id JOIN daily_insurance_detail did ON did.daily_insurance = di.id WHERE di.status='2' AND date(di.update_time) = date_sub(curdate(), INTERVAL 1 DAY)", nativeQuery = true)
    List<Object[]> findStopData()

    @Query(value = "select di.create_time,c.description as sourceChannel,a.`owner`,a.license_plate_no,po.order_no,-1*di.total_refund_amount as premiumSum,di.begin_date,di.end_date ,i.damage_premium,if(di.bank_card is null, '车车','安心'),u.mobile from daily_insurance di join purchase_order po on di.purchase_order = po.id join channel c on c.id = po.source_channel left join auto a on a.id = po.auto join quote_record qr on qr.id = po.obj_id left join insurance i on i.quote_record=qr.id join user u on u.id=po.applicant where di.end_date between ?1 and ?2 and di.begin_date >='2017-06-01 00:00:00' and qr.insurance_company = ?3 and di.`status` != ?4 order by di.id", nativeQuery = true)
    List<Object[]> findAnswernUltimoData(Date startDate, Date endDate, InsuranceCompany company, DailyInsuranceStatus excludeStatus)

    //申请过停驶的车辆数
    @Query(value = "SELECT count(DISTINCT purchase_order) FROM daily_insurance WHERE `status` <> 1 AND date(create_time) < CURDATE()", nativeQuery = true)
    BigInteger findStoppedNum()

    //当日正在停驶的车辆数
    @Query(value = "SELECT count(DISTINCT purchase_order) FROM daily_insurance WHERE `status` <> 1 AND begin_date <= DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND end_date >= DATE_SUB(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
    BigInteger findStoppingNum()

    //停驶总天数 复驶总天数 停驶总次数 停驶总返还车险保费
    @Query(value = "SELECT IFNULL(sum(TO_DAYS(end_date)-TO_DAYS(begin_date)+1),0),count(purchase_order),sum(total_refund_amount) FROM daily_insurance WHERE `status` <> 1 AND date(create_time) < CURDATE()", nativeQuery = true)
    List<Object[]> findStopDays()

    @Query(value = "select purchase_order,di.begin_date,timestampdiff(DAY,di.begin_date,di.end_date) + 1 lastStopDays from  daily_insurance di left join daily_restart_insurance dri on dri.daily_insurance=di.id where di.id in( select max(id) from  daily_insurance where purchase_order in (?1) group by purchase_order)", nativeQuery = true)
    List<Object[]> findStopDataByIds(List<String> ids)

    @Query(value = "select po.id,po.create_time,po.order_no,a.owner,a.license_plate_no,ae.name city,ch.name source,ost.name,po.order_source_id,po.paid_amount,i.premium,dis.description, count(di.id) stopNum,sum(timestampdiff(DAY,di.begin_date,di.end_date) + 1) stopDays,sum(timestampdiff(DAY,dri.begin_date,dri.end_date) + 1) restartDays,sum(total_refund_amount)   from daily_insurance di join daily_insurance_status dis on di.status=dis.id join purchase_order po on di.purchase_order=po.id join auto a on po.auto=a.id  join quote_record qr on po.obj_id=qr.id join area ae on qr.area=ae.id join channel ch on po.source_channel=ch.id left join order_source_type ost on po.order_source_type=ost.id join insurance i on qr.create_time=i.create_time  left join daily_restart_insurance dri on dri.daily_insurance=di.id  where po.id in(?1) and di.status<>1 group by po.id ORDER BY po.id desc", nativeQuery = true)
    List<Object[]> findDataByOrderIds(List<String> ids)

    @Query(value = "select au.license_plate_no,di.policy_no,di.begin_date,di.end_date,di.total_refund_amount,di.create_time,di.id from daily_insurance di, purchase_order po,auto au where di.purchase_order=po.id and po.auto=au.id and (di.status=2 or di.status=3) and di.bank_card is null and date_add(DATE_FORMAT(di.create_time,'%Y-%c-%d'),INTERVAL 1 DAY) = CURDATE()", nativeQuery = true)
    List<Object[]> findStopInsuranceByCurDate()

    // 地区, 订单号, 累计返现金额, 累计停驶天数, 最长停驶天数
    @Query(value = "SELECT IF(po.area LIKE '11%','110000',IF(po.area LIKE '4403%','440300',IF(po.area LIKE '44%','440000',po.area))),po.order_no,IF(SUM(dri.premium) IS NOT NULL,SUM(di.total_refund_amount)-SUM(dri.premium),SUM(di.total_refund_amount)),SUM(IF(di.restart_date IS NOT NULL,DATEDIFF(di.restart_date,di.begin_date),DATEDIFF(di.end_date,di.begin_date) + 1)),MAX(IF(di.restart_date IS NOT NULL,DATEDIFF(di.restart_date,di.begin_date),DATEDIFF(di.end_date,di.begin_date) + 1)) FROM purchase_order po LEFT JOIN daily_insurance di ON po.id = di.purchase_order LEFT JOIN daily_restart_insurance dri ON di.id = dri.daily_insurance AND dri.`status` = 5 WHERE po.`status` = 5 AND di.`status` <> 1 AND po.`create_time` BETWEEN DATE_SUB(NOW(), INTERVAL 1 YEAR) AND NOW() GROUP BY di.purchase_order ORDER BY po.area,po.create_time;", nativeQuery = true)
    List<Object[]> countOneYearDailyInsurance()

    @Query(value = "SELECT DISTINCT po.order_no ,u.mobile FROM purchase_order po,daily_insurance di,insurance i , user u WHERE po.id = di.purchase_order AND po.obj_id = i.quote_record and po.applicant = u.id AND po.status = 5 AND di.status <> 1 AND DATEDIFF(i.expire_date,CURDATE())=89", nativeQuery = true)
    List<Object[]> findAnswernExpire90DaysData()

    @Query(value = "SELECT sum(wt.amount) FROM daily_insurance di, wallet_trade wt WHERE di.id = wt.trade_source_id AND di.purchase_order = ?1 AND wt.trade_type = 1",nativeQuery = true)
    BigDecimal findAllReturnMoney(PurchaseOrder purchaseOrder)

}
