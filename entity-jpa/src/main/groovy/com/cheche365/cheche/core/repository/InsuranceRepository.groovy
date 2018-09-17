package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.*
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface InsuranceRepository extends PagingAndSortingRepository<Insurance, Long> {

    Long countByPolicyNo(String policyNo);

    Insurance findByPolicyNo(String policyNo);

    Insurance findByProposalNo(String proposalNo);

    @Query(value = "select * from insurance i where i.policy_no = ?1 order by i.create_time desc limit 1", nativeQuery = true)
    Insurance findLastByPolicyNo(String policyNo)

    Insurance findByPolicyNoAndQuoteRecordNot(String policyNo, QuoteRecord quoteRecord);

    Insurance findFirstByQuoteRecordOrderByCreateTimeDesc(QuoteRecord quoteRecord);

    @Query(value = "select * from insurance where auto = ?1 order by id desc limit 1 ",nativeQuery = true)
    Insurance findFirstByAutoIdOrderByIdDesc(Long autoId);

    Insurance findFirstByQuoteRecordOrderByIdDesc(QuoteRecord quoteRecord);

    @Query(value = "select * from insurance where  quote_record=?1 order by create_time desc limit 1", nativeQuery = true)
    Insurance findByQuoteRecordId(Long quoteRecordId);

    @Query(value = "select * from insurance where quote_record = (select obj_id from purchase_order where order_no=?1 and applicant=?2 and type=1) order by create_time desc limit 1", nativeQuery = true)
    Insurance searchByPurchaseOrderNo(String orderNo, Long userId);

    @Query(value = "select i.* from insurance i,purchase_order o where i.quote_record = o.obj_id and o.applicant=?1 and o.type=1 and o.id in(?2) ", nativeQuery = true)
    List<Insurance> findInsurancesByUser(Long userId, List<Long> orderId);

    @Query(value = "select i.* from insurance i,purchase_order o,auto a where i.quote_record = o.obj_id and i.auto = a.id and o.type=1 and o.id in(?1) and a.identity_type = 1 order by create_time desc limit 1", nativeQuery = true)
    Insurance findInsurancesByOrder(List<Long> orderId);

    @Query(value = "SELECT i.applicant,i.expire_Date,i.create_Time,i.id,'insurance',i.quote_Record FROM insurance i JOIN user ur ON i.applicant = ur.id JOIN quote_record qr ON i.quote_Record = qr.id LEFT JOIN ( SELECT a.*,IFNULL(al.days,90) AS days FROM area a LEFT JOIN area_insurance_time_limit al ON a.id = al.area ) r ON r.id = qr.area WHERE (ur.user_Type IS NULL OR ur.user_Type = 1 ) AND qr.channel NOT IN (?5) AND i.expire_date BETWEEN IF(?1 is null, ?2, ?1+INTERVAL r.days DAY) AND ?2+INTERVAL r.days DAY LIMIT ?4 OFFSET ?3", nativeQuery = true)
    List<Object[]> findPageDataByExpireDate(Date sDate, Date eDate, int startIndex, int pageSize, List excludeChannels);

    @Query("select distinct i.auto from Insurance i where i.applicant = ?1 and i.expireDate BETWEEN ?2 and ?3")
    List<Auto> findAutoByUserAndExpireDate(User applicant, Date startExpireDate, Date endExpireDate);

    @Query(value = "select * from insurance where applicant=?1 and policy_no <> '' order by create_time desc limit 1", nativeQuery = true)
    Insurance findBilledByUser(User user)

    @Query(value = "select * from insurance where applicant=?1 and insurance_company=?2 and policy_no <> '' AND NOW() < expire_date order by create_time desc limit 1", nativeQuery = true)
    Insurance findBilledByUserAndCompany(User user, InsuranceCompany insuranceCompany)

    @Query(value = '''select i.* from insurance i , purchase_order po where NOW() < i.expire_date and i.insurance_company=?2 and 
                        i.quote_record = po.obj_id and (po.status = 3 or po.status = 5) and po.applicant = ?1 order by i.create_time desc''', nativeQuery = true)
    List<Insurance> findAllBilledByUserAndCompany(User user, InsuranceCompany insuranceCompany)

    @Query(value = "SELECT b.name AS bankName,bc.bank_no,bc.name as bcName,case when r.hct BETWEEN ?1+INTERVAL -30 DAY AND ?2 then 1 else 0 END as isTelCenter,r.* FROM (SELECT po.order_no,a.license_plate_no,u.mobile,max(di.bank_card) AS bank_card,MAX(tmch.create_time) AS hct FROM insurance i JOIN purchase_order po ON i.quote_record = po.obj_id LEFT JOIN daily_insurance di ON di.purchase_order = po.id JOIN USER u ON u.id = po.applicant JOIN auto a ON a.id = po.auto LEFT JOIN tel_marketing_center tmc ON tmc.`user` = po.applicant LEFT JOIN tel_marketing_center_history tmch ON tmch.tel_marketing_center = tmc.id WHERE po.`status` IN (?3) AND i.insurance_company in (?4) AND i.effective_date BETWEEN ?1 AND ?2 GROUP BY po.id ) r LEFT JOIN bank_card bc ON bc.id = r.bank_card LEFT JOIN bank b ON bc.bank = b.id", nativeQuery = true)
    List<Object[]> findPerformanceOrder(Date startDate, Date endDate, List<OrderStatus> statuses, List<InsuranceCompany> companies)

    @Query(value = "SELECT * from insurance where quote_record=?1 and ((expire_date<= ?2 and expire_date >now()) or (expire_date >=?3 and expire_date < now()))", nativeQuery = true)
    Insurance findRenewal(Long qrId, Date beforeDate, Date afterDate)

    @Query(value = "select po.id from purchase_order po where obj_id = (select i.quote_record from insurance i  where i.id = ?1)", nativeQuery = true)
    Long findOrderIdByInsuranceId(Long id)
}
