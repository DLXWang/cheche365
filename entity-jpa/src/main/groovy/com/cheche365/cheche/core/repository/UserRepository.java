package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends UserRepositoryCustom, PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findById(Long id);

    List<User> findByName(String name);

    Page<User> findAll(Specification<User> specification, Pageable pageable);

    User findFirstByMobile(String mobile); //动态调用，误删

    @Query(value = "select * from user where mobile=?1 and audit=1 order by update_time desc limit 1 ", nativeQuery = true)
    User findByMobile(String mobile);

    @Query(value = "select u.* from user u where u.mobile=?1", nativeQuery = true)
    List<User> findUsersByMobile(String mobile);

    @Query(value = "SELECT * FROM user WHERE mobile = ?1 AND bound = 1 ORDER BY id DESC LIMIT 1", nativeQuery = true)
    User findByMobileAndBound(String mobile);

    @Query(value = "" +
            "select u.id, u.mobile,wc.open_id,u.create_time,c.name,u.register_ip,uli.last_login_time,a.id as aid,a.license_plate_no " +
            "from user u " +
            "left join user_auto ua " +
            "on ua.user = u.id " +
            "left join auto a " +
            "on ua.auto = a.id " +
            "left join user_login_info uli " +
            "on u.id = uli.user " +
            "left join wechat_user_info wi " +
            "on  wi.user = u.id " +
            "left join wechat_user_channel wc " +
            "on wc.wechat_user_info = wi.id " +
            "left join channel c  " +
            "on u.register_channel = c.id " +
            "where 1=1 " +
            "and u.id in(select t.id from (select distinct(uu.id) from user uu left join user_auto uuaa on  uu.id = uuaa.user left join auto aa on  aa.id = uuaa.auto where 1=1 and  aa.license_plate_no like ?1% and aa.disable =0 order by uu.create_time desc limit ?2, ?3 )t) " +
            "order by u.create_time desc,a.id asc", nativeQuery = true)
    List<Object[]> findUserInfosByLicensePlateNo(String keyword, int startIndex, int pageSize);


    @Query(value = "" +
            "select u.id, u.mobile,wc.open_id,u.create_time,c.name,u.register_ip,uli.last_login_time,a.id as aid ,a.license_plate_no " +
            "from user u " +
            "left join user_auto ua " +
            "on  ua.user = u.id " +
            "left join auto a " +
            "on  ua.auto = a.id " +
            "left join user_login_info uli " +
            "on  u.id = uli.user " +
            "left join wechat_user_info wi " +
            "on  wi.user = u.id " +
            "left join wechat_user_channel wc " +
            "on  wc.wechat_user_info = wi.id " +
            "left join channel c  " +
            "on u.register_channel = c.id " +
            "where 1=1 " +
            "and (a.disable is null or a.disable = false ) " +
            "and  u.id in( select t.id from " +
            "(select id from user ut where 1=1 and (?1 or ut.mobile like ?2%) order by ut.create_time desc limit ?3, ?4)t) " +
            "order by u.create_time desc,a.id asc", nativeQuery = true)
    List<Object[]> findUserInfoAllOrByMobile(boolean isMobileNull, String keyword, int startIndex, int pageSize);

    @Query(value = "select count(distinct(uu.id)) from user uu left join user_auto uuaa on  uu.id = uuaa.user left join auto aa on  aa.id = uuaa.auto where 1=1 and  aa.license_plate_no like ?1% and aa.disable =0", nativeQuery = true)
    Long countByLicensePlateno(String keyword);

    @Query(value = "select count(ut.id) from user ut where 1=1 and (?1 or ut.mobile like ?2%)", nativeQuery = true)
    Long countAllOrByMobile(boolean isMobileNull, String keyword);

    @Query(value = "SELECT u.id,u.mobile,u.create_time,c.name,u.register_ip,uli.last_login_time FROM user u " +
            "LEFT JOIN user_login_info uli ON u.id = uli.user LEFT JOIN channel c ON u.register_channel = c.id " +
            "WHERE 1 = 1 AND u.id IN (SELECT t.id FROM " +
            "        (SELECT id FROM user ut WHERE 1 = 1 AND (?1 OR ut.mobile LIKE ?2%) ORDER BY ut.create_time DESC LIMIT ?3,?4) t) " +
            "ORDER BY u.create_time DESC", nativeQuery = true)
    List<Object[]> findUserInfoByMobile(boolean isMobileNull, String keyword, int startIndex, int pageSize);

    User findFirstByOrderById();

    // 只有手机号，无预约、无拍照、无下过订单、无领过优惠券、已绑定手机号
    @Query(value = "select u.* from user u where char_length(u.mobile) = 11 and u.bound = 1 and u.audit = 1 and u.id > ?1" +
            " and (u.user_type is null or u.user_type = 1)" +
            " and not exists ( select 1 from appointment_insurance ai where ai.user = u.id and ai.id > ?2)" +
            " and not exists ( select 1 from quote_photo qp where qp.user = u.id and qp.id > ?3)" +
            " and not exists ( select 1 from purchase_order po where po.audit=1 and po.applicant = u.id and po.id > ?4)" +
            " and not exists ( select 1 from gift gf where gf.applicant = u.id and gf.id > ?5)" +
            " and not exists ( select 1 from marketing_success ms where ms.mobile = u.mobile and ms.id > ?6)" +
            " and not exists ( select 1 from tel_marketing_center tmc1 where tmc1.user=u.id )" +
            " and not exists ( select 1 from tel_marketing_center tmc2 where tmc2.mobile=u.mobile )" +
            " and u.register_channel not in (?7)" +
            " order by u.id" +
            " limit 0,1000", nativeQuery = true)
    List<User> getRegisterNoOperationUsers(Long maxUserId, Long maxAppointmentInsuranceId, Long maxQuotePhotoId, Long maxPurchaseOrderId,
                                           Long maxGiftId, Long maxMarketingSuccessId, List<Channel> channels);

    @Query(value = "SELECT source_id,count(*) FROM `user` " +
            "WHERE source_type =1 AND source_id in(?1) GROUP BY source_id", nativeQuery = true)
    List<Object[]> findUserRegister(List<String> sourceIds);

    @Query(value = "SELECT source_id,count(*) FROM `user` " +
            "WHERE source_type =1 AND source_id in(?1)" +
            "AND create_time BETWEEN ?2 AND ?3 GROUP BY source_id", nativeQuery = true)
    List<Object[]> findUserRegister(List<String> sourceIds, Date startTime, Date endTime);

    @Query(value = "select u.mobile from user u where u.mobile in (?3) and exists" +
            " (select p.id from purchase_order p where p.applicant = u.id and p.create_time between ?1 and ?2 )", nativeQuery = true)
    List<String> findOrderedMobileList(Date startTime, Date endTime, List<String> userIds);

    //统计前一天的微车用户注册
    @Query(value = "SELECT count(u.id) " +
            "FROM user u JOIN channel c ON u.register_channel= c.id " +
            "WHERE u.mobile is not null and u.register_channel in (?3) " +
            "AND u.create_time BETWEEN ?1 AND ?2", nativeQuery = true)
    Long findCountNewUser(Date startDate, Date endDate, List<Long> arg);

    User findFirstByIdentity(String identity);


    @Query(value = "SELECT  " +
        "u.*  " +
        "FROM  " +
        "channel_agent_purchase_order_rebate capo,  " +
        "channel_agent ca,  " +
        "`user` u  " +
        "WHERE  " +
        "capo.channel_agent = ca.id  " +
        "AND ca.`user` = u.id  " +
        "AND capo.purchase_order = ?1 order by ca.agent_level desc limit 1,1", nativeQuery = true)
    User findInviterByPurchaseOrderId(Long id);

    @Query(value = "SELECT  " +
        "u.*  " +
        "FROM  " +
        "channel_agent_purchase_order_rebate capo,  " +
        "channel_agent ca,  " +
        "`user` u  " +
        "WHERE  " +
        "capo.channel_agent = ca.id  " +
        "AND ca.`user` = u.id  " +
        "AND capo.purchase_order = ?1 order by ca.agent_level desc limit 2,1", nativeQuery = true)
    User findTopInviterByByPurchaseOrderId(Long id);

}

