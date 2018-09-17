package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.OrderType;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.SimpleOrderResult;
import com.cheche365.cheche.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**sv
 * Created by zhengwei on 3/1/16.
 */
public interface WebPurchaseOrderRepository extends PagingAndSortingRepository<PurchaseOrder, Long>, JpaSpecificationExecutor<PurchaseOrder> {

    PurchaseOrder findFirstByOrderNo(String purchaseOrderNo);

    PurchaseOrder findFirstByOrderNoAndTypeAndApplicant(String purchaseOrderNo, OrderType orderType, User user);

    PurchaseOrder findFirstByApplicantAndObjId(User user, Long objId);

    @Query(value = "SELECT po.* FROM purchase_order po where  po.audit=1 and po.order_no = ?1 and po.type in (?2) and po.applicant = ?3  limit 1", nativeQuery = true)
    PurchaseOrder findFirstByOrderNoAndOrderTypesAndApplicant(String orderNo, List<OrderType> orderTypes, User user);

    @Query(value = "select * from purchase_order where source_channel=?1 and audit=1 order by id desc limit 10", nativeQuery = true)
    List<PurchaseOrder> getTop10ByChannel(long channelId);

    @Query(value = " select po.* from purchase_order po,insurance i where i.quote_record = po.obj_id and po.status = 5 and  po.audit=1 " +
        " and po.applicant = ?1 and i.insurance_company = ?2 and i.expire_date >= now() and (po.create_time < '2017-06-01' or (po.create_time > '2017-07-14' and po.create_time < '2018-09-05')) order by po.id desc ", nativeQuery = true)
    List<PurchaseOrder> findFinishedOrdersByApplicantAndCompany(User applicant, InsuranceCompany insuranceCompany);

    @Query(value = "select po from PurchaseOrder po where po.applicant = ?1 and po.sourceChannel in ?2 and po.status in ?3 and po.audit = 1 and po.type = 1 order by po.id desc ")
    Page<PurchaseOrder> findByUserAndChannelsAndStatus(User applicant, List<Channel> channels, List<OrderStatus> status, Pageable pageable);

    @Query(value = "select new com.cheche365.cheche.core.model.SimpleOrderResult(qr, po) from PurchaseOrder po,QuoteRecord qr where po.objId = qr.id and po.applicant = ?1 and po.sourceChannel in ?2 and po.status in ?3 and po.audit = 1 and po.type = 1 order by po.id desc ")
    Page<SimpleOrderResult> findSimplifiedOrders(User applicant, List<Channel> channels, List<OrderStatus> status, Pageable pageable);

    @Query(value = "SELECT count(1) as count, sum(payable_amount) as amount FROM purchase_order po " +
        "WHERE po.`status` in (3,4,5) AND po.source_channel in (?1) AND po.applicant = ?2 " +
        "AND po.create_time >= ?3 AND po.create_time <= ?4 ", nativeQuery = true)
    List<Object[]> sumPaidOrdersByCreateTime(List<Channel> channels, User user, Date beginTime, Date endTime);

    @Query(value = "SELECT count(1) as count, sum(payable_amount) as amount FROM purchase_order po " +
        "WHERE po.`status` in (3,4,5) AND po.source_channel in (?1) AND po.applicant = ?2 ", nativeQuery = true)
    List<Object[]> sumPaidOrdersByChannelAndApplicant(List<Channel> channels, User user);

    @Query(value = "SELECT count(1) as count, sum(payable_amount) as amount FROM purchase_order po " +
        "WHERE po.source_channel in (?1) AND po.applicant = ?2 AND po.`status` = ?3 ", nativeQuery = true)
    List<Object[]> sumByChannelAndApplicantAndStatus(List<Channel> channels, User user, OrderStatus status);

    @Query(value = " select po.* from user_customer uc left join customer_auto ca on uc.customer = ca.customer " +
        "left join purchase_order po on uc.user = po.applicant and ca.auto = po.auto " +
        "where uc.user = ?1 and uc.customer = ?2 ", nativeQuery = true)
    List<PurchaseOrder> findOrdersByCustomer(User user, Long customerId);

    @Query(value = " select po.* from purchase_order po,insurance i,auto a where i.quote_record = po.obj_id and po.auto = a.id and a.license_plate_no = ?1 and po.status = 5 and  po.audit=1 and i.effective_date > now() order by po.id desc ", nativeQuery = true)
    List<PurchaseOrder> findRenewalOrderByLicensePlateNo(String licensePlateNo);

    @Query(value = "(select po.* from insurance i join purchase_order po on i.quote_record = po.obj_id where po.applicant =?1 and po.status =?2 and po.audit=1 and po.source_channel in (?3) and ((i.expire_date<= ?4 and i.expire_date >now()) or (i.expire_date >=?5 and i.expire_date < now()))) " +
        "UNION " +
        "(select po.* from compulsory_insurance ci join purchase_order po on ci.quote_record = po.obj_id where po.applicant =?1 and po.status =?2 and po.audit=1 and po.source_channel in (?3) and ((ci.expire_date<= ?4 and ci.expire_date >now()) or (ci.expire_date >=?5 and ci.expire_date < now())))" ,nativeQuery = true)
    List<PurchaseOrder> findAllByApplicantAndStatusAndChannel(User user,OrderStatus orderStatus,List<Channel> channels,Date beforeDate,Date afterDate);

    @Query(value = "SELECT po.* from purchase_order po join insurance i on po.obj_id = i.quote_record where po.applicant= ?1 and  po.auto in (?2) and ( po.`status`= 3 or po.`status`=5 ) and i.expire_date >?3", nativeQuery = true)
    List<PurchaseOrder> findRenewaledInsuranceOrder(User user,List<Auto> autos,Date expireDate);

    @Query(value = "SELECT po.* from purchase_order po join compulsory_insurance ci on po.obj_id = ci.quote_record where po.applicant= ?1 and po.auto in (?2) and ( po.`status`= 3 or po.`status`=5 ) and ci.expire_date >?3", nativeQuery = true)
    List<PurchaseOrder> findRenewaledCiOrder(User user,List<Auto> autos,Date expireDate);
}
