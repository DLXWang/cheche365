package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.agent.AgentLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface PurchaseOrderRepository extends PagingAndSortingRepository<PurchaseOrder, Long>, JpaSpecificationExecutor<PurchaseOrder>, PurchaseOrderRepositoryCustom {

    PurchaseOrder findFirstByOrderNo(String purchaseOrderNo);

    PurchaseOrder findFirstByTypeAndObjId(OrderType type, Long objId);

    List<PurchaseOrder> findByDeliveryAddress(Address deliveryAddress);

    @Query(value = "SELECT po.* FROM purchase_order po where po.status in (?1) and po.applicant = ?2 and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and ( " +
            "exists (select 1 from insurance i where i.quote_record = po.obj_id and i.policy_no is not null) " +
            "or " +
            "exists (select 1 from compulsory_insurance ci where ci.quote_record = po.obj_id and ci.policy_no is not null) " +
            ") order by id desc", nativeQuery = true)
    List<PurchaseOrder> findEffectiveOrdersByStatusAndApplicant(List<String> Status, Long userId);

    @Query(value = "SELECT po.* FROM purchase_order po where po.status in (?1) and po.applicant = ?2 and  (po.audit=1 or po.audit=10 or po.audit=20) order by id desc", nativeQuery = true)
    List<PurchaseOrder> findUnOrdersByStatusAndApplicant(List<String> Status, Long userId);

    @Query(value = "FROM PurchaseOrder po WHERE po.status in (?1) and po.applicant = ?2 and po.sourceChannel in (?3) and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and ( " +
            "exists (select 1 from Insurance i where i.quoteRecord = po.objId and i.policyNo is not null) " +
            "or " +
            "exists (select 1 from CompulsoryInsurance ci where ci.quoteRecord = po.objId and ci.policyNo is not null) " +
            ") ORDER BY po.createTime DESC ")
    Page<PurchaseOrder> findEffectiveOrdersByStatusAndApplicant(List<OrderStatus> status, User user, List<Channel> channels, Pageable pageable);


    @Query(value = "select * from purchase_order po where po.obj_id=?1 and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 ", nativeQuery = true)
    PurchaseOrder findByQuoteRecordId(Long quoteRecordId);


    @Query(value = "select count(1) from purchase_order p where (p.operator is null or p.operator = ?1) and (p.status = 1 or p.status = 3 or p.status = 7) and  (p.audit=1 or p.audit=10 or p.audit=20)  and p.type = 1 and p.update_time is null", nativeQuery = true)
    long findNewOrderCount(Long operator);

    @Query(value = "select count(1) from purchase_order p, auto a, address d where p.auto = a.id and p.delivery_address = d.id and (p.operator is null or p.operator = ?3) and (p.status = 1 or p.status = 3 or p.status = 7) and  (p.audit=1 or p.audit=10 or p.audit=20)   and p.type = 1 and p.update_time is null and (d.mobile like %?1% or a.owner like %?2%)", nativeQuery = true)
    long findNewOrderCountByUser(String mobile, String name, Long operator);

    @Query(value = "select * from purchase_order p where p.channel in (5,6) and  (p.audit=1 or p.audit=10 or p.audit=20)  and p.type = 1", nativeQuery = true)
    List<PurchaseOrder> findOfflinePaymentOrders();

    @Query(value = "select p.* from purchase_order p where p.channel in (1,2,3,4) and  (p.audit=1 or p.audit=10 or p.audit=20)  and p.type = 1", nativeQuery = true)
    List<PurchaseOrder> findOnlinePaymentOrders();


    @Query(value = " select po.* from purchase_order po, payment pay, purchase_order_gift pg,gift g " +
            " where po.id = pay.purchase_order " +
            " and pg.purchase_order = po.id " +
            " and pg.gift = g.id " +
            " and exists (select 1 from marketing_success m where m.marketing_id = ?1 and g.source_type=2 and g.source= m.id) " +
            " and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and po.applicant=?2 " +
            " and pay.channel <=7 " +
            " and pay.status=2 ", nativeQuery = true)
    List<PurchaseOrder> findPaidOrdersByUserAndMarketing(Long marketingId, Long userId);


    PurchaseOrder findFirstByApplicantAndType(User user, OrderType orderType);

    @Query(value = "select sum(po.paid_amount) from purchase_order po, payment pay, user applicant where pay.purchase_order = po.id and po.applicant = applicant.id " +
            "and case when ?1 = 1 then po.company_activity is null and applicant.id in (select id from user where user_type = 1) " +
            "when ?1 = 2 then po.company_activity is null and applicant.id in (select id from user where user_type = 2) " +
            "when ?1 = 3 then po.company_activity is not null " +
            "when ?1 = 4 then po.order_source_type = 1 " +
            "else 1 = 1 end " +
            "and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and po.status in (3, 4, 5) and po.channel in (1, 2, 3, 4, 5, 6, 7) and pay.channel in (1, 2, 3, 4, 5, 6, 7) and pay.status = 2 " +
            "order by po.id desc", nativeQuery = true)
    Double getSumPaidAmount(int type);

    @Query(value = "select sum(po.paid_amount) from purchase_order po, payment pay, user applicant where pay.purchase_order = po.id and po.applicant = applicant.id " +
            "and case when ?1 = 1 then po.company_activity is null and applicant.id in (select id from user where user_type = 1) " +
            "when ?1 = 2 then po.company_activity is null and applicant.id in (select id from user where user_type = 2) " +
            "when ?1 = 3 then po.company_activity is not null " +
            "when ?1 = 4 then po.order_source_type = 1 " +
            "else 1 = 1 end " +
            "and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and po.status in (3, 4, 5) and po.channel in (1, 2, 3, 4, 7) and pay.channel in (1, 2, 3, 4, 7) and pay.status = 2 " +
            "order by po.id desc", nativeQuery = true)
    Double getOnLineSumPaidAmount(int type);

    @Query(value = "select count(po.id) from purchase_order po, payment pay, user applicant where pay.purchase_order = po.id and po.applicant = applicant.id " +
            "and case when ?1 = 1 then po.company_activity is null and applicant.id in (select id from user where user_type = 1) " +
            "when ?1 = 2 then po.company_activity is null and applicant.id in (select id from user where user_type = 2) " +
            "when ?1 = 3 then po.company_activity is not null " +
            "when ?1 = 4 then po.order_source_type = 1 " +
            "else 1 = 1 end " +
            "and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and po.status in (3, 4, 5) and po.channel in (1, 2, 3, 4, 5, 6, 7) and pay.channel in (1, 2, 3, 4, 5, 6, 7) and pay.status = 2 " +
            "order by po.id desc", nativeQuery = true)
    Long getSumPaidOrderCount(int type);

    @Query(value = "select sum(po.paid_amount) from purchase_order po, payment pay, user applicant where pay.purchase_order = po.id and po.applicant = applicant.id " +
            "and case when ?1 = 1 then po.company_activity is null and applicant.id in (select id from user where user_type = 1) " +
            "when ?1 = 2 then po.company_activity is null and applicant.id in (select id from user where user_type = 2) " +
            "when ?1 = 3 then po.company_activity is not null " +
            "when ?1 = 4 then po.order_source_type = 1 " +
            "else 1 = 1 end " +
            "and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and po.status in (3, 4, 5) and po.channel in (1, 2, 3, 4, 5, 6, 7) and pay.channel in (1, 2, 3, 4, 5, 6, 7) and pay.status = 2 " +
            "and datediff(date_sub(now(), INTERVAL 1 DAY), pay.update_time) = 0 " +
            "order by po.id desc", nativeQuery = true)
    Double getYesterdaySumPaidAmount(int type);

    @Query(value = "select sum(po.paid_amount) from purchase_order po, payment pay, user applicant where pay.purchase_order = po.id and po.applicant = applicant.id " +
            "and case when ?1 = 1 then po.company_activity is null and applicant.id in (select id from user where user_type = 1) " +
            "when ?1 = 2 then po.company_activity is null and applicant.id in (select id from user where user_type = 2) " +
            "when ?1 = 3 then po.company_activity is not null " +
            "when ?1 = 4 then po.order_source_type = 1 " +
            "else 1 = 1 end " +
            "and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and po.status in (3, 4, 5) and po.channel in (1, 2, 3, 4, 7) and pay.channel in (1, 2, 3, 4, 7) and pay.status = 2 " +
            "and datediff(date_sub(now(), INTERVAL 1 DAY), pay.update_time) = 0 " +
            "order by po.id desc", nativeQuery = true)
    Double getYesterdayOnLineSumPaidAmount(int type);

    @Query(value = "select count(po.id) from purchase_order po, payment pay, user applicant where pay.purchase_order = po.id and po.applicant = applicant.id " +
            "and case when ?1 = 1 then po.company_activity is null and applicant.id in (select id from user where user_type = 1) " +
            "when ?1 = 2 then po.company_activity is null and applicant.id in (select id from user where user_type = 2) " +
            "when ?1 = 3 then po.company_activity is not null " +
            "when ?1 = 4 then po.order_source_type = 1 " +
            "else 1 = 1 end " +
            "and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and po.status in (3, 4, 5) and po.channel in (1, 2, 3, 4, 5, 6, 7) and pay.channel in (1, 2, 3, 4, 5, 6, 7) and pay.status = 2 " +
            "and datediff(date_sub(now(), INTERVAL 1 DAY), pay.update_time) = 0 " +
            "order by po.id desc", nativeQuery = true)
    Long getYesterdaySumPaidOrderCount(int type);

    @Query(value = "select p.* from purchase_order p where  (p.audit=1 or p.audit=10 or p.audit=20)  and p.type = ?1 and p.id > ?2", nativeQuery = true)
    List<PurchaseOrder> findDumpOrder(Long type, Long id);


    List<PurchaseOrder> findByOperatorAndType(InternalUser internalUser, OrderType orderType);

    @Query(value = "select p.* from purchase_order p where p.applicant = ?1 and  (p.audit=1 or p.audit=10 or p.audit=20)  and p.type = 1 ", nativeQuery = true)
    List<PurchaseOrder> searchByApplicant(Long userId);


    @Query(value = " select po.* from purchase_order po " +
            " where (po.audit=1 or po.audit=10 or po.audit=20) and po.type = 1 " +
            " and po.expire_time < ?1 and (po.status = 1 or po.status = 7) " +
            "and not exists (select purchase_order from purchase_order_amend poa " +
            "where poa.purchase_order =po.id and poa.purchase_order_amend_status=1)" +
            " order by po.id desc limit 200 ", nativeQuery = true)
    List<PurchaseOrder> getExpiredOrderListByDate(Date expiredDate);


    @Query(value = "select po.* from purchase_order po, payment pay " +
            "where pay.purchase_order = po.id and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and po.status in (3, 4, 5) " +
            "and po.channel in (1, 2, 3, 4, 5, 6, 7) and pay.channel in (1, 2, 3, 4, 5, 6, 7) and pay.status = 2 " +
            "and case when ?1 = 1 then (pay.update_time <= now()) else (pay.update_time <= now() and pay.update_time >= date_sub(now(), INTERVAL 7 DAY)) end " +
            "order by po.id desc", nativeQuery = true)
    List<PurchaseOrder> findCompletedOrderByWeek(int type);

    @Query(value = "select p.* from purchase_order p, payment pm " +
            "where pm.purchase_order = p.id " +
            "and p.order_source_type = 3 and  (p.audit=1 or p.audit=10 or p.audit=20)  and p.type = 1 " +
            "and p.channel in (1, 2, 3, 4, 5, 6, 7) " +
            "and pm.status = 1 and pm.channel in (1, 2, 3, 4, 5, 6, 7) " +
            "and SUBSTRING_INDEX(timediff(now(), pm.create_time), ':', 1) > 48 " +
            "order by p.id desc", nativeQuery = true)
    List<PurchaseOrder> findDidiTimeoutOrder();

    @Query(value = "select po.* from purchase_order po, payment pay " +
            "where pay.purchase_order = po.id and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and po.status in (3, 4, 5) " +
            "and po.channel in (1, 2, 3, 4, 5, 6, 7) and pay.channel in (1, 2, 3, 4, 5, 6, 7) and pay.status = 2 " +
            "order by po.id desc", nativeQuery = true)
    List<PurchaseOrder> findCompletedOrderByDay();

    @Query(value = "SELECT * FROM purchase_order po,user u WHERE   (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 AND po. STATUS IN (3, 4, 5) AND u.mobile = ?1 AND u.id = po.applicant AND po.update_time BETWEEN DATE_SUB(NOW(), INTERVAL 7 DAY) AND NOW() GROUP BY po.applicant", nativeQuery = true)
    List<PurchaseOrder> getPurchaseOrdersOfOrderedByMobile(String mobile);

    // 运营部门成交订单
    @Query(value = "select po.* from purchase_order po, user ur ,payment p " +
            "where po.applicant = ur.id and p.purchase_order=po.id " +
            "and (po.audit=1 or po.audit=10 or po.audit=20) " +
            "and (ur.user_type is null or ur.user_type = 1) " +
            "and po.type = 1 and po.status in(3,5) " +
            "and p.update_time between ?1 and ?2 " +
            "and p.payment_type=1 and p.status=2 " +
            "order by po.id desc", nativeQuery = true)
    List<PurchaseOrder> findCompletedOrderByCreateTimeBetween(Date startDate, Date endDate);

    Long countByOrderNo(String orderNo);

    @Query(value = "SELECT COUNT(*) from purchase_order p WHERE  (p.audit=1 or p.audit=10 or p.audit=20)  and p.applicant = ?1 and status in (3,4,5)", nativeQuery = true)
    Integer findOrderCountByUserId(String userId);

    @Query(value = "SELECT sum(p.paid_amount) from purchase_order p WHERE  (p.audit=1 or p.audit=10 or p.audit=20)  and p.applicant = ?1 and status in (3,4,5)", nativeQuery = true)
    Double findSumMoneyByUserId(String userId);

    // 半小时未支付订单
    @Query(value = "select po.* from purchase_order po, user ur " +
            "where po.applicant = ur.id " +
            "and (po.audit=1 or po.audit=10 or po.audit=20) " +
            "and (ur.user_type is null or ur.user_type = 1) " +
            "and po.id >?3 and po.status in(1,2) " +
            "and po.create_time between ?1 and ?2 " +
            "order by po.id desc", nativeQuery = true)
    List<PurchaseOrder> findUnPayOrderByCreateTimeBetween(Date startDate, Date endDate, int id);

    @Query(value = "select po.* from purchase_order po where po.applicant = ?1 and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = ?2 limit 1", nativeQuery = true)
    PurchaseOrder findByUserAndNewYearOrderType(Long userId, Long orderTypeId);

    @Query(value = "select * from purchase_order where type=?1 and audit=1 and status in (3,5) and update_time >= ?2", nativeQuery = true)
    List<PurchaseOrder> findByTypeAndStatusAndUpdateTimeAfter(OrderType orderType, Date date);

    @Query(value = "select po.applicant from purchase_order_gift pog, purchase_order po, gift gi where pog.purchase_order = po.id and pog.gift = gi.id and po.applicant = gi.applicant and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 and gi.gift_type = ?1", nativeQuery = true)
    List<BigInteger> findInsuranceOrderUserForNewYearGift(Long giftTypeId);

    @Query(value = "SELECT po.order_no FROM purchase_order po WHERE po.create_time BETWEEN ?1 AND ?2 ORDER BY po.order_no DESC LIMIT 1", nativeQuery = true)
    String findLastOrderNo(Date startCreateTime, Date endCreateTime);


    @Query(value = "select distinct us.mobile from purchase_order po, user us, auto au, address address " +
            "where po.applicant = us.id and po.auto = au.id and po.delivery_address = address.id " +
            "and po.type = 1 and po.status in (3,4,5) and (us.user_type <> 2 or us.user_type is null) " +
            "and char_length(us.mobile) = 11 and au.owner = address.name and SUBSTRING(au.identity, 11, 4) = ?1", nativeQuery = true)
    List<String> findMemberBirthdayWishedUserMobile(String currentDate);

    @Query(value = "select * from purchase_order where source_channel=15 and date(create_time) = curdate() and auto=?1 and applicant = ?2 ORDER BY order_no DESC LIMIT 1 ", nativeQuery = true)
    PurchaseOrder findBaiduCurdatePurchaseOrderByAutoAndApplicant(Auto auto, User applicant);

    // 需跟进订单
    @Query(value = "select po.* from purchase_order po, user ur " +
            "where po.applicant = ur.id " +
            "and (ur.user_type is null or ur.user_type = 1) " +
            "and po.status in (1,6,7) and po.type=1 and po.audit=1 " +
            "and po.create_time between ?1 and ?2 and po.source_channel not in(?3)", nativeQuery = true)
    List<PurchaseOrder> getYesterdayFollowUpOrders(Date startDate, Date endDate, List<Long> channelIds);

    @Query(value = "select po.id from purchase_order po where po.obj_id = ?1 order by po.create_time desc limit 1", nativeQuery = true)
    Long findFirstIdByObjId(Long objId);

    PurchaseOrder findFirstByObjIdOrderByCreateTimeDesc(Long objId);

    // 电销系统未支付订单
    @Query("select po from PurchaseOrder po, User ur where po.applicant = ur.id and (ur.userType is null or ur.userType.id = 1) and po.id >= ?1 and po.status in (?2) and po.type = ?3 and po.audit = 1 and po.createTime <= ?4 and po.sourceChannel not in (?5) order by po.id")
    Page<PurchaseOrder> findUnPayOrderList(Long previousOrderId, List<OrderStatus> status, OrderType type, Date maxCreateTime, List<Channel> channelList, Pageable pageable);

    @Query(value = "select po.* from purchase_order po, quote_record qr where po.obj_id=qr.id and po.audit=1 and po.type = 1 and po.status = 5 and po.channel in (1,2,3,4,13,14) and qr.insurance_company = ?1 and po.id not in(?2) and po.create_time <= '2016-05-08 00:00:00' order by po.create_time DESC LIMIT 1", nativeQuery = true)
    PurchaseOrder findTempDataNotIn(Long insuranceCompanyId, Set<Long> existIds);

    @Query(value = "select po.* from purchase_order po, quote_record qr where po.obj_id=qr.id and po.audit=1 and po.type = 1 and po.status = 5 and po.channel in (1,2,3,4,13,14) and qr.insurance_company = ?1 and po.create_time <= '2016-05-08 00:00:00' order by po.create_time DESC LIMIT 1", nativeQuery = true)
    PurchaseOrder findTempData(Long insuranceCompanyId);

    @Query("select po from PurchaseOrder po where po.audit = 1 and po.type = 1 and po.status = ?1 and po.updateTime between ?2 and ?3 order by po.id")
    Page<PurchaseOrder> findPageDataByStatusAndTime(OrderStatus status, Date startTime, Date endTime, Pageable pageable);


    @Query("select ooi.purchaseOrder from com.cheche365.cheche.core.model.OrderOperationInfo ooi where  ooi.purchaseOrder.audit = 1 and ooi.purchaseOrder.type = 1 and ooi.currentStatus = ?1 and ooi.purchaseOrder.updateTime between ?2 and ?3 ")
    Page<PurchaseOrder> findPageDataByCurrentStatusAndTime(OrderTransmissionStatus status, Date startTime, Date endTime, Pageable pageable);


    @Query(value = "select id from purchase_order where create_time <= ?1 order by id desc limit 1", nativeQuery = true)
    Long findMaxIdByTime(Date createTime);

    @Query(value = "select order_no from purchase_order where create_time >= ?1 and create_time <= ?2 order by id desc limit 1", nativeQuery = true)
    String findMaxOrderNoByTime(Date startTime, Date endTime);

    @Query(value = "select po.* from purchase_order po , payment p where po.id = p.purchase_order and p.thirdparty_payment_no=?1 ", nativeQuery = true)
    PurchaseOrder findByThirdPaymentNo(String thirdPaymentNo);

    @Query(value = "SELECT a.* FROM `purchase_order` a, (SELECT MAX(po.`id`) `id` FROM `purchase_order` po LEFT JOIN `user_invitation` ui ON po.`applicant` = ui.`invited` WHERE po.`applicant` IN ?1 AND `auto` IS NOT NULL AND po.`source_channel` IN ?2 AND po.`create_time` >= ui.`create_time` AND po.`create_time` <= ?3 AND `status` IN ?4 GROUP BY `auto`) b WHERE a.id = b.id ORDER BY `create_time` DESC", nativeQuery = true)
    List<PurchaseOrder> findAllByTimeAndAutoUnique(List<Long> userIds, List<Long> channelIds, Date endTime, String[] status);

    @Query(value = "select count(distinct po.applicant) from purchase_order po where po.id in (?1)", nativeQuery = true)
    Long getRecommendedUserCount(List<Long> purchaseOrderIdList);

    @Query(value = "SELECT a.* FROM `purchase_order` a, (SELECT MAX(po.`id`) `id` FROM `purchase_order` po LEFT JOIN `user_invitation` ui ON po.`applicant` = ui.`invited` WHERE po.`applicant` IN ?1 AND `auto` IS NOT NULL AND po.`source_channel` IN ?2 AND po.`create_time` >= ui.`create_time` AND `update_time` <= ?3 AND `status` IN ?4 GROUP BY `auto`) b WHERE a.id = b.id ORDER BY `update_time` DESC", nativeQuery = true)
    List<PurchaseOrder> filterRecommendedList(List<Long> userIds, List<Long> channelIds, Date endTime, String[] status);

    @Query(value = " SELECT o.* FROM purchase_order o                                                           " +
            " JOIN quote_record q on o.obj_id = q.id                                                                " +
            " left join (select a.*,IFNULL(al.days,90) as days                                                      " +
            " 			from area a left join area_insurance_time_limit al on a.id = al.area ) r on r.id = q.area   " +
            " left join `user` u on u.id = o.applicant                                                              " +
            " WHERE o.type = 1 AND o.audit = 1 AND o. STATUS IN (?6) AND o.source_channel not in (?5)               " +
            " AND o.create_time BETWEEN IF(?1 is null, ?2, ?1+INTERVAL r.days DAY) AND ?2+INTERVAL r.days DAY       " +
            " AND u.mobile is not null limit ?4 offset ?3                                                           ", nativeQuery = true)
    List<PurchaseOrder> findLastYearUnOrderOrder(Date startDate, Date endDate, int startIndex, int pageSize, List excludeChannels, List includeStatus);

    @Query(value = "select * from purchase_order p where p.applicant =?1 and p.create_time >?2 and p.create_time <?3 ", nativeQuery = true)
    List<PurchaseOrder> findByUserAndCreateTime(User user, Date yesterdayStart, Date yesterdayEnd);

    @Query(value = "select count(*) from purchase_order p where p.delivery_address = ?1 ", nativeQuery = true)
    Integer countPurchaseOrderByAddressId(Long addressId);

    @Query(value = "SELECT ic.name as insuranceCompany,u.name,pu.partner_id,a.license_plate_no,u.mobile," +
            " ci.compulsory_premium,i.premium,po.create_time,aut.model,a.vin_no,a.engine_no,a.enroll_date,qr.expire_date" +
            " FROM purchase_order po                                                                        " +
            " JOIN user u ON u.id = po.applicant                                                            " +
            " JOIN partner_user pu ON pu.user = u.id                                                        " +
            " JOIN auto a ON a.id = po.auto                                                                 " +
            " LEFT JOIN auto_type aut ON aut.id = a.auto_type                                               " +
            " JOIN quote_record qr ON qr.id = po.obj_id                                                     " +
            " JOIN insurance_company ic ON ic.id = qr.insurance_company                                     " +
            " LEFT JOIN insurance i ON i.quote_record = qr.id                                               " +
            " LEFT JOIN compulsory_insurance ci ON ci.quote_record = qr.id                                  " +
            " WHERE po.create_time BETWEEN ?1 AND ?2                                                        " +
            " AND po.source_channel IN (?3) AND pu.partner = ?4 LIMIT ?5 OFFSET ?6                          ", nativeQuery = true)
    List<Object[]> findByCreateTimeAndChannelAndPartner(Date startDate, Date endDate, List<Channel> channelList, ApiPartner partner, Integer pageSize, Integer startIndex);

    @Query(value = "SELECT p.id,ic.name AS company,a.owner,a.license_plate_no,p.order_no,ar.name AS province,ae.name AS city," +
            "         aa.name AS district,ads.street,ads.name AS receiver,ads.mobile,i.create_time,p.paid_amount," +
            "         p.channel,qr.compulsory_premium,qr.auto_tax,qr.premium,i.insured_id_no,a.vin_no,a.engine_no,ci.create_time create_time2,ci.insured_id_no insured_id_no2 " +
            "         FROM purchase_order p " +
            "         LEFT JOIN insurance i ON p.obj_id=i.quote_record " +
            "         LEFT JOIN compulsory_insurance ci ON p.obj_id=ci.quote_record  " +
            "         JOIN address ads ON ads.id =p.delivery_address " +
            "         JOIN insurance_company ic ON i.insurance_company=ic.id OR ci.insurance_company = ic.id " +
            "         JOIN auto a ON a.id=p.auto " +
            "         LEFT JOIN quote_record qr ON qr.id=i.quote_record OR qr.id=ci.quote_record " +
            "         LEFT JOIN area ar ON ar.id=ads.province" +
            "         LEFT JOIN area ae ON ae.id=ads.city" +
            "         LEFT JOIN area aa ON aa.id=ads.district" +
            "         WHERE p.id IN (?1)", nativeQuery = true)
    List<Object[]> findDataByPurchaseOrderId(List<String> ids);


    @Query(value = "select ots.status,count(*) from  purchase_order p join quote_record qr on p.obj_id=qr.id and qr.insurance_company=65000 join order_operation_info ooi on ooi.purchase_order=p.id join order_transmission_status ots on ooi.current_status=ots.id and ots.id in(20,23,1,15) group by ots.id", nativeQuery = true)
    List<Object[]> findHistoryDataByStatus();

    @Query(value = "select ots.status,p.order_no from  purchase_order p join quote_record qr on p.obj_id=qr.id and qr.insurance_company=65000 join order_operation_info ooi on ooi.purchase_order=p.id join order_transmission_status ots on ooi.current_status=ots.id and ots.id in(20,23,1,15) where date(p.update_time) = date_sub(curdate(),interval 1 day)", nativeQuery = true)
    List<Object[]> findDataByStatus();

    @Query(value = "select os.status,count(1) " +
            "from purchase_order po " +
            "join quote_record qr on po.obj_id = qr.id " +
            "join order_status os on os.id=po.status " +
            "where qr.insurance_company = 65000 " +
            "and po.create_time between curdate() and now() " +
            "group by po.status", nativeQuery = true)
    List<Object[]> findDataByPurchaseOrderStatus();

    @Query(value = "select * from purchase_order where order_source_id = ?1 and order_source_type=?2 order by id desc limit 1", nativeQuery = true)
    List<PurchaseOrder> findByOrderSourceId(String sourceId, OrderSourceType sourceType);

    @Query(value = "select * from purchase_order where order_source_id = ?1 order by id desc limit 1", nativeQuery = true)
    List<PurchaseOrder> findPurchaseOrderByOrderSourceId(String orderSourceId);

    @Query(value = "select p.* from purchase_order as p " +
            "join payment pay on pay.purchase_order=p.id " +
            "where p.status=10 " +
            "and p.order_source_type=5 " +
            "and pay.payment_type =4 and pay.status=1 " +
            "and pay.create_time < ?1 ", nativeQuery = true)
    List<PurchaseOrder> findOverdueFanhuaFullRefundByDate(Date createTime);

    @Query(value = "select p.* from purchase_order as p  " +
            "join payment pay on pay.purchase_order=p.id " +
            "and p.order_source_type=5 " +
            "and pay.payment_type =3 and pay.status=1 " +
            "and p.status=1 " +
            "and pay.create_time < ?1 ", nativeQuery = true)
    List<PurchaseOrder> findOverdueFanhuaPartialRefundByDate(Date createTime);

    @Query(value = "SELECT po.*  " +
            "FROM purchase_order po " +
            "JOIN quote_record qr ON po.obj_id = qr.id " +
            "WHERE po.`status`=10  " +
            "AND po.order_source_type=5 " +
            "AND qr.quote_valid_time < now() ", nativeQuery = true)
    List<PurchaseOrder> findQuoteValidTimeOverdue();

    //保单总量 原始保费总额（商业+较强+车船税） 原始保费总额（仅商业险）
    @Query(value = "SELECT count(*),sum(paid_amount),sum(premium) " +
            "FROM purchase_order po " +
            "left JOIN insurance i ON i.quote_record = po.obj_id " +
            "WHERE date(po.create_time) < CURDATE() " +
            "AND po.`status` IN (3,4,5) AND i.insurance_company = 65000", nativeQuery = true)
    List<Object[]> findOrderNumAndPrice();

    //未申请过停驶的车辆数
    @Query(value = "SELECT count(DISTINCT po.id) " +
            "FROM purchase_order po " +
            "LEFT OUTER JOIN daily_insurance di ON di.purchase_order = po.id  AND di.`status` <>1 " +
            "JOIN insurance i ON po.obj_id=i.quote_record  " +
            "WHERE di.purchase_order IS NULL  " +
            "AND po.`status` IN (3,4,5) " +
            "AND i.insurance_company=65000 " +
            "AND date(po.create_time) < CURDATE()", nativeQuery = true)
    BigInteger findUnstoppedNum();

    @Query(value = "SELECT ic.name as insuranceCompany,u.name,pu.partner_id,po.order_no,os.description,a.license_plate_no," +
            " u.mobile,ci.compulsory_premium,i.premium,po.create_time,a.vin_no,a.engine_no,a.enroll_date,qr.expire_date       " +
            " FROM purchase_order po                                                                                          " +
            " JOIN order_status os on po.`status` = os.id                                                                     " +
            " JOIN user u ON u.id = po.applicant                                                                              " +
            " JOIN partner_user pu ON pu.user = u.id                                                                          " +
            " JOIN auto a ON a.id = po.auto                                                                                   " +
            " JOIN quote_record qr ON qr.id = po.obj_id                                                                       " +
            " JOIN insurance_company ic ON ic.id = qr.insurance_company                                                       " +
            " LEFT JOIN insurance i ON i.quote_record = qr.id                                                                 " +
            " LEFT JOIN compulsory_insurance ci ON ci.quote_record = qr.id                                                    " +
            " WHERE (po.create_time BETWEEN ?1 AND ?2 OR po.update_time BETWEEN ?1 AND ?2)                                    " +
            " AND po.`status` in (?3) AND po.source_channel IN (?4) AND pu.partner = ?5                                       ", nativeQuery = true)
    List<Object[]> findByUpdateTimeAndChannelAndPartner(Date startDate, Date endDate, List<OrderStatus> statuslList, List<Channel> channelList, ApiPartner partner);

    @Query(value = "select " +
            "    (pu.partner_id) as '0', po.order_no as '1', po.create_time as '2', os.status as '3', aea.name as '4' , ic.name as '5', u.mobile as '6', wi.nick_name as '7', qr.owner_mobile as '8'," +
            "     if(user_type = 2,'代理','个人' ) as '9' , c1.description as '10', a.license_plate_no as '11', a.vin_no as '12', a.engine_no as '13', a.enroll_date as '14' , aut.seats as '15', aut.code as '16', " +
            "    qsi.value as '17', i.effective_date as '18', i.expire_date as '19', i.policy_no as '20', ci.effective_date as '21' , ci.expire_date as '22' , ci.policy_no as " +
            "    '23' , po.payable_amount as '24', pay.amount as '25', qr.compulsory_premium as '26', qr.auto_tax as '27' , qr.premium as '28', i.damage_premium as '29', i.damage_amount as '30', " +
            "    i.third_party_premium as '31', i.third_party_amount as '32' , i.driver_premium as '33', i.driver_amount as '34', i.passenger_premium as '35', i.passenger_amount as '36', i.theft_premium as '37' , " +
            "    i.theft_amount as '38', i.scratch_premium as '39', i.scratch_amount as '40', i.spontaneous_loss_premium as '41', i.spontaneous_loss_amount as '42' , i.engine_premium as '43', i.engine_amount as " +
            "    '44', a.owner as '45', idt.name as '46', a.identity as '47' , i.applicant_name as '48', '身份证' as '49', ifnull( i.applicant_id_no,a.identity) as '50', i.insured_name as '51', '身份证' as '52' , " +
            "    ifnull(i.insured_id_no,a.identity) as '53' , (pay.id) as '54', (pt.description) as '55' , (pay.amount) as '56', ps.description as '57' , pay.update_time as '58', pc.name as '59', " +
            "    (pay.out_trade_no) as '60', pay.thirdparty_payment_no as '61', ads.name as '62' , ' ' as '63', ads.mobile as '64', ooi.send_time as '65', ooi.send_period as '66', " +
            "    concat(a1.name,a2.name,a3.name,ads.street) as '67' " +
            "from purchase_order po " +
            "join order_status os              on po.status = os.id " +
            "join user u                       on u.id = po.applicant " +
            "left join wechat_user_info wi     on wi.user = u.id " +
            "left join partner_user pu         on pu.user = u.id and pu.partner = 22 " +
            "join auto a                       on a.id = po.auto " +
            "left join auto_type aut           on aut.id = a.auto_type " +
            "join area aea                     on a.area = aea.id " +
            "join channel c1                   on po.source_channel = c1.id " +
            "join quote_record qr              on qr.id = po.obj_id " +
            "left join insurance i             on i.quote_record = qr.id " +
            "left join compulsory_insurance ci on ci.quote_record = qr.id " +
            "join insurance_company ic         on ic.id = qr.insurance_company " +
            "join payment pay                  on pay.purchase_order = po.id and pay.payment_type = 1 " +
            "join payment_type pt              on pay.payment_type = pt.id " +
            "join payment_status ps            on pay.status = ps.id " +
            "join payment_channel pc           on pay.channel = pc.id " +
            "join address ads                  on ads.id = po.delivery_address " +
            "left join quote_supplement_info qsi on qsi.quote_record = qr.id and  qsi.field_path like '%transferDate' " +
            "left join identity_type idt       on a.identity_type = idt.id " +
            "join order_operation_info ooi     on ooi.purchase_order = po.id " +
            "left join area a1                      on ads.province = a1.id " +
            "left join area a2                      on ads.city = a2.id " +
            "left join area a3                      on ads.district = a3.id " +
            "where po.source_channel in (?3) and po.create_time between ?1 and ?2 " +
            "group by po.id " +
            "order by u.id desc", nativeQuery = true)
    List<Object[]> findDatebaoOrders(Date yesterdayStart, Date yesterdayEnd, List datebaoChannelList);

    @Query(value = "select g.quantity,g.gift_amount,g.gift_type,g.gift_content,gt.`name`,g.gift_display,g.unit,            " +
            " po.create_time,c.description as channel,a.`owner`,a.license_plate_no,po.order_no,                                " +
            " IFNULL(qr.compulsory_premium,0) + IFNULL(qr.auto_tax,0) + IFNULL(qr.premium,0) as sumPremium,                    " +
            " qr.compulsory_premium,qr.auto_tax,qr.premium,qr.damage_premium,pc.description as paymentChannel,gt.id,gt.category" +
            " from purchase_order po                                                                                           " +
            " join order_status os on po.`status` = os.id                                                                      " +
            " join channel c on c.id = po.source_channel                                                                       " +
            " join quote_record qr on qr.id = po.obj_id                                                                        " +
            " left join auto a on a.id = po.auto                                                                               " +
            " left join payment_channel pc on pc.id = po.channel                                                               " +
            " left join purchase_order_gift pog on pog.purchase_order = po.id                                                  " +
            " left join gift g on pog.gift = g.id                                                                              " +
            " left join gift_type gt on gt.id = g.gift_type                                                                    " +
            " where po.update_time between ?1 and ?2 and po.`status` in (?3) and po.order_source_type = ?4 order by po.id      ", nativeQuery = true)
    List<Object[]> findFanhuaOrderBillData(Date startDate, Date endDate, List<OrderStatus> statusList, OrderSourceType sourceType);

    @Query(value = "select g.quantity,g.gift_amount,g.gift_type,g.gift_content,gt.`name` as giftType,g.gift_display,             " +
            " g.unit,c.description,a.`name` as areaName,po.order_no,u.mobile as userMobile,au.license_plate_no,                      " +
            " IFNULL(i.insured_name,ci.insured_name),ic.`name` as companyName,qr.compulsory_premium,qr.premium,ad.`name` as linkMan, " +
            " ad.mobile as linkMobile,ct.`name` as city,dt.`name` as district,ad.street,gt.category,pt.`name` as province  " +
            " from purchase_order po                                                                                       " +
            " join channel c on c.id = po.source_channel                                                                   " +
            " join area a on a.id = po.area                                                                                " +
            " join user u on u.id = po.applicant                                                                           " +
            " join auto au on au.id = po.auto                                                                              " +
            " join quote_record qr on qr.id = po.obj_id                                                                    " +
            " join insurance_company ic on qr.insurance_company = ic.id                                                    " +
            " left join insurance i on qr.id = i.quote_record                                                              " +
            " left join compulsory_insurance ci on qr.id = ci.quote_record                                                 " +
            " left join purchase_order_gift pog on pog.purchase_order = po.id                                              " +
            " left join gift g on pog.gift = g.id                                                                          " +
            " left join gift_type gt on gt.id = g.gift_type                                                                " +
            " join address ad on ad.id = po.delivery_address                                                               " +
            " left join area ct on ct.id = ad.city                                                                         " +
            " left join area dt on dt.id = ad.district                                                                     " +
            " left join area pt on pt.id = ad.province                                                                     " +
            " where po.update_time between ?1 and ?2 and po.`status` in (?3) and po.order_source_type = ?4 order by po.id  ", nativeQuery = true)
    List<Object[]> findFanhuaOrderGiftData(Date startDate, Date endDate, List<OrderStatus> statusList, OrderSourceType sourceType);


    @Query(value = "SELECT * FROM purchase_order " +
            "WHERE source_channel IN (54,56) " +
            "AND date(update_time) BETWEEN DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND  DATE_SUB(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
        //"LIMIT 20 ", nativeQuery = true)
    List<PurchaseOrder> findOrderByHaoDaiExcel();


    @Query(value = "select po.create_time,c.description as sourceChannel,a.`owner`,a.license_plate_no,po.order_no," +
            "IFNULL(qr.compulsory_premium,0) + IFNULL(qr.auto_tax,0) + IFNULL(qr.premium,0) as sumPremium " +
            ",qr.compulsory_premium,qr.auto_tax,qr.premium,pc.description,IF(TIMESTAMPDIFF(MONTH, a.enroll_date, now())>9,'否','是') as isNewAuto,qr.damage_premium ,po.id " +
            "from purchase_order po " +
            "join quote_record qr on qr.id = po.obj_id " +
            "join channel c on c.id = po.source_channel " +
            "left join auto a on a.id = po.auto " +
            "left join address ad on ad.id = po.delivery_address " +
            "left join payment_channel pc on pc.id = po.channel " +
            "join order_operation_info ooi on ooi.purchase_order=po.id " +
            "where ooi.confirm_order_date between ?1 " +
            "and ?2 " +
            "and po.`status` = ?3 " +
            "and qr.insurance_company = ?4 " +
            "order by po.id", nativeQuery = true)
    List<Object[]> findAnswernOrderData(Date startDate, Date endDate, OrderStatus status, InsuranceCompany company);


    @Query(value = "select p.* from purchase_order p join quote_record q on q.id=p.obj_id join order_operation_info ooi on ooi.purchase_order=p.id " +
            "where p.status =?1 and ooi.confirm_order_date between ?2 and ?3 and q.insurance_company =?4", nativeQuery = true)
    List<PurchaseOrder> findOrderByInsuranceCompanyAndStatus(OrderStatus status, Date startDate, Date endDate, InsuranceCompany company);

    @Query(value = " select * from purchase_order where status=5 and (audit=1 or audit=10 or audit=20) " +
            "and auto in (select id from auto where license_plate_no=?1) order by create_time desc", nativeQuery = true)
    List<PurchaseOrder> findByLicensePlateNo(String licensePlateNo);

    @Query(value = "select o.* from purchase_order o,quote_record q where o.obj_id = q.id and o.status=?1 and o.sub_status=?2 and q.insurance_company =?3 and o.update_time BETWEEN ?4 AND ?5", nativeQuery = true)
    List<PurchaseOrder> findByStatusAndSubStatusAndInsuranceCompany(OrderStatus status, OrderSubStatus substatus, InsuranceCompany ic, String startDate, String endDate);

    @Query(value = "SELECT po.* FROM purchase_order po, quote_record qr WHERE po.obj_id = qr.id AND po.status = ?1 AND qr.type = ?2 AND po.create_time > ?3", nativeQuery = true)
    List<PurchaseOrder> findByStatusAndQuoteSource(OrderStatus status, QuoteSource quoteSource, Date startTime);

    @Query(value = "SELECT " +
            "po.order_no AS '1', " +
            "u.mobile AS '2'," +
            "a.license_plate_no AS '3'," +
            "i.applicant_name AS '4'," +
            "qr.compulsory_premium AS '5', " +
            "qr.premium AS '6'," +
            "po.create_time AS '7'," +
            "aea. NAME AS '8', " +
            "ic. NAME AS '9'," +
            "po.id as '10'" +
            "FROM " +
            "purchase_order po " +
            "join order_operation_info ooi on ooi.purchase_order = po.id " +
            "JOIN USER u ON u.id = po.applicant " +
            "JOIN auto a ON a.id = po.auto " +
            "JOIN area aea ON a.area = aea.id " +
            "JOIN quote_record qr ON qr.id = po.obj_id " +
            "JOIN insurance_company ic ON ic.id = qr.insurance_company " +
            "JOIN payment pay ON pay.purchase_order = po.id " +
            "AND pay.payment_type = 1 " +
            "LEFT JOIN insurance i ON i.quote_record = qr.id " +
            "WHERE " +
            "ooi.current_status in (13,15) " +
            "and po.create_time between ?1 and ?2 " +
            "AND po.source_channel IN (?3) " +
            "GROUP BY " +
            "po.id " +
            "ORDER BY " +
            "u.id DESC ", nativeQuery = true)
    List<Object[]> findTuhuOrders(Date yesterdayStart, Date yesterdayEnd, List tuhuChannelList);


    @Query(value = "SELECT po.* FROM purchase_order po WHERE audit =1 and (order_source_type is null or order_source_type = 6 or order_source_type = 7 ) and (status = 4 or status = 5 ) and po.obj_id =?1 limit 1", nativeQuery = true)
    PurchaseOrder findSuccessByObjId(String autoId);


    @Query(value = "select * from purchase_order where status=1 and  create_time >= ?1 AND create_time <= ?2", nativeQuery = true)
    List<PurchaseOrder> findUnpayByDate(Date start,Date end);

    @Query(value = "select po.* from channel_agent ca,channel_agent_purchase_order_rebate capor" +
        " join purchase_order po on capor.purchase_order = po.id where ca.id in (?1) and ca.id = capor.channel_agent" +
        " and po.status = 5 and po.applicant = ca.user and YEAR(po.update_time) =YEAR(NOW())", nativeQuery = true)
    List<PurchaseOrder> findAgentOrder(List<Long> channelAgentIds);

    @Query(value = "select po.create_time,c.description as sourceChannel,a.`owner`,a.license_plate_no,po.order_no," +
        "IFNULL(qr.compulsory_premium,0) + IFNULL(qr.auto_tax,0) + IFNULL(qr.premium,0) as sumPremium " +
        ",qr.compulsory_premium,qr.auto_tax,qr.premium,pc.description,IF(TIMESTAMPDIFF(MONTH, a.enroll_date, now())>9,'否','是') as isNewAuto,qr.damage_premium ,po.id " +
        "from purchase_order po " +
        "join quote_record qr on qr.id = po.obj_id " +
        "join channel c on c.id = po.source_channel " +
        "left join auto a on a.id = po.auto " +
        "left join address ad on ad.id = po.delivery_address " +
        "left join payment_channel pc on pc.id = po.channel " +
        "join order_operation_info ooi on ooi.purchase_order=po.id " +
        "where ooi.confirm_order_date between ?1 " +
        "and ?2 " +
        "and po.`status` = ?3 " +
        "and qr.insurance_company = ?4 " +
        "order by po.id", nativeQuery = true)
    List<Object[]> findBaobiaoOrderData(Date startTime, Date endTime, OrderStatus finished5, InsuranceCompany zhongan50000);

    @Query(value = "SELECT IFNULL(i.policy_no,''), IFNULL(ci.policy_no,''),(IFNULL(i.premium, 0) + IFNULL(ci.compulsory_premium, 0)+IFNULL(ci.auto_tax,0)), " +
        "  IFNULL(i.premium,0),IFNULL(ci.compulsory_premium,0),IFNULL(ci.auto_tax,0),i.effective_date as iEffectiveDate,i.expire_date as iExpireDate,ci.effective_date as ciEffectiveDate, " +
        "  ci.expire_date AS ciExpireDate,a.license_plate_no,IFNULL(partner.state,'') AS xiaomi1, " +
        "  a.area,( CASE WHEN i.insured_mobile IS NULL THEN ci.insured_mobile ELSE i.insured_mobile END ) AS insured_mobile, " +
        "  ( CASE WHEN i.insured_name IS NULL THEN ci.insured_name ELSE i.insured_name END ) AS insured_name, " +
        "  ( CASE WHEN i.insured_id_no IS NULL THEN ci.insured_id_no ELSE i.insured_id_no END ) AS insured_id_no1, " +
        "  ( CASE WHEN i.applicant_name IS NULL THEN ci.applicant_name ELSE i.applicant_name END ) AS applicant_name, " +
        "  ( CASE WHEN i.applicant_mobile IS NULL THEN ci.applicant_mobile ELSE i.applicant_mobile END ) AS applicant_mobile, " +
        "  ( CASE WHEN i.applicant_id_no IS NULL THEN ci.applicant_id_no ELSE i.applicant_id_no END ) AS applicant_id_no, " +
        "  payment.update_time as pay_time,IFNULL(partner.state,'') AS xiaomi2,ooi.confirm_order_date,po.paid_amount, " +
        "  (po.payable_amount - po.paid_amount),ic.`name` AS name1,a.`owner`,a.identity,po.order_no,'' as null1, " +
        "  0,3,'' as null2,pc.description,u.mobile mobile1,'' as null3, " +
        "  ( CASE WHEN i.insured_id_no IS NULL THEN ci.insured_id_no ELSE i.insured_id_no END ) AS insured_id_no2, " +
        "  a.vin_no, a.engine_no, a.enroll_date, IFNULL(atp.code,''), " +
        " CONCAT(IFNULL(ar.`name`, '') ,IFNULL(ae.`name`, '') ,IFNULL(aa.`name`, ''),IFNULL(addr.street, '')), " +
        "  addr.`name` AS name2,addr.mobile mobile2, po.create_time,ooi.confirm_order_date confirm_order_date2" +
        " FROM purchase_order po " +
        " LEFT JOIN insurance i ON po.obj_id = i.quote_record " +
        " LEFT JOIN compulsory_insurance ci ON po.obj_id = ci.quote_record " +
        " LEFT JOIN auto a ON po.auto = a.id " +
        " LEFT JOIN order_operation_info ooi ON ooi.purchase_order = po.id " +
        " LEFT JOIN quote_record qr ON qr.id = po.obj_id " +
        " LEFT JOIN insurance_company ic ON ic.id = qr.insurance_company " +
        " LEFT JOIN payment_channel pc ON pc.id = po.channel " +
        " LEFT JOIN auto_type atp ON a.auto_type = atp.id " +
        " LEFT JOIN address addr ON po.delivery_address = addr.id " +
        " LEFT JOIN area ar ON ar.id = addr.province " +
        " LEFT JOIN area ae ON ae.id = addr.city " +
        " LEFT JOIN area aa ON aa.id = addr.district " +
        " LEFT JOIN partner_order partner ON partner.purchase_order_id = po.id " +
        " LEFT JOIN `user` u ON u.id = po.applicant " +
        " LEFT JOIN payment ON payment.purchase_order = po.id AND payment.update_time IS NOT NULL AND payment.`status` = 2 AND payment.payment_type = 1 " +
        " LEFT JOIN channel ON channel.id = po.source_channel " +
        " WHERE channel.`description`  like ('小米车险%') " +
        " AND po.`status` IN (5) " +
        " AND ooi.confirm_order_date BETWEEN ?1 " +
        " AND ?2 " +
        " ORDER BY po.id desc ", nativeQuery = true)
    List<Object[]> getXiaomiData(Date startTime, Date endTime);

    @Query(value = " SELECT IFNULL(count(po.id),0) " +
        " FROM purchase_order po " +
        " LEFT JOIN channel c ON c.id = po.source_channel " +
        " WHERE  c.`description` like ( '小米车险%' ) AND po.`status` = 5" +
        " AND po.update_time > ?1" +
        " AND po.update_time < ?2 ", nativeQuery = true)
    Integer countXiaomiOrderNum(Date startTime, Date endTime);

    @Query(value = " SELECT IFNULL(sum(paid_amount),0) " +
        " FROM purchase_order po " +
        " LEFT JOIN channel c ON c.id = po.source_channel " +
        " WHERE  c.`description` like ( '小米车险%' ) AND po.`status` IN (3,4,5)" +
        " AND po.update_time > ?1" +
        " AND po.update_time < ?2 ", nativeQuery = true)
    Double getXiaomiPaid(Date startTime, Date endTime);

    @Query(value = " select * from purchase_order po LEFT JOIN order_operation_info ooi ON ooi.purchase_order = po.id join order_transmission_status ots on ooi.current_status=ots.id and ots.id in (13,14,15) where ooi.insurance_inputter is NULL and ooi.confirm_order_date BETWEEN ?1 AND ?2 ", nativeQuery = true)
    List<PurchaseOrder> findChebaoyi(Date start,Date end);

    @Query(value = "SELECT count(1) from purchase_order where applicant = ?1 and source_channel in (?2) and status =5 and update_time < ?3", nativeQuery = true)
    Long findBeforeActivityOrder(User user, List<Channel> channels, Date activityBeginTime);
}

interface PurchaseOrderRepositoryCustom extends BaseDao<PurchaseOrder> {
    com.cheche365.cheche.core.repository.Page<PurchaseOrder> findOrdersPageByPartnerParam(com.cheche365.cheche.core.repository.Page<PurchaseOrder> page, String sql, Object[] sqlParam);
}


class PurchaseOrderRepositoryImpl extends BaseDaoImpl<PurchaseOrder> implements PurchaseOrderRepositoryCustom {
    @Override
    public com.cheche365.cheche.core.repository.Page<PurchaseOrder> findOrdersPageByPartnerParam(com.cheche365.cheche.core.repository.Page<PurchaseOrder> page, String sql, Object[] sqlParam) {
        return findBySql(page, sql, PurchaseOrder.class, sqlParam);
    }
}
