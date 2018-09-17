package com.cheche365.cheche.core.service.gift;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.ActivityType
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component

/**
 * Created by mahong on 2015/8/21.
 */
@Component
public class RuleFactory {

    private List<ConfigurableRule> configurableRules

    public RuleFactory (List<ConfigurableRule> configurableRules) {
        this.configurableRules = configurableRules
    }

    public ConfigurableRule findRuleService(Long activityTypeId) {

        configurableRules.find {it.support(new ActivityType(id: activityTypeId))}.with {rule ->
            if(!rule){
                throw new BusinessException(BusinessException.Code.UNEXPECTED_PARAMETER, "处理类不存在, 活动类型为:" + activityTypeId);
            }
            rule

        }
    }
}
