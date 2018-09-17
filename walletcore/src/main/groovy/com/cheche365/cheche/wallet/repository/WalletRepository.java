package com.cheche365.cheche.wallet.repository;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.wallet.model.Wallet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mjg on 2017/6/6.
 */
@Repository
public interface WalletRepository extends PagingAndSortingRepository<Wallet, Long> {

    @Query(value = "select  * from wallet where user_id=?1 and ((?2 is not null and channel = ?2) or (?2 is null and channel is null)) and status=1 limit 1", nativeQuery = true)
    Wallet findByUserIdAndChannel(Long userId, Channel channel);

    @Query(value = "select u.mobile,w.balance,c.description,sum(if(t.trade_flag = 0 and t.status != 3, t.amount, 0)) as outAmount " +
            " from wallet w join user u on u.id = w.user_id join wallet_trade t on w.id = t.wallet_id               " +
            " left join channel c on u.register_channel = c.id where w.id = ?1                                      ", nativeQuery = true)
    List<Object[]> findUserInfoByWallet(Long walletId);

    @Query(value = "select count(DISTINCT w.id) from wallet w left join wallet_trade t on w.id = t.wallet_id             " +
            " where (?1 OR w.mobile = ?2) AND (?3 OR t.trade_no = ?4) AND (?5 OR t.update_time BETWEEN ?6 AND ?7) AND (?8 OR w.channel = ?9)", nativeQuery = true)
    Long countWallet(int isNullMobile, String mobile, int isNullTradeNo, String tradeNo, int isNullUpdateTime, Date startTime, Date endTime, int isNullChannel, Long channel);

    @Query(value = "SELECT w.id, w.mobile, wt.inAmount, wt.outAmount, wt2.update_time, w.balance, wts.description," +
            "IF( wt2.trade_flag = 1, concat('入账-', wtso.description), concat('出账-', wtso.description)) AS currType " +
            "FROM wallet AS w LEFT JOIN " +
            "( SELECT  wallet_id,  sum(IF(trade_flag = 1, amount, 0)) AS inAmount,  sum(IF(trade_flag = 0 and status!=3, amount, 0)) AS outAmount " +
            "FROM  wallet_trade WHERE  ( ?5 OR update_time BETWEEN ?6   AND ?7  ) AND (?3 OR trade_no = ?4) " +
            "GROUP BY  wallet_id) wt " +
            "ON wt.wallet_id = w.id LEFT JOIN ( SELECT  update_time,  trade_flag,  STATUS,  trade_type,  wallet_id,  trade_no " +
            "FROM  wallet_trade wtt1 JOIN (SELECT max(id) id  FROM wallet_trade  GROUP BY  wallet_id ) wtt2 ON wtt1.id = wtt2.id " +
            "ORDER BY  wtt1.wallet_id) wt2 ON wt2.wallet_id = w.id " +
            "LEFT JOIN wallet_trade_status wts ON wts.id = wt2. STATUS " +
            "LEFT JOIN wallet_trade_source wtso ON wtso.id = wt2.trade_type " +
            "WHERE (?1 OR w.mobile = ?2)AND (?10 OR w.channel = ?11)AND ( ?5 OR wt2.update_time BETWEEN ?6 AND ?7)AND (?3 OR wt2.trade_no = ?4)" +
            "ORDER BY wt2.update_time DESC LIMIT ?8 OFFSET ?9", nativeQuery = true)
    List<Object[]> findWalletAndLatestTrade(int isNullMobile, String mobile, int isNullTradeNo, String tradeNo, int isNullUpdateTime, Date startTime, Date endTime, int currentSize, int startIndex, int isNullChannel, Long channel);

    @Query(value = "select w.balance from wallet w left join wallet_trade t on w.id = t.wallet_id                           " +
            " where (?1 OR w.mobile = ?2) AND (?3 OR t.trade_no = ?4) AND (?5 OR t.update_time BETWEEN ?6 AND ?7) AND (?8 OR w.channel = ?9) group by w.id", nativeQuery = true)
    List<BigDecimal> findWalletBalance(int isNullMobile, String mobile, int isNullTradeNo, String tradeNo, int isNullUpdateTime, Date startTime, Date endTime, int isNullChannel, Long channel);

    @Query(value = "select count(*) as walletCount,sum(t.balance) as walletBalance from wallet t where (t.channel not in (?1) or t.channel is null)", nativeQuery = true)
    List<Object[]> findWalletCountAndBalance(List<Channel> toAChannels);

    @Query(value = "select count(*) as outNumber,sum(if(t.`status` = 2, t.amount, 0)) as outAmount,count(distinct w.user_id) as outUserCount from wallet w join wallet_trade t on w.id = t.wallet_id where t.trade_flag = 0 and (w.channel not in (?1) or w.channel is null)", nativeQuery = true)
    List<Object[]> findOutNumberAndAmountAndUserCount(List<Channel> toAChannels);

    @Query(value = "select count(*) from wallet w where exists (select 1 from wallet_trade t where w.id = t.wallet_id) and (w.channel not in (?1) or w.channel is null)", nativeQuery = true)
    Long findActiveWalletCount(List<Channel> toAChannels);

    @Query(value = "select count(if(c.`disable` = 0, c.id, null)) as cardCount,count(distinct c.`user`) as bindingCount, count(distinct if(c.`disable` = 1, c.`user`, null)) as delCount " +
            "from wallet w join bank_card c on w.user_id = c.`user` where (w.channel not in (?1) or w.channel is null)", nativeQuery = true)
    List<Object[]> findbindCountAndBindUserAndDelUserCount(List<Channel> toAChannels);

    @Query(value = "select sum(if(wt.`status` = 2, wt.amount, 0)) as totalAmount,count(*) as totalNum, count(distinct w.user_id) as userCount, " +
            "count(if(wt.`status` = 2, wt.id, null)) as successCount, count(if(wt.`status` = 3, wt.id, null)) as failCount, " +
            "count(if(wt.`status` = 5, wt.id, null)) as outingCount from wallet w join wallet_trade wt on w.id = wt.wallet_id " +
            "where wt.trade_flag = 0 and wt.trade_date between ?1 and ?2 and (w.channel not in (?3) or w.channel is null)", nativeQuery = true)
    List<Object[]> findYesterdayOutInfo(Date startDate, Date endDate, List<Channel> toAChannels);
}
