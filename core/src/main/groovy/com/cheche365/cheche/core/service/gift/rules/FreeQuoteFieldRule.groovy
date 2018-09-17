package com.cheche365.cheche.core.service.gift.rules

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.model.ActivityType
import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.model.MarketingInsuranceType
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.RuleConfig
import com.cheche365.cheche.core.service.gift.ConfigurableRule
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import static com.cheche365.cheche.core.model.RuleParam.Enum.DEDUCT_CALCULATE_PKG_LIMIT_RULE_17
import static com.cheche365.cheche.core.model.RuleParam.Enum.DEDUCT_INSURANCE_TYPE_RULE_13
import static com.cheche365.cheche.core.model.RuleParam.Enum.DEDUCT_ORDER_PKG_LIMIT_RULE_16
import static com.cheche365.cheche.core.model.RuleParam.Enum.DEDUCT_OTHER_SEND_RULE_18
import static com.cheche365.cheche.core.model.RuleParam.Enum.DEDUCT_PERCENT_RULE_15
import static com.cheche365.cheche.core.model.RuleParam.Enum.DEDUCT_TOP_PKG_RULE_14


/**
 * Created by mahong on 2016/8/5.
 * 全国通用活动-抵扣类型活动-优惠策略
 */
@Service("commonMarketingDeduct")
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Transactional
@Slf4j
@Order(3)
public class FreeQuoteFieldRule extends ConfigurableRule {

    @Override
    public Map configParams(List<RuleConfig> ruleConfigs){
        [
            fullInsurance: find(ruleConfigs,DEDUCT_CALCULATE_PKG_LIMIT_RULE_17),
            deductInsType: find(ruleConfigs,DEDUCT_INSURANCE_TYPE_RULE_13) ,
            quoteFields : find(ruleConfigs,DEDUCT_ORDER_PKG_LIMIT_RULE_16),
            deductPercent: find(ruleConfigs,DEDUCT_PERCENT_RULE_15) ,
            topPkg: find(ruleConfigs,DEDUCT_TOP_PKG_RULE_14 ),
            otherAdditionalAndRealGift : find(ruleConfigs,DEDUCT_OTHER_SEND_RULE_18),
        ]
    }

    @Override
    boolean support(ActivityType activityType) {
        return ActivityType.Enum.INSURANCE_PACKAGE_DEDUCT_6 == activityType
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
        RuleConfig insuranceTypeRuleConfig = ruleEqualsMap.get('deductInsType');
        Double deductInsurancePremium = 0.0;
        if (insuranceTypeRuleConfig.getRuleValue() == String.valueOf(MarketingInsuranceType.Enum.COMPULSORY_1.id)) {
            deductInsurancePremium = DoubleUtils.doubleValue(quoteRecord.getCompulsoryPremium());
        } else if (insuranceTypeRuleConfig.getRuleValue() == String.valueOf(MarketingInsuranceType.Enum.COMMERCIAL_2.id)) {
            deductInsurancePremium = DoubleUtils.doubleValue(quoteRecord.getPremium());
        } else if (insuranceTypeRuleConfig.getRuleValue() == String.valueOf(MarketingInsuranceType.Enum.AUTO_TAX_3.id)) {
            deductInsurancePremium = DoubleUtils.doubleValue(quoteRecord.getAutoTax());
        }
        RuleConfig discountRuleConfig = ruleEqualsMap.get('deductPercent');
        Double pkgLimitAmount = calculatePkgLimitAmount(quoteRecord, ruleConfigs);
        Double deductAmount = pkgLimitAmount * Double.valueOf(discountRuleConfig.getRuleValue()) / 100;
        return DoubleUtils.displayDoubleValue(deductAmount > deductInsurancePremium ? deductInsurancePremium : deductAmount);
    }
}
