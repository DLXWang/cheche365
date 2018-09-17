package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ActivityType;
import com.cheche365.cheche.core.model.RuleParam;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mahong on 2016/8/23.
 */
@Repository
public interface RuleParamRepository extends PagingAndSortingRepository<RuleParam, Long>, JpaSpecificationExecutor<RuleParam> {
    List<RuleParam> findRuleParamByActivityType(ActivityType activityType);
}
