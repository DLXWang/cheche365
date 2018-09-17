package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.PartnerOrder;
import com.cheche365.cheche.core.model.PartnerOrderSync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by mahong on 2016/2/19.
 */
@Repository
public interface PartnerOrderSyncRepository  extends JpaRepository<PartnerOrderSync, Integer> {
    @Query(value = "select * from partner_order_sync where partner_order = ?1 and purchase_order_status=?2 and status=?3 order by create_time desc limit 1",nativeQuery = true)
    PartnerOrderSync findPartnerOrderAndOrderStatus(PartnerOrder partnerOrder, OrderStatus orderStatus, Integer status);
    @Query(value = "select distinct purchase_order_status  from partner_order_sync where partner_order=?1 ORDER BY  purchase_order_status",nativeQuery = true)
    List<BigInteger> findSyncFailOrderStatus(PartnerOrder partnerOrder);
    @Query(value = "select * from partner_order_sync where purchase_order_status =?1 ORDER BY create_time DESC  LIMIT 1",nativeQuery = true)
    PartnerOrderSync findNeedSyncOrder(BigInteger status);

}
