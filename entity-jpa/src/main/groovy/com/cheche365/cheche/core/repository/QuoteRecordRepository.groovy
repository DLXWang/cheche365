package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface QuoteRecordRepository extends PagingAndSortingRepository<QuoteRecord, Long> {

    QuoteRecord findFirstByApplicantAndId(User user, Long id)


    @Query(value = '''
SELECT qr.* FROM `quote_record` qr LEFT JOIN `user_invitation_code` uic ON qr.`applicant` = uic.`user`
WHERE `qr`.`applicant` IN ?1
AND qr.`channel` IN ?2
AND `qr`.`create_time` >= uic.`create_time`
AND `qr`.`create_time` <= ?3
AND NOT EXISTS (SELECT 1 FROM `purchase_order` po WHERE `po`.`applicant` IN ?1 AND (`po`.`obj_id` = `qr`.`id` OR `po`.`auto` = `qr`.`auto`) AND `create_time` <= ?3 AND `create_time` >= ?3)
    ''', nativeQuery = true)
    List<QuoteRecord> findAllHasNoOrder(List<Long> userId, List<Long> channelIds, Date endDate);

    @Query(value="select q.* from quote_record q where q.id in ?1", nativeQuery = true)
    List<QuoteRecord> findQuoteRecordByIds(List ids);

    @Query(value = "SELECT q.id,q.premium,q.compulsory_premium,q.auto_tax,q.create_time,p.purchase_order_history FROM purchase_order_amend p JOIN order_operation_info o ON p.order_operation_info = o.id JOIN purchase_order_history h ON h.id = p.purchase_order_history JOIN quote_record q ON q.id = h.obj_id WHERE p.payment_type IN (2, 3, 6) AND o.purchase_order = ?1 ORDER BY p.id" , nativeQuery = true)
    List<Object[]> findAmendRecordsByOrderId(Long orderId);

    @Query(value = "select qr.* from quote_record qr join purchase_order po on po.obj_id = qr.id where po.id = ?1" , nativeQuery = true)
    QuoteRecord findByOrderId(Long orderId);

    @Query(value = "select qr.* from quote_record qr,quote q  where qr.quote = q.id and q.id = ?1 ORDER BY qr.id desc limit 1" , nativeQuery = true)
    QuoteRecord findquoteRecordByQuoteId(int quoteId)
}
