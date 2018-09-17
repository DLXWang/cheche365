package com.cheche365.cheche.core.service.gift.rules

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.GiftTypeRepository
import com.cheche365.cheche.core.repository.MarketingRepository
import com.cheche365.cheche.core.repository.MarketingRuleRepository
import com.cheche365.cheche.core.repository.RuleConfigRepository
import com.cheche365.cheche.core.service.GiftService
import com.cheche365.cheche.core.service.PurchaseOrderGiftService
import com.cheche365.cheche.core.service.gift.ConfigurableRule
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.model.RuleParam.Enum.REDUCE_CALCULATE_PKG_LIMIT_RULE_5
import static com.cheche365.cheche.core.model.RuleParam.Enum.REDUCE_FULL_RULE_1
import static com.cheche365.cheche.core.model.RuleParam.Enum.REDUCE_IS_REGULAR_RULE_2
import static com.cheche365.cheche.core.model.RuleParam.Enum.REDUCE_ORDER_PKG_LIMIT_RULE_4
import static com.cheche365.cheche.core.model.RuleParam.Enum.REDUCE_OTHER_SEND_RULE_6
import static com.cheche365.cheche.core.model.RuleParam.Enum.REDUCE_TOP_RULE_3

/**
 * Created by mahong on 2016/8/5.
 * 全国通用活动-满减类型活动-优惠策略
 */
@Service("commonMarketingReduce")
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Transactional
@Slf4j
@Order(1)
public class DiscountRule extends ConfigurableRule {

    @Override
    public Map<String,RuleConfig> configParams(List<RuleConfig> ruleConfigs){
        [
            fullInsurance: find(ruleConfigs,REDUCE_CALCULATE_PKG_LIMIT_RULE_5),
            quoteFields : find(ruleConfigs,REDUCE_ORDER_PKG_LIMIT_RULE_4),
            isAccTotal: find(ruleConfigs,REDUCE_IS_REGULAR_RULE_2) ,
            reduceFull: find(ruleConfigs,REDUCE_FULL_RULE_1) ,
            topDiscount: find(ruleConfigs,REDUCE_TOP_RULE_3 ),
            otherAdditionalAndRealGift : find(ruleConfigs,REDUCE_OTHER_SEND_RULE_6),
        ]
    }

    @Override
    boolean support(ActivityType activityType) {
        return ActivityType.Enum.FULL_REDUCE_4 == activityType
    }

    @Override
    public List<Gift> generateCouponGifts(QuoteRecord quoteRecord, List<RuleConfig> ruleConfigs) {
        Double amount = calculateReduceAmount(quoteRecord, ruleConfigs);
        List<Gift> couponGifts = new ArrayList<>();
        if (amount > 0) {
            couponGifts.add(getCouponGift(quoteRecord, amount));
        }
        return couponGifts;
    }

    private Double calculateReduceAmount(QuoteRecord quoteRecord, List<RuleConfig> ruleConfigs) {
        Map<String,RuleConfig> ruleEqualsMap=configParams(ruleConfigs);
        RuleConfig reduceRuleConfig = ruleEqualsMap.get('reduceFull');
        RuleConfig isRegularReduceRuleConfig = ruleEqualsMap.get('isAccTotal');
        RuleConfig topReduceRuleConfig = ruleEqualsMap.get('topDiscount');

        Double reduceAmount = 0.0;
        Double fullAmount = 0.0;
        Double pkgLimitAmount = calculatePkgLimitAmount(quoteRecord, ruleConfigs);
        Boolean isRegularReduce = (isRegularReduceRuleConfig != null && Boolean.TRUE.toString() == isRegularReduceRuleConfig.getRuleValue());
        if (isRegularReduce) {
            String[] reduceAmountArray = reduceRuleConfig.getRuleValue().split(WebConstants.COMMON_MARKETING_SYMBOL_SPLIT);
            reduceAmount = Double.valueOf(((int) (pkgLimitAmount / Double.valueOf(reduceAmountArray[0]))) * Double.valueOf(reduceAmountArray[1]));
        } else {
            List<String> reduceAmountPairs = Arrays.asList(reduceRuleConfig.getRuleValue().split(WebConstants.COMMON_MARKETING_LOGIC_SYMBOL_AND));
            for (String reduceAmountPair : reduceAmountPairs) {
                String[] reduceAmountArray = reduceAmountPair.split(WebConstants.COMMON_MARKETING_SYMBOL_SPLIT);
                if (Double.valueOf(reduceAmountArray[0]) <= pkgLimitAmount && Double.valueOf(reduceAmountArray[0]) > fullAmount) {
                    reduceAmount = Double.valueOf(reduceAmountArray[1]);
                    fullAmount = Double.valueOf(reduceAmountArray[0]);
                }
            }
        }
        return (topReduceRuleConfig == null || reduceAmount < Double.valueOf(topReduceRuleConfig.getRuleValue())) ? DoubleUtils.displayDoubleValue(reduceAmount) : DoubleUtils.displayDoubleValue(Double.valueOf(topReduceRuleConfig.getRuleValue()));
    }
}
