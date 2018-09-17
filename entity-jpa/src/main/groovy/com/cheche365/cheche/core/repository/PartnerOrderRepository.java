package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ApiPartner;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.PartnerOrder;
import com.cheche365.cheche.core.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by zhaozhong on 2016/1/14.
 */
@Repository
public interface PartnerOrderRepository extends JpaRepository<PartnerOrder, Integer>{
    @Query(value = "select * from partner_order where purchase_order_id = ?1 ", nativeQuery = true)
    PartnerOrder findFirstByPurchaseOrderId(long purchseOrderId);

    int countByPurchaseOrder(PurchaseOrder purchaseOrder);

    @Query(value = "select count(*) from PartnerOrder where purchaseOrder = ?1 and channel = 'ZRJ' ")
    int countFromAutoHomeMainApp(PurchaseOrder purchaseOrder);

    @Query(value = "SELECT * FROM partner_order partner JOIN purchase_order purchase ON purchase.id = partner.purchase_order_id WHERE purchase.`status` IN (?1) AND partner.partner_third = ?2 AND purchase.update_time > ?3 AND purchase.update_time < ?4", nativeQuery = true)
    List<PartnerOrder> getPartnerByTime(List<OrderStatus> statusList, ApiPartner partner, Date start, Date end);
}
