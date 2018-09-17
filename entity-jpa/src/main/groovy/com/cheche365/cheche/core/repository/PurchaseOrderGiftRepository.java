package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.PurchaseOrderGift;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mahong on 2015/6/23.
 */
@Repository
public interface PurchaseOrderGiftRepository extends PagingAndSortingRepository<PurchaseOrderGift, Long> {

    @Query(value = "SELECT count(og.id) FROM purchase_order_gift og,gift g WHERE og.gift = g.id AND g.applicant = ?1 AND g.gift_type = ?2", nativeQuery = true)
    Integer getUnUsedGiftUser(Long userId, Long giftType);

    List<PurchaseOrderGift> findByPurchaseOrder(PurchaseOrder purchaseOrder);

    @Query(value = "select * from purchase_order_gift where purchase_order=?1 order by id desc ", nativeQuery = true)
    List<PurchaseOrderGift> searchUsedGift(Long orderId);

    @Query(value = "select count(pog.id) from purchase_order_gift pog,gift g where pog.gift=g.id and pog.purchase_order=?1 and g.gift_type=?2", nativeQuery = true)
    Long countByPurchaseOrderAndGiftType(Long purchaseOrderId,Long giftTypeId);

    List<PurchaseOrderGift> findByPurchaseOrderAndGivenAfterOrder(PurchaseOrder purchaseOrder, boolean givenAfterOrder);

    @Query(value = "select ms.marketing_id from purchase_order_gift pg ,purchase_order p,gift g,marketing_success ms " +
        " where pg.purchase_order =  p.id  and pg.gift = g.id and g.source_type = 2 and g.source = ms.id and p.id = ?1 limit 1 ", nativeQuery = true)
    Long findMarketingIdByOrder(PurchaseOrder purchaseOrder);

    @Query(value = "select * from purchase_order_gift where purchase_order in ?1", nativeQuery = true)
    List<PurchaseOrderGift> findByOrderIds(List ids);
}
