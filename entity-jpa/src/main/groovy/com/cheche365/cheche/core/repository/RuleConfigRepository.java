package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.MarketingRule;
import com.cheche365.cheche.core.model.RuleConfig;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleConfigRepository extends PagingAndSortingRepository<RuleConfig, Long> {
    List<RuleConfig> findByMarketingRule(MarketingRule marketingRule);

    List<RuleConfig> findByMarketingRuleOrderByRuleParamAsc(MarketingRule marketingRule);

    //需购买哪几种险种才能享受优惠
    @Query(value = "select * from rule_config  where marketing_rule = ?1 and rule_param in(?2)",nativeQuery = true)
    RuleConfig findByInsuranceTypes(Long marketingRuleId,List<Long> ruleParam);

    void deleteByMarketingRule(MarketingRule marketingRule);
}
