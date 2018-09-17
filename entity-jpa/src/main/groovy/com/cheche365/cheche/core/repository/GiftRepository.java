package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Gift;
import com.cheche365.cheche.core.model.Marketing;
import com.cheche365.cheche.core.model.PurchaseOrderHistory;
import com.cheche365.cheche.core.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by zhengwei on 4/16/15.
 */

@Repository
public interface GiftRepository extends PagingAndSortingRepository<Gift, Long>, JpaSpecificationExecutor<Gift> {

    List<Gift> findByApplicant(User applicant);

    @Query(value="select g.* from gift g where id=?1 and applicant=?2 and status=1", nativeQuery = true)
    Gift getGift(Long id, Long applicant);

    @Query(value="select g.* from gift g where applicant=?1 and status in (?2)", nativeQuery = true)
    List<Gift> selectByApplicantAndStatus(Long applicant, String status);


    @Query(value="select * from gift g ,gift_status gs where g.applicant=?1 and g.status=gs.id and g.status in ?2 and g.gift_type in ?3 " +
        "and g.effective_date is not null and g.expire_date is not null  " +
        "order by gs.rank,g.status,g.gift_amount desc, g.create_time", nativeQuery = true)
    List<Gift> searchGifts(long userId, List<Long> giftStatus, List<Long> giftType);

    @Query(value = " select * from gift g ,gift_status gs where g.applicant=?1 and g.status=gs.id and g.status in ?2 and g.gift_type in ?3 " +
        "    and g.effective_date <= CURDATE()  and g.expire_date >= CURDATE() and source_type = 2 " +
        "    and exists (select 1 from marketing_success ms where ms.id = g.source and ms.marketing_id = ?4) " +
        "    order by gs.rank,g.status,g.gift_amount desc, g.create_time ", nativeQuery = true)
    List<Gift> searchValidGiftsByMarketing(long userId, List<Long> giftStatus, List<Long> giftType, Marketing marketing);


    @Query(value = "FROM Gift WHERE expire_date < ?1 and (status = 1 OR status IS NULL) order by id DESC")
    List<Gift> getExpireDatePutGray(String nowDateTime,Pageable pageable);


    @Query(value = "select count(*) FROM gift WHERE expire_date < ?1 and (status = 1 OR status IS NULL)", nativeQuery = true)
    Object[] getCountByExpireDate(String nowDateTime);


    @Query(value = "select id from gift where create_time <= ?1 order by id desc limit 1", nativeQuery = true)
    Long findMaxIdByTime(Date createTime);

    @Query(value = "SELECT g.* FROM purchase_order_gift pg   " +
        " JOIN gift g ON pg.gift = g.id                      " +
        " JOIN gift_type gt ON g.gift_type = gt.id           " +
        " WHERE gt.category in (4,6) AND pg.purchase_order = ?1   " , nativeQuery = true)
    List<Gift> findMaterialGiftByOrder(Long purchaseOrder);

    @Query(value = "SELECT g.*               " +
        " FROM purchase_order_gift_history t " +
        " JOIN gift g ON t.gift = g.id       " +
        " WHERE g.gift_type IN (4, 6)        " +
        " AND t.purchase_order_history = ?1  "  , nativeQuery = true)
    List<Gift> findGiftByOrderHistory(PurchaseOrderHistory purchaseOrderHistory);
}
