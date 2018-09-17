package com.cheche365.cheche.wallet.repository;

import com.cheche365.cheche.wallet.model.WalletRemitRecord;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mjg on 2017/6/6.
 */
@Repository
public interface WalletRemitRepository extends PagingAndSortingRepository<WalletRemitRecord, Long>, JpaSpecificationExecutor<WalletRemitRecord> {

    @Query(value = "select  a.* from wallet_remit_record a,wallet_trade b where b.trade_source_id = a.id and a.request_no = ?1 and b.user_id=?2  limit 1", nativeQuery = true)
    WalletRemitRecord findByRequestNo(String requestNo, Long userId);

    @Query(value = "select  * from wallet_remit_record where remit_date >= ?1 and remit_date <= ?2 and status=1", nativeQuery = true)
    List<WalletRemitRecord> findByDate(String beginDate, String endDate);

    @Query(value = "select wr.account_no '0', (case when ai.id is not null then concat(wr.account_name,'-车车') " +
            "when ai.id is null then wr.account_name end) as account_name, u.identity '2',wt.user_id '3',wr.id '4',wr.trade_amt '5',wr.request_no '6' " +
            "from wallet_remit_record wr join wallet_trade wt on wr.id = wt.trade_source_id join user u on u.id = wt.user_id " +
            "left join agent_internal ai on u.identity = ai.identity where wr.remit_date between ?1 and ?2 and wt.channel in (?3) and wt.trade_type = 3 and wr.status = 1", nativeQuery = true)
    List<Object[]> getChebaoyiWithDrawalDetailInfo(Date startTime, Date endTime, List<Long> channelIds);

    @Query(value = "select count(*),ifnull(sum(wr.trade_amt),0) from wallet_remit_record wr join wallet_trade wt on wr.id = wt.trade_source_id " +
            " where wr.remit_date between ?1 and ?2 and wt.channel in (?3) and wt.trade_type = 3 and wr.status = 1", nativeQuery = true)
    Object getChebaoyiWithdrawalCollectInfo(Date startTime, Date endTime, ArrayList<Long> channelIds);

    @Query(value = "select wr.* from wallet_remit_record wr join user_remit_trade_history uh on uh.wallet_remit_trade_id = wr.id where uh.merchant_seq_no = ?1", nativeQuery = true)
    List<WalletRemitRecord> findByMerchantSeqNo(String merchantSeqNo);
}
