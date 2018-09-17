package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.IdentityType;
import com.cheche365.cheche.core.model.User;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoRepository extends AutoRepositoryCustom, PagingAndSortingRepository<Auto, Long>, JpaSpecificationExecutor<Auto> {


    List<Auto> findByLicensePlateNo(String licensePlatNo);

    Auto findById(String id);

    Auto findFirstByIdentityAndIdentityType(String identity, IdentityType identityType);

    @Query("select distinct at from Auto at, UserAuto ua, User ut where at.id = ua.auto and ua.user = ut.id and at.disable=0 and ut.mobile like :mobile%")
    Page<Auto> findAutoByMobile(@Param("mobile") String mobile, Pageable pageable);

    @Query(value = "select u.* from auto u where u.license_plate_no like ?1% limit 20", nativeQuery = true)
    List<Auto> findByLicense(String license);

    @Query(value = "select u.* from auto u where u.license_plate_no = ?1", nativeQuery = true)
    List<Auto> findByLicenseExact(String license);

    @Query(value = "select distinct u.license_plate_no from auto u where u.license_plate_no like %?1% limit 20", nativeQuery = true)
    List<String> listLicenseNoRange(String licenseNo);

    @Query("select at from Auto at, UserAuto ua, User ut where at.id = ua.auto and ua.user = ut.id and at.licensePlateNo like :licenseNo% and at.owner like :name% and ut.mobile like :mobile%")
    Page<Auto> findByLicenseNoAndNameAndMobile(@Param("licenseNo") String licenseNo,
                                               @Param("name") String name, @Param("mobile") String mobile, Pageable pageable);

    @Query(value = "SELECT a.* " +
        "FROM user_auto ua left join user u on ua.user=u.id left join auto a on ua.auto=a.id " +
        "WHERE " +
        "u.id=?1  and a.engine_no=?2 and a.identity=?3 " +
        "and a.license_plate_no=?4 and a.owner=?5 and a.vin_no=?6 and a.disable=0 order by a.id desc, a.auto_type desc limit 1 ", nativeQuery = true)
    Auto searchByUserAuto(Long userId, String engineNo, String identity, String licensePlateNo, String owner, String vinNo);

    @Query(value = "SELECT a.* " +
        "FROM user_auto ua left join user u on ua.user=u.id left join auto a on ua.auto=a.id " +
        "WHERE " +
        "u.id=?1 and a.license_plate_no=?2 order by a.id desc, a.auto_type desc", nativeQuery = true)
    List<Auto> listByUserAuto(Long userId, String licensePlateNo);

    @Query(value = "SELECT a.* " +
        "FROM user_auto ua left join user u on ua.user=u.id left join auto a on ua.auto=a.id " +
        "WHERE " +
        "u.id=?1 and a.disable=0 order by a.id desc, a.auto_type desc", nativeQuery = true)
    List<Auto> listByUser(Long userId);

    @Query(value = "SELECT a.* " +
        "FROM user_auto ua left join user u on ua.user=u.id left join auto a on ua.auto=a.id " +
        "WHERE " +
        "u.id=?1 and a.disable=0 and a.license_plate_no is not null order by a.id desc, a.auto_type desc", nativeQuery = true)
    List<Auto> listByUserAndPlateNoNotNull(Long userId);

    //我的车辆里面暂时过滤掉证件信息不是身份证的信息以及新车
    @Query(value = "select a from UserAuto ua join ua.user u join ua.auto a where u=?1 and a.disable=0 and a.identityType =1 and a.area is not null order by a desc, a.autoType desc ")
    Page<Auto> searchAutoListPageable(User user, Pageable pageable);

    @Query(value = "SELECT DISTINCT a.* FROM user_auto ua LEFT JOIN auto a ON ua.auto=a.id WHERE a.license_plate_no=?1 AND a.disable=0 ORDER BY a.id DESC ", nativeQuery = true)
    List<Auto> searchByPlateNo(String licensePlateNo);

    @Query(value = "SELECT DISTINCT a.* " +
        "FROM user_auto ua LEFT JOIN user u ON ua.user=u.id LEFT JOIN auto a ON ua.auto=a.id " +
        "WHERE u.id=?1 AND a.license_plate_no=?2 " +
        "AND a.disable=0 ORDER BY a.id DESC ", nativeQuery = true)
    List<Auto> searchByUserAndPlateNo(Long userId, String licensePlateNo);

    @Query(value = "SELECT DISTINCT a.* " +
        "FROM user_auto ua LEFT JOIN user u ON ua.user=u.id LEFT JOIN auto a ON ua.auto=a.id " +
        "WHERE u.id=?1 AND a.license_plate_no=?2 AND a.disable=0 AND a.bill_related = 0 " +
        "AND NOT EXISTS (SELECT 1 FROM user_auto u_a where u_a.auto = a.id and u_a.user <> u.id) ", nativeQuery = true)
    List<Auto> findAutoNotRelatedOtherUser(Long userId, String licensePlateNo);

    @Query(value = "SELECT * FROM auto a WHERE a.id = ?1 AND EXISTS " +
        "(SELECT 1 FROM user_auto ua WHERE ua.auto=a.id And ua.user = ?2)", nativeQuery = true)
    Auto getAutoByIdAndUser(Long autoId, Long userId);

    @Query(value = "select count(Distinct ua.user) from user_auto ua left join auto on ua.auto=auto.id where auto.license_plate_no=?1 AND auto.disable=0 ", nativeQuery = true)
    long countAutoRelatedUser(String licensePlateNo);

    @Query(value = "select DISTINCT a.* from user_auto ua " +
        "LEFT JOIN user u ON ua.user=u.id " +
        "LEFT JOIN auto a ON ua.auto=a.id " +
        "where ua.user=?1 and a.owner=?2 and a.license_plate_no=?3 " +
        "order by a.id desc limit 1 ", nativeQuery = true)
    Auto searchByUserAndOwnerAndPlateNo(Long userId, String owner, String licensePlateNo);

    Auto findFirstByLicensePlateNoAndDisableOrderByUpdateTimeDesc(String licensePlatNo, boolean disable);

    @Query(value = "select a.* from user_auto ua left join auto a on ua.auto=a.id where ua.user=?1 and a.license_plate_no=?2 and a.disable=0 order by a.update_time desc limit 1", nativeQuery = true)
    Auto searchByUserAndPlateNoAndDisableOrderByUpdateTimeDesc(Long userId, String licensePlateNo);

    List<Auto> findAutosByLicensePlateNoAndOwnerAndIdentityAndEngineNoNotNullAndVinNoNotNullAndDisableFalse(String licensePlateNo, String owner, String identity);
    List<Auto> findAutosByLicensePlateNoAndOwnerAndIdentityAndVinNoAndEngineNoNotNullAndDisableFalse(String licensePlateNo, String owner, String identity, String vinNO);
    List<Auto> findAutosByLicensePlateNoAndOwnerAndIdentityAndEngineNoAndVinNoNotNullAndDisableFalse(String licensePlateNo, String owner, String identity, String engineNo);
    List<Auto> findAutosByLicensePlateNoAndOwnerAndIdentityAndEngineNoAndVinNoAndDisableFalse(String licensePlateNo, String owner, String identity, String engineNo, String vinNo);


    @Query(value = "SELECT DISTINCT a.* " +
        "FROM user_auto ua LEFT JOIN user u ON ua.user=u.id LEFT JOIN auto a ON ua.auto=a.id " +
        "WHERE u.id=?1 AND a.license_plate_no=?2  and a.owner=?3 and a.identity=?4 " +
        "AND a.disable=0 ORDER BY a.id DESC ", nativeQuery = true)
    List<Auto> searchByUserAndPlateNoAndIdentityAndOwner(Long userId, String licensePlateNo, String owner, String identity);

    @Query(value = "SELECT DISTINCT a.* " +
        "FROM user_auto ua LEFT JOIN user u ON ua.user=u.id LEFT JOIN auto a ON ua.auto=a.id " +
        "WHERE u.id=?1 AND a.license_plate_no=?2 " +
        "ORDER BY a.id DESC ", nativeQuery = true)
    List<Auto> searchRenewalAuto(Long userId, String licensePlateNo);


    @Query(value = "SELECT id, license_plate_no, applicant_name, quote_record, MIN(expire_date) AS expire_date  FROM ( SELECT a.id, a.license_plate_no, i.applicant_name, i.quote_record, MAX(i.expire_date) AS expire_date FROM auto a JOIN insurance i ON a.id = i.auto WHERE a.license_plate_no IN ( SELECT a.license_plate_no FROM auto a JOIN insurance i ON a.id = i.auto WHERE i.expire_date IN ( ?1 ) ) GROUP BY a.license_plate_no HAVING expire_date IN ( ?1 ) UNION SELECT a.id, a.license_plate_no, i.applicant_name, i.quote_record, MAX(i.expire_date) AS expire_date FROM auto a JOIN compulsory_insurance i ON a.id = i.auto WHERE a.license_plate_no IN ( SELECT a.license_plate_no FROM auto a JOIN compulsory_insurance i ON a.id = i.auto WHERE i.expire_date IN ( ?1 ) ) GROUP BY a.license_plate_no HAVING expire_date IN ( ?1 ) ) a GROUP BY license_plate_no", nativeQuery = true)
    List<Object[]> findRenewalOrderAutoByExpireDate(List<String> expireDates);

    @Query(value = "SELECT u.id AS uid, u.mobile, a.license_plate_no, ci.expire_date ,ci.create_time,ci.id AS ciid, 'compulsory_insurance', po.source_channel, ci.operator,ci.effective_date " +
        "FROM purchase_order po " +
        "LEFT JOIN `user` u ON u.id = po.applicant, order_operation_info ooi, auto a, quote_record qr " +
        "LEFT JOIN compulsory_insurance ci ON qr.id = ci.quote_record " +
        "WHERE po.id = ooi.purchase_order " +
        "AND ( po.order_source_type NOT  IN (6,7)OR po.order_source_type IS NULL) " +
        "AND ooi.current_status = 15 " +
        "AND po.audit = 1 " +
        "AND (po.order_source_type IS NULL OR po.order_source_type NOT IN ( 6, 7)) " +
        "AND po.auto = a.id " +
        "AND qr.id = po.obj_id " +
        "AND (((a.area='310000' AND ci.insurance_company IN(20000,250000) AND date_add(curdate(), INTERVAL 30 DAY) = ci.expire_date) " +
        " OR (a.area='310000' AND ci.insurance_company IN(10000,10500) AND date_add(curdate(), INTERVAL 30 DAY) = ci.expire_date) " +
        " OR (a.area='310000' AND (ci.insurance_company = 25000 )AND (date_add(curdate(), INTERVAL 60 DAY) = ci.expire_date )) " +
        " OR (a.area IN('430100','330100') and (ci.insurance_company IN (10000,10500,20000,250000,25000) )AND (date_add(curdate(), INTERVAL 60 DAY) = ci.expire_date)) " +
        " OR(a.area IN('320500','320100') AND ci.insurance_company IN (10000,10500,20000,250000,25000,40000) AND date_add(curdate(), INTERVAL 40 DAY) = ci.expire_date) " +
        " OR(a.area IN('510100','330200','371000') AND date_add(curdate(), INTERVAL 30 DAY) = ci.expire_date) " +
        " OR(a.area='370100' AND (ci.insurance_company IN (10000,10500,20000,250000,25000,40000) )AND (date_add(curdate(), INTERVAL 30 DAY) = ci.expire_date)) " +
        " OR(a.area IN('330400','330300') AND (date_add(curdate(), INTERVAL 60 DAY) = ci.expire_date))) " +
        " OR  ((ci.expire_date = date_add(curdate(), INTERVAL 90 DAY) " +
        " AND((a.area IN ('310000','430100','330100') AND ci.insurance_company NOT IN(20000,250000,10000,10500,25000)) " +
        " OR (a.area IN ('320500','320100','370100') AND ci.insurance_company NOT IN(20000,250000,10000,10500,25000,4000)) " +
        " OR (a.area NOT IN ('310000','430100','330100','320500','320100','370100','510100','330200','371000','330300','330400')))))) " +
        " GROUP BY a.id", nativeQuery = true)
    List<Object[]> renewalCompulsoryInsurance();

    @Query(value = " SELECT u.id AS uid, u.mobile, a.license_plate_no, i.expire_date ,i.create_time,i.id AS iid, 'insurance', po.source_channel, i.operator,i.effective_date " +
        " FROM purchase_order po   " +
        " LEFT JOIN `user` u ON u.id = po.applicant, order_operation_info ooi, auto a, quote_record qr   " +
        " LEFT JOIN insurance i ON qr.id = i.quote_record   " +
        " WHERE po.id = ooi.purchase_order   " +
        " AND ( po.order_source_type NOT IN (6,7)OR po.order_source_type IS NULL)   " +
        " AND ooi.current_status = 15   " +
        " AND po.audit = 1   " +
        " AND (po.order_source_type IS NULL OR po.order_source_type NOT IN ( 6, 7))   " +
        " AND po.auto = a.id   " +
        " AND qr.id = po.obj_id   " +
        " AND ((a.area='310000' AND i.insurance_company IN(20000,250000) AND date_add(curdate(), INTERVAL 65 DAY) = i.expire_date)    " +
        " OR (a.area='310000' AND (i.insurance_company = 25000)AND (date_add(curdate(), INTERVAL 60 DAY) = i.expire_date))   " +
        " OR (a.area IN('430100','330100') and ( i.insurance_company IN (10000,10500,20000,250000,25000))AND (date_add(curdate(), INTERVAL 60 DAY) = i.expire_date))   " +
        " OR (a.area='350000' AND i.insurance_company IN (10000,10500,20000,250000,25000,40000) AND date_add(curdate(), INTERVAL 30 DAY) = i.expire_date)    " +
        " OR(a.area IN('320500','320100') AND ( i.insurance_company IN (10000,10500,20000,250000,25000,40000))AND (date_add(curdate(), INTERVAL 40 DAY) = i.expire_date))   " +
        " OR(a.area IN('510100','330200','371000') AND (date_add(curdate(), INTERVAL 30 DAY) = i.expire_date))    " +
        " OR(a.area='370100' AND ( i.insurance_company IN (10000,10500,20000,250000,25000,40000))AND ( date_add(curdate(), INTERVAL 30 DAY) = i.expire_date))    " +
        " OR(a.area IN('330400','330300') AND (date_add(curdate(), INTERVAL 60 DAY) = i.expire_date)) " +
        " OR  ((i.expire_date = date_add(curdate(), INTERVAL 90 DAY) " +
        " AND((a.area IN ('310000') AND i.insurance_company NOT IN(20000,250000,25000)) " +
        " OR (a.area IN ('430100','330100') AND i.insurance_company NOT IN(20000,250000,10000,10500,25000)) " +
        " OR (a.area IN ('350000','320500','320100','370100') AND i.insurance_company NOT IN(20000,250000,10000,10500,25000,4000)) " +
        " OR (a.area NOT IN ('310000','430100','350000','320500','320100','330100','370100','510100','330200','371000','330400','330300')))))) " +
        " GROUP BY a.id ", nativeQuery = true)
    List<Object[]> renewalCommercialInsurance();
}


