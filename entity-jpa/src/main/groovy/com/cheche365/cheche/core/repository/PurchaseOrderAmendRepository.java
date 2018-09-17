package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by mahong on 2016/9/19.
 */
@Repository
public interface PurchaseOrderAmendRepository extends PagingAndSortingRepository<PurchaseOrderAmend, Long> {

    @Query(value = "select * from purchase_order_amend where purchase_order = ?1 order by id desc limit 1", nativeQuery = true)
    PurchaseOrderAmend findLatestAmendByPurchaseOrder(PurchaseOrder order);

    @Query(value = "select * from purchase_order_amend where purchase_order = ?1 and purchase_order_amend_status <> 2 and payment_type <> 4 order by id desc limit 1", nativeQuery = true)
    PurchaseOrderAmend findLatestAmendNotFullRefundNotCancel(PurchaseOrder order);

    List<PurchaseOrderAmend> findByPurchaseOrder(PurchaseOrder purchaseOrder);

    PurchaseOrderAmend findByNewQuoteRecord(QuoteRecord quoteRecord);

    List<PurchaseOrderAmend> findByPurchaseOrderAndPurchaseOrderAmendStatus(PurchaseOrder purchaseOrder, PurchaseOrderAmendStatus purchaseOrderAmendStatus);

    @Query(value = "select pa from com.cheche365.cheche.core.model.PurchaseOrderAmend pa where pa.orderOperationInfo.currentStatus= ?1 and pa.paymentType = ?2 and (pa.createTime between ?3 and ?4 or pa.createTime < ?5)  and pa.purchaseOrder.status = ?6 and pa.purchaseOrderAmendStatus=?7 order by pa.id")
    List<PurchaseOrderAmend> findOvertimeRefundData(OrderTransmissionStatus applyForRefund, PaymentType fullrefund, Date startDate, Date endDate, Date currDateBefore24, OrderStatus REFUNDING, PurchaseOrderAmendStatus CREATE);

    @Query(value = "select pa from com.cheche365.cheche.core.model.PurchaseOrderAmend pa where pa.orderOperationInfo.currentStatus= ?2 " +
        "and pa.paymentType = ?3 and pa.purchaseOrder.status = ?4 and pa.purchaseOrderAmendStatus=?5 and pa.purchaseOrder.sourceChannel not in (?6) and pa.id > ?1 order by pa.id")
    List<PurchaseOrderAmend> findRefundOrderList(long preId, OrderTransmissionStatus applyForRefund, PaymentType fullrefund, OrderStatus REFUNDING, PurchaseOrderAmendStatus CREATE, List<Channel> excludeChannelList);

    @Query(value = "select * from purchase_order_amend pa where pa.order_operation_info = ?1 order by pa.id desc limit 1", nativeQuery = true)
    PurchaseOrderAmend findLastByOrderOperationInfo(OrderOperationInfo orderOperationInfo);

    @Query(value = "select * from purchase_order_amend pa where pa.purchase_order_amend_status = ?1 and pa.id = (select tr.source_id from tel_marketing_center_repeat tr where tr.mobile = ?2 and tr.source_table = ?3 order by tr.id desc limit 1)", nativeQuery = true)
    PurchaseOrderAmend findByMobileFromTelMarketingCenter(Long amendStatus, String mobile, String tableName);

    PurchaseOrderAmend findByPurchaseOrderHistory(PurchaseOrderHistory purchaseOrderHistory);

    PurchaseOrderAmend findFirstByOriginalQuoteRecord(QuoteRecord quoteRecord);

    @Query(value = "select pa from PurchaseOrderAmend pa where pa.id in(?1)  order by pa.id")
    List<PurchaseOrderAmend> findByIds(List<Long> ids);
}
