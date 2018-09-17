package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by liqiang on 4/9/15.
 */
public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long> {

    Payment findFirstByPurchaseOrder(PurchaseOrder purchaseOrder);

    Payment findFirstByPurchaseOrderAndPaymentTypeOrderByIdDesc(PurchaseOrder purchaseOrder, PaymentType paymentType);

    @Query(value = "select * from payment where purchase_order = ?1 and payment_type <> 7 order by id ", nativeQuery = true)
    List<Payment> findByPurchaseOrder(PurchaseOrder purchaseOrder);

    @Query(value = "select * from payment where purchase_order = ?1 order by id ", nativeQuery = true)
    List<Payment> findAllByPurchaseOrder(PurchaseOrder purchaseOrder);

    Payment findFirstByOutTradeNo(String outTradeNo);

    //查询用户待支付记录
    @Query(value = "select * from payment where purchase_order = ?1 and payment_type in (1,2,7) and status = 1 order by id ", nativeQuery = true)
    List<Payment> findCustomerPendingPayments(PurchaseOrder purchaseOrder);

    //查询已支付支付记录
    @Query(value = "select * from payment where purchase_order = ?1 and payment_type in (1,2) and status = 2 order by id ", nativeQuery = true)
    List<Payment> findPaidPayments(PurchaseOrder purchaseOrder);

    //查询用户待支付，待退款记录
    @Query(value = "select * from payment where purchase_order = ?1 and payment_type in (1,2,3,4) and status <> 4 order by id ", nativeQuery = true)
    List<Payment> findCustomerPayments(PurchaseOrder purchaseOrder);

    @Query(value = "select * from payment where channel = ?1 and purchase_order =?2 and status <> 4 and payment_type <> 7 order by id desc limit 1", nativeQuery = true)
    Payment findFirstByChannelAndPurchaseOrderOrderByIdDesc(PaymentChannel paymentChannel, PurchaseOrder purchaseOrder);

    @Query(value = "select * from payment where channel in ?1 and purchase_order =?2 and status <> 4 and payment_type <> 7 order by id desc limit 1", nativeQuery = true)
    Payment findFirstByChannelInAndPurchaseOrderOrderByIdDesc(List<PaymentChannel> paymentChannels, PurchaseOrder purchaseOrder);

    @Query(value = "select * from payment p where p.purchase_order = ?1 and payment_type <> 7 and exists (select * from payment_channel pc where p.channel = pc.id and pc.customer_pay = ?2) ", nativeQuery = true)
    List<Payment> findByPurchaseOrderAndChannel_CustomerPay(PurchaseOrder purchaseOrder, Boolean customerPay);

    Payment findFirstByThirdpartyPaymentNo(String thirdpartyPaymentNo);

    Payment findByOutTradeNo(String outTradeNo);

    @Query(value = "SELECT p1.* from payment p0 INNER JOIN payment p1 on p0.id = p1.upstream_id where p0.thirdparty_payment_no =?1 and p1.`status`=1 and p1.payment_type in ('3','4');", nativeQuery = true)
    List<Payment> findRefundPaymentByTpn(String thirdpartyPaymentNo);

    /**
     * 查看该订单的该支付渠道的支付或退款的流水号最大的payment
     */
    @Query(value = "select count(id) from payment where purchase_order = ?1 and out_trade_no is not null ", nativeQuery = true)
    int findPaymentOutTradeNo(Long orderNo);

    @Query(value = "select p.* FROM payment p  WHERE p.purchase_order = ?1 and p.payment_type not in (?2) and p.`status` != 4 order by p.id", nativeQuery = true)
    List<Payment> findPaymentInfoByOrderId(Long orderId, List<Long> excludeType);

    @Query(value = "SELECT p.* FROM payment p WHERE p.payment_type in (?3) AND p.`status` in (?2) AND p.purchase_order = ?1", nativeQuery = true)
    List<Payment> findSuccessPaymentsByOrderId(Long orderId, List<Long> status, List<Long> types);

    List<Payment> findByPurchaseOrderAmend(PurchaseOrderAmend purchaseOrderAmend);

    @Query(value = "SELECT p.* FROM payment p WHERE p.purchase_order_amend = ?1 order by channel", nativeQuery = true)
    List<Payment> findByPurchaseOrderAmendOrderByChannel(PurchaseOrderAmend purchaseOrderAmend);

    @Query(value = "select * from payment where purchase_order = ?1 and payment_type = ?2 and status = ?3", nativeQuery = true)
    List<Payment> findByPurchaseOrderAndPaymentTypeAndStatus(Long purchaseOrder, PaymentType paymentType, PaymentStatus paymentStatus);

    List<Payment> findByPurchaseOrderHistoryAndPaymentType(PurchaseOrderHistory purchaseOrderHistory, PaymentType paymentType);

    @Query(value = "select * from payment p where p.payment_type in (?1) and p.status =?2 order by p.id desc ", nativeQuery = true)
    List<Payment> findByPaymentTypesAndStatus(List<PaymentType> refundTypes, PaymentStatus refundFaild);

    //  部分退款订单
    @Query(value = "select p1.itp_no ,p.* from purchase_order po, payment p " +
        "join payment p1 on p.upstream_id = p1.id " +
        "where p.purchase_order = po.id " +
        "and p.channel in (21,22,23,24,25) " +
        "and po.update_time between ?1 and ?2 " +
        "and p.payment_type = 3 and p.status = 1 " +
        "order by po.id desc", nativeQuery = true)
    List<Payment> findPingPlusByTimeBetween(Date startDate, Date endDate);

    Payment findFirstByItpNo(String itpNo);

}
