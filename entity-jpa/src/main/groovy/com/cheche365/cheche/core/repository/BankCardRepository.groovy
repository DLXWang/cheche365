package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.Bank
import com.cheche365.cheche.core.model.BankCard
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Created by mahong on 17/02/2017.
 */
interface BankCardRepository extends PagingAndSortingRepository<BankCard, Long>, JpaSpecificationExecutor<BankCard> {

    @Query(value = "select bc.* from bank_card bc where bc.disable = 0 and bc.user = ?2 and exists ( select 1 from insurance i where i.applicant_name = bc.name and i.quote_record = ?1) ", nativeQuery = true)
    List<BankCard> findByApplicantNameAndUser(Long quoteRecordId, User user)

    @Query(value = "select bc.* from bank_card bc where bc.disable = 0 and bc.user = ?1 ", nativeQuery = true)
    List<BankCard> findByUser(User user)

    BankCard findFirstByIdAndUserAndDisable(Long id, User user, boolean disable)

    BankCard findFirstByBankAndBankNoAndNameAndUserAndDisable(Bank bank, String bankNo, String name, User user, boolean disable)

    @Query(value = "select bankcard_id from wallet_trade where trade_type=3 and user_id=?1 and channel=?2 order by id desc limit 1", nativeQuery = true)
    Long findPreRemit(User user, Channel channel)
}
