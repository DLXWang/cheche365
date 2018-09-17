package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.*
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface CompulsoryInsuranceRepository extends PagingAndSortingRepository<CompulsoryInsurance, Long> {

    CompulsoryInsurance findFirstByApplicantAndAutoAndEffectiveDateBetween(User applicant, Auto auto, Date beginEffectiveDate, Date endEffectiveDate)

    CompulsoryInsurance findFirstByApplicantAndAutoAndEffectiveDateBeforeOrderByEffectiveDateDesc(User applicant, Auto auto, Date effectiveDate);

    List<CompulsoryInsurance> findByApplicantAndAuto(User applicant, Auto);

    List<CompulsoryInsurance> findByApplicantAndAutoAndInsurancePackage(User applicant, Auto auto, InsurancePackage insurancePackage);

    CompulsoryInsurance findFirstByApplicantAndAutoAndEffectiveDateAfterOrderByEffectiveDateDesc(User applicant, Auto auto, Date effectiveDate);

    Long countByPolicyNo(String policyNo);

    @Query(value = "select * from compulsory_insurance i where i.policy_no = ?1 order by i.create_time desc limit 1", nativeQuery = true)
    CompulsoryInsurance findLastByPolicyNo(String policyNo);

    CompulsoryInsurance findByPolicyNoAndQuoteRecordNot(String policyNo, QuoteRecord quoteRecord);

    CompulsoryInsurance findByProposalNo(String proposalNo);

    CompulsoryInsurance findFirstByQuoteRecordOrderByCreateTimeDesc(QuoteRecord quoteRecord);

    CompulsoryInsurance findFirstByQuoteRecordOrderByIdDesc(QuoteRecord quoteRecord);

    @Query(value = "select * from compulsory_insurance where auto = ?1 order by id desc limit 1 ",nativeQuery = true)
    CompulsoryInsurance findFirstByAutoIdOrderByIdDesc(Long autoId);

    @Query(value = "select * from compulsory_insurance where  quote_record=?1 order by create_time desc limit 1", nativeQuery = true)
    CompulsoryInsurance findByQuoteRecordId(Long quoteRecordId);

    @Query(value = "select count(id) from compulsory_insurance where  quote_record=?1", nativeQuery = true)
    Long countByQuoteRecordId(Long quoteRecordId);

    @Query(value = "select * from compulsory_insurance where quote_record = (select obj_id from purchase_order where order_no=?1 and applicant=?2 and type=1) order by create_time desc limit 1", nativeQuery = true)
    CompulsoryInsurance searchByPurchaseOrderNo(String orderNo, Long userId);

    @Query(value = "select ci.* from compulsory_insurance ci,purchase_order o where ci.quote_record = o.obj_id and o.applicant=?1 and o.type=1 and o.id in(?2) ", nativeQuery = true)
    List<CompulsoryInsurance> findCompulsoryInsurancesByUser(Long userId, List<Long> orderIds);

    @Query(value = "select ci.* from compulsory_insurance ci,purchase_order o,auto a where ci.quote_record = o.obj_id and ci.auto = a.id and o.type=1 and o.id in(?1) and a.identity_type = 1 order by create_time desc limit 1", nativeQuery = true)
    CompulsoryInsurance findCompulsoryInsurancesByOrder(List<Long> orderIds);

    @Query(value = "select ci.applicant, ci.expire_date, ci.create_time, ci.id, 'compulsory_insurance' from compulsory_insurance ci WHERE ci.expire_date BETWEEN ?1 and ?2 order by ci.id", nativeQuery = true)
    List<Object[]> findByExpireDateBetween(Date sDate, Date eDate);

    @Query(value = "SELECT i.applicant,i.expire_Date,i.create_Time,i.id,'compulsory_insurance',i.quote_Record FROM compulsory_insurance i JOIN user ur ON i.applicant = ur.id JOIN quote_record qr ON i.quote_Record = qr.id LEFT JOIN ( SELECT a.*,IFNULL(al.days,90) AS days FROM area a LEFT JOIN area_insurance_time_limit al ON a.id = al.area ) r ON r.id = qr.area WHERE (ur.user_Type IS NULL OR ur.user_Type = 1 ) AND qr.channel NOT IN (?5) AND i.expire_date BETWEEN IF(?1 is null, ?2, ?1+INTERVAL r.days DAY) AND ?2+INTERVAL r.days DAY LIMIT ?4 OFFSET ?3", nativeQuery = true)
    List<Object[]> findPageDataByExpireDate(Date sDate, Date eDate, int startIndex, int pageSize, List excludeChannels);

    @Query("select distinct i.auto from CompulsoryInsurance i where i.applicant = ?1 and i.expireDate BETWEEN ?2 and ?3")
    List<Auto> findAutoByUserAndExpireDate(User applicant, Date startExpireDate, Date endExpireDate);


    @Query(value = "SELECT * from compulsory_insurance where quote_record=?1 and ((expire_date<= ?2 and expire_date >now()) or (expire_date >=?3 and expire_date < now()))", nativeQuery = true)
    CompulsoryInsurance findRenewal(Long qrId, Date beforeDate, Date afterDate)

    @Query(value = "select po.id from purchase_order po where obj_id = (select i.quote_record from compulsory_insurance i where i.id = ?1)", nativeQuery = true)
    Long findOrderIdByCompulsoryInsuranceId(Long id)
}