interface AutoRepositoryCustom extends BaseDao<Auto> {
    List<Auto> findOtherAutosByConditions(Auto auto);
}

class AutoRepositoryImpl extends BaseDaoImpl<Auto> implements AutoRepositoryCustom{

    @Override
    public List<Auto> findOtherAutosByConditions(Auto auto) {
        DetachedCriteria detachedCriteria = createDetachedCriteria();
        detachedCriteria.add(Restrictions.eq("licensePlateNo", auto.getLicensePlateNo()));
        detachedCriteria.add(Restrictions.eq("owner", auto.getOwner()));

        if(StringUtils.isNotBlank(auto.getIdentity())){
            detachedCriteria.add(Restrictions.eq("identity", auto.getIdentity()));
        }

        if(StringUtils.isNotBlank(auto.getEngineNo())){
            detachedCriteria.add(Restrictions.eq("engineNo", auto.getEngineNo()));
        }

        if(StringUtils.isNotBlank(auto.getVinNo())){
            detachedCriteria.add(Restrictions.eq("vinNo", auto.getVinNo()));
        }

        detachedCriteria.add(Restrictions.eq("disable",false));
        detachedCriteria.addOrder(Order.desc("identity"));
        detachedCriteria.addOrder(Order.desc("vinNo"));
        detachedCriteria.addOrder(Order.desc("updateTime"));
        return find(detachedCriteria);
    }
}