interface UserRepositoryCustom extends BaseDao<User> {
    com.cheche365.cheche.core.repository.Page<String> findUserMobileList(
            com.cheche365.cheche.core.repository.Page page, String sql, Object[] parameters);

    com.cheche365.cheche.core.repository.Page<User> findUserList(
            com.cheche365.cheche.core.repository.Page page, String sql, Object[] parameters);

    Integer findUserMobileCount(String sql, Object[] parameters);
}

class UserRepositoryImpl extends BaseDaoImpl<User> implements UserRepositoryCustom {
    @Override
    public com.cheche365.cheche.core.repository.Page<String> findUserMobileList(
            com.cheche365.cheche.core.repository.Page page, String sql, Object[] parameters) {
        return super.findBySql(page, sql, parameters);
    }

    @Override
    public com.cheche365.cheche.core.repository.Page<User> findUserList(
            com.cheche365.cheche.core.repository.Page page, String sql, Object[] parameters) {
        return super.findBySql(page, sql, parameters);
    }

    @Override
    public Integer findUserMobileCount(String sql, Object[] parameters) {
        org.hibernate.Query query;
        if (parameters == null || parameters.length == 0) {
            query = super.createSqlQuery(sql);
        } else {
            query = super.createSqlQuery(sql, parameters);
        }
        List<Object> list = query.list();
        if (list.size() > 0) {
            return Integer.valueOf(list.get(0).toString());
        } else {
            return new Integer(list.size());
        }
    }

}
