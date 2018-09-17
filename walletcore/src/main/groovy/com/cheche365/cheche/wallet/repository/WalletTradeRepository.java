package com.cheche365.cheche.wallet.repository;

import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.wallet.model.Wallet;
import com.cheche365.cheche.wallet.model.WalletTrade;
import com.cheche365.cheche.wallet.model.WalletTradeSource;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by mjg on 2017/6/6.
 */
@Repository
public interface WalletTradeRepository extends PagingAndSortingRepository<WalletTrade, Long> , JpaSpecificationExecutor<WalletTrade> {

    @Query(value = "select  * from wallet_trade where trade_no=?1 and user_id=?2 limit 1", nativeQuery = true)
    WalletTrade findByTradeNo(String tradeNo, User user);

    @Query(value = "select  * from wallet_trade where trade_type=3 and trade_source_id=?1 and user_id=?2 limit 1", nativeQuery = true)
    WalletTrade findByRemitId(Long remitId, User user);

    @Query(value = "select  * from wallet_trade where trade_type=3 and partner_requestno=?1 limit 1", nativeQuery = true)
    WalletTrade findByRequestNo(String requestNo);

    @Query(value = "select  * from wallet_trade where trade_date >= ?1 and trade_date <= ?2 and status=1", nativeQuery = true)
    List<WalletTrade> findByDate(String beginDate, String endDate);

    @Query(value = "select count(*) from wallet_trade t join wallet_trade_source so on so.id = t.trade_type       " +
        " join wallet_trade_status st on st.id = t.`status` left join bank_card c on t.bankcard_id = c.id            " +
        " left join bank b on c.bank = b.id left join channel ch on ch.id = t.channel                                " +
        " where t.wallet_id = ?1 and (?2 or t.trade_flag = ?3) and (?4 or t.trade_type in (?5))                   " +
        " and (?6 or t.`status` in (?7)) and (?8 or t.channel in (?9)) and (?10 or t.trade_date between ?11 and ?12)" , nativeQuery = true)
    Long findTransactionCount(Long walletId, int isNullType, int type, int isNullSource, List sources, int isNullStatus, List statuses, int isNullPlatform, List platform, int isNullTime, Date startTime, Date endTime);

    @Query(value = "select t.trade_no,t.trade_flag,so.description as type,b.`name`,st.description as status,         " +
        " t.amount,t.balance,t.trade_date,ch.description as channel ,wr.response_msg as failReason                    " +
        " from wallet_trade t join wallet_trade_source so on so.id = t.trade_type                                 " +
        " join wallet_trade_status st on st.id = t.`status` left join bank_card c on t.bankcard_id = c.id            " +
        " left join bank b on c.bank = b.id left join channel ch on ch.id = t.channel                                " +
        " left join wallet_remit_record wr on t.trade_source_id = wr.id                                          "+
        " where t.wallet_id = ?1 and (?2 or t.trade_flag = ?3) and (?4 or t.trade_type in (?5))                   " +
        " and (?6 or t.`status` in (?7)) and (?8 or t.channel in (?9)) and (?10 or t.trade_date between ?11 and ?12) order by t.trade_date limit ?13,?14" , nativeQuery = true)
    List<Object[]> findTransactionList(Long walletId, int isNullType, int type, int isNullSource, List sources, int isNullStatus,
                                       List statuses, int isNullPlatform, List platform, int isNullTime, Date startTime, Date endTime, int startIndex, int currentSize);

    @Query(value = " select * from wallet_trade where wallet_id = ?1 and trade_type in (?2) and create_time >= ?3 and create_time <= ?4 ", nativeQuery = true)
    List<WalletTrade> findByWalletAndTradeTypeAndCreateTime(Long walletId, List<WalletTradeSource> tradeTypes, Date begin, Date end);

    List<WalletTrade> findByTradeSourceIdAndTradeType(Long tradeSourceId,WalletTradeSource type);

    WalletTrade findByUserIdAndTradeSourceIdAndTradeType(Long userId,Long tradeSourceId,WalletTradeSource type);

    @Query(value = "SELECT " +
        "wts.status, " +
        "wt.amount, " +
        "wt.trade_date, "+
        "u.identity, " +
        "b.name, " +
        "bc.bank_no, " +
        "wrr.response_msg ," +
        "wtss.source " +
        "FROM " +
        "wallet_trade wt," +
        "wallet_trade_status wts," +
        "user u," +
        "bank_card bc, " +
        "bank b, " +
        "wallet_remit_record wrr ," +
        "wallet_trade_source wtss " +
        "WHERE " +
        "wt.status = wts.id " +
        "AND wt.user_id = u.id " +
        "AND wt.bankcard_id = bc.id " +
        "AND bc.bank = b.id " +
        "AND wt.partner_requestno = wrr.request_no " +
        "AND wtss.id = wt.trade_type " +
        "AND wt.id = ?1", nativeQuery = true)
    List<Object[]> findWalletTradeDetail(Long id);

    WalletTrade findByUserIdAndTradeType(Long userId, WalletTradeSource type);
}
