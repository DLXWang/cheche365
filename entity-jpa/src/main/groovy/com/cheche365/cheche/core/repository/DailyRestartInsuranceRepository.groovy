package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.DailyInsurance
import com.cheche365.cheche.core.model.DailyInsuranceStatus
import com.cheche365.cheche.core.model.DailyRestartInsurance
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
public interface DailyRestartInsuranceRepository extends PagingAndSortingRepository<DailyRestartInsurance, Long>, JpaSpecificationExecutor<DailyRestartInsurance> {

    DailyRestartInsurance findFirstByPayment(Payment payment)

    @Query(value = "select * from daily_restart_insurance where daily_insurance = ?1 and status = 5", nativeQuery = true)
    List<DailyRestartInsurance> findAllByDailyInsurance(DailyInsurance dailyInsurance)

    DailyRestartInsurance findFirstByDailyInsuranceAndStatusOrderByIdDesc(DailyInsurance dailyInsurance, DailyInsuranceStatus status)

    DailyRestartInsurance findFirstByDailyInsuranceOrderByIdDesc(DailyInsurance dailyInsurance)

    @Query(value = "select * from daily_restart_insurance dri where exists (select 1 from daily_insurance di where dri.daily_insurance = di.id and di.purchase_order = ?1 ) order by dri.id desc limit 1", nativeQuery = true)
    DailyRestartInsurance findLastByPurchaseOrder(PurchaseOrder purchaseOrder)

    @Query(value = "SELECT a.license_plate_no,DATE_FORMAT(dri.begin_date,'%Y年%c月%d日')AS time,u.mobile FROM daily_restart_insurance dri JOIN daily_insurance di ON dri.daily_insurance=di.id JOIN purchase_order p ON p.id=di.purchase_order JOIN user u ON p.applicant=u.id JOIN auto a ON p.auto=a.id WHERE dri.status=4 AND dri.begin_date = date_add(curdate(), INTERVAL 1 DAY)", nativeQuery = true)
    List<Object[]> findRestartDataByTime()

    @Query(value = "SELECT p.order_no,dri.begin_date,dri.end_date,timestampdiff(DAY,dri.begin_date,dri.end_date) + 1 AS days,dri.paid_amount,drid.name,dri.update_time FROM daily_restart_insurance dri JOIN daily_insurance di ON di.id = dri.daily_insurance JOIN purchase_order p ON di.purchase_order = p.id JOIN daily_restart_insurance_detail drid ON drid.daily_restart_insurance = dri.id WHERE dri.status IN('4','5','6') AND date(dri.update_time) = date_sub(curdate(), INTERVAL 1 DAY)", nativeQuery = true)
    List<Object[]> findRestartedData()

    @Query(value = "select dri.create_time,c.description as sourceChannel,a.`owner`,a.license_plate_no,po.order_no,dri.paid_amount,di.begin_date,di.end_date,i.damage_premium,if(di.bank_card is null, '车车','安心'),u.mobile from daily_restart_insurance dri join daily_insurance di on dri.daily_insurance = di.id join purchase_order po on di.purchase_order = po.id join channel c on c.id = po.source_channel left join auto a on a.id = po.auto join quote_record qr on qr.id = po.obj_id left join insurance i on i.quote_record=qr.id join user u on u.id=po.applicant where dri.create_time between ?1 and ?2 and qr.insurance_company = ?3 and dri.`status` = ?4 order by dri.id", nativeQuery = true)
    List<Object[]> findAnswernUltimoData(Date startDate, Date endDate, InsuranceCompany company, DailyInsuranceStatus status)

    @Query(value = "select po.id,dri.begin_date from  daily_insurance di left join daily_restart_insurance dri on dri.daily_insurance=di.id join purchase_order po on di.purchase_order=po.id where di.id in( select max(id) from  daily_insurance di where purchase_order in (?1) and di.status='7' group by purchase_order)", nativeQuery = true)
    List<Object[]> findRestartDataByIds(List<String> ids)

    @Query(value = "SELECT IFNULL(sum(TO_DAYS(end_date)-TO_DAYS(begin_date)+1),0) FROM daily_restart_insurance WHERE date(create_time) < CURDATE() AND status = 5", nativeQuery = true)
    BigInteger findAllRestartDays()

    @Query(value = "select au.license_plate_no,di.policy_no,dri.begin_date,dri.paid_amount,dri.create_time from daily_insurance di, purchase_order po,auto au,daily_restart_insurance dri where di.purchase_order=po.id and po.auto=au.id and dri.daily_insurance=di.id and dri.status=5 and di.bank_card is null and date_add(DATE_FORMAT(dri.create_time,'%Y-%c-%d'),INTERVAL 1 DAY) = curdate()", nativeQuery = true)
    List<Object[]> findRestartApplyList()
}
