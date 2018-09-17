package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.tide.TidePlatform;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yinJianBin on 2018/4/19.
 */
public interface TidePlatformRepository extends PagingAndSortingRepository<TidePlatform, Long>, JpaSpecificationExecutor<TidePlatform> {

    Iterable<TidePlatform> findAllByOperator(InternalUser internalUser);

    @Query(value = "select distinct tp.id,tp.name, tp.user_name, tp.mobile, to_days(now()) - to_days(max(tcr.update_time)) as status," +
            "tp.create_time,tp.update_time,tp.email,tp.operator ,tp.description " +
            "from tide_contract_rebate tcr " +
            "  join tide_contract tc on tcr.tide_contract = tc.id " +
            "  join tide_branch tb on tc.tide_branch = tb.id " +
            "  join tide_platform tp on tb.tide_platform = tp.id " +
            "group by tide_platform " +
            "having status > ?1 and status <?2 ", nativeQuery = true)
    Iterable<TidePlatform> findRebateNoChange(Integer start, Integer end);
}