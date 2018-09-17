package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.GiftCode;
import com.cheche365.cheche.core.model.GiftCodeExchangeWay;
import com.cheche365.cheche.core.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mahong on 2015/6/26.
 */
@Repository
public interface GiftCodeRepository extends PagingAndSortingRepository<GiftCode,Long> {
    GiftCode findFirstByCode(String code);

    List<GiftCode> findByApplicantAndExchangeWayAndExchangedTrue(User user, GiftCodeExchangeWay exchangeWay);

    @Query(value="select * from gift_code WHERE applicant=?1 and exchanged=?2  order by id desc limit 1", nativeQuery = true)
    GiftCode findByApplicantAndExchanged(User user, Boolean exchanged);
}
