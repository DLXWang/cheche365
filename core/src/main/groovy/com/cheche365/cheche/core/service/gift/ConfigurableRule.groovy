package com.cheche365.cheche.core.service.gift

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ActivityType
import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.model.GiftStatus
import com.cheche365.cheche.core.model.GiftType
import com.cheche365.cheche.core.model.MarketingInsuranceType
import com.cheche365.cheche.core.model.MarketingRule
import com.cheche365.cheche.core.model.MarketingRuleStatus
import com.cheche365.cheche.core.model.MarketingSuccess
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderGift
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.RuleConfig
import com.cheche365.cheche.core.model.SourceType
import com.cheche365.cheche.core.repository.GiftTypeRepository
import com.cheche365.cheche.core.repository.MarketingRepository
import com.cheche365.cheche.core.repository.MarketingRuleRepository
import com.cheche365.cheche.core.repository.RuleConfigRepository
import com.cheche365.cheche.core.service.GiftService
import com.cheche365.cheche.core.service.PurchaseOrderGiftService
import com.cheche365.cheche.core.service.gift.rules.DefaultGiftRule
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.DecimalFormat

/**
 * Created by mahong on 2016/8/5.
 */
abstract class ConfigurableRule extends DefaultGiftRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurableRule.class);

    static final int CONDITION_NONE = 1 << 1
    static final int CONDITION_PACKAGE = 1 << 2
    static final int CONDITION_DISCOUNTABLE_AMOUNT = 1 << 3

    protected GiftService giftService;
    protected PurchaseOrderGiftService pogService;
    protected MarketingRuleRepository mrRepo;
    protected RuleConfigRepository rcRepo;
    protected MarketingRepository marketingRepo
    protected GiftTypeRepository giftTypeRepo
    protected ConfigurableRule(GiftService giftService, PurchaseOrderGiftService pogService, MarketingRuleRepository mrRepo,
                        RuleConfigRepository rcRepo, MarketingRepository marketingRepo, GiftTypeRepository giftTypeRepo) {
        this.giftService = giftService
        this.pogService = pogService
        this.mrRepo = mrRepo
        this.rcRepo = rcRepo
        this.marketingRepo = marketingRepo
        this.giftTypeRepo = giftTypeRepo
    }
    static final Closure find={ ruleConfigs, type ->
        List<RuleConfig> resultRuleConfigs = ruleConfigs.findAll{ruleConfig -> type ==  ruleConfig.ruleParam}
        (resultRuleConfigs == null || resultRuleConfigs.isEmpty()) ? null : resultRuleConfigs.get(0);
    }

    abstract List<Gift> generateCouponGifts(QuoteRecord quoteRecord, List<RuleConfig> ruleConfigs);
    /***
     * update by wenling on 2017/5/18
     * **/
    public abstract Map<String,RuleConfig> configParams(List<RuleConfig> ruleConfigs);

    abstract boolean support(ActivityType activityType)

    List<PurchaseOrderGift> saveCommonMarketingDiscount(QuoteRecord quoteRecord, PurchaseOrder order) {
        MarketingRule marketingRule = mrRepo.findFirstByAreaAndChannelAndInsuranceCompanyAndStatus(quoteRecord.getArea(), quoteRecord.getChannel(), quoteRecord.getInsuranceCompany(), MarketingRuleStatus.Enum.EFFECTIVE_2);
        if (marketingRule == null) {
            LOGGER.error("根据报价ID:{} 未找到生效中活动", quoteRecord.getId());
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "未找到生效中活动");
        }

        List<RuleConfig> ruleConfigs = rcRepo.findByMarketingRule(marketingRule);
        if (!this.meetRuleConfig(quoteRecord, ruleConfigs)) {
            LOGGER.error("根据报价ID:{} 不满足 活动ID:{} 配置的优惠规则", quoteRecord.getId(), marketingRule.getId());
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "当前优惠活动已失效，请重新报价下单");
        }

        List<Gift> couponGifts = this.generateCouponGifts(quoteRecord, ruleConfigs);
        if (couponGifts != null && couponGifts.size() > 0) {
            couponGifts.each{couponGift ->
                MarketingSuccess marketingSuccess = genMarketingSuccessByGift(couponGift, order);
                couponGift.setSource(marketingSuccessRepository.save(marketingSuccess).getId());
            }
            giftService.save(couponGifts);
        }
        List<Gift> realGifts = this.generateRealGifts(quoteRecord, ruleConfigs);
        if (realGifts != null && realGifts.size() > 0) {
            realGifts.each {realGift ->
                realGift.setSource(order.getId());
                realGift.setSourceType(SourceType.Enum.PURCHASE_ORDER_1);
            }
            giftService.save(realGifts);
        }
        return pogService.assembleAndSavePurchaseOrderGift(order, couponGifts, realGifts);
    }

    @Override
    void beforePlaceOrder(Gift gift, QuoteRecord quoteRecord) {
    }

    @Override
    void processAfterReleaseGift(Gift gift) {
        gift.setStatus(GiftStatus.Enum.CANCLED_5);
    }

    int conditions(){
        CONDITION_PACKAGE | CONDITION_DISCOUNTABLE_AMOUNT
    }

    List<Gift> generateRealGifts(QuoteRecord quoteRecord, List<RuleConfig> ruleConfigs) {
        RuleConfig realGiftRuleConfig = configParams(ruleConfigs).get('otherAdditionalAndRealGift');
        if (realGiftRuleConfig == null) {
            return null;
        }

        String giftConfigStr = "";
        Double fullAmount = 0.0;
        List<Gift> realGifts = new ArrayList<>();
        Double pkgLimitAmount = calculatePkgLimitAmount(quoteRecord, ruleConfigs);
        List<String> realGiftRulePairs = Arrays.asList(realGiftRuleConfig.getRuleValue().split(WebConstants.COMMON_MARKETING_LOGIC_SYMBOL_AND));
        for (String realGiftRulePair : realGiftRulePairs) {
            String[] realGiftRuleArray = realGiftRulePair.split(WebConstants.COMMON_MARKETING_SYMBOL_SPLIT);
            if (Double.valueOf(realGiftRuleArray[0]) <= pkgLimitAmount && Double.valueOf(realGiftRuleArray[0]) >= fullAmount) {
                giftConfigStr = realGiftRuleArray[1];
                fullAmount = Double.valueOf(realGiftRuleArray[0]);
            }
        }
        if (!StringUtils.isBlank(giftConfigStr)) {
            realGifts.add(getRealGift(quoteRecord, giftConfigStr));
        }
        return realGifts;
    }

    MarketingSuccess genMarketingSuccessByGift(Gift gift, PurchaseOrder purchaseOrder) {
        MarketingSuccess marketingSuccess = new MarketingSuccess();
        marketingSuccess.setMarketing(marketingRepo.findFirstByCode(WebConstants.COMMON_MARKETING_CODE));
        marketingSuccess.setMobile(purchaseOrder.getApplicant().getMobile());
        marketingSuccess.setEffectDate(gift.getEffectiveDate());
        marketingSuccess.setFailureDate(gift.getExpireDate());
        marketingSuccess.setAmount(gift.getGiftAmount());
        marketingSuccess.setSourceChannel(purchaseOrder.getSourceChannel().getName());
        marketingSuccess.setSynced(true);
        marketingSuccess.setChannel(purchaseOrder.getSourceChannel());
        return marketingSuccess;
    }

    Boolean meetRuleConfig(QuoteRecord quoteRecord, List<RuleConfig> ruleConfigs) {
        checkPackage(quoteRecord, ruleConfigs)
    }

    private boolean checkPackage(QuoteRecord quoteRecord, List<RuleConfig> ruleConfigs){
        if(!(CONDITION_PACKAGE & conditions())){
            return true
        }
        RuleConfig orderPkgLimitRuleConfig=configParams(ruleConfigs).get('quoteFields');
        if (orderPkgLimitRuleConfig == null) {
            return true;
        }
        if (orderPkgLimitRuleConfig.getRuleValue().contains(String.valueOf(MarketingInsuranceType.Enum.COMPULSORY_1.id))
            && (quoteRecord.getCompulsoryPremium() == null || quoteRecord.getCompulsoryPremium() <= 0)) {
            return false;
        }
        else if (orderPkgLimitRuleConfig.getRuleValue().contains(String.valueOf(MarketingInsuranceType.Enum.COMMERCIAL_2.id))
            && (quoteRecord.getPremium() == null || quoteRecord.getPremium() <= 0)) {
            return false;
        }
        else if (orderPkgLimitRuleConfig.getRuleValue().contains(String.valueOf(MarketingInsuranceType.Enum.AUTO_TAX_3.id))
            && (quoteRecord.getAutoTax() == null || quoteRecord.getAutoTax() <= 0)) {
            return false;
        }
        return true
    }


    Double calculatePkgLimitAmount(QuoteRecord quoteRecord, List<RuleConfig> ruleConfigs) {
        if(!(CONDITION_DISCOUNTABLE_AMOUNT & conditions())){
            return quoteRecord.getTotalPremium();
        }
        RuleConfig calculatePkgLimitRuleConfig=configParams(ruleConfigs).get('fullInsurance');
        if (calculatePkgLimitRuleConfig == null) {
            return quoteRecord.getTotalPremium();
        }
        Double pkgLimitAmount = 0.0;
        if (calculatePkgLimitRuleConfig.getRuleValue().contains(String.valueOf(MarketingInsuranceType.Enum.COMPULSORY_1.id))) {
            pkgLimitAmount += DoubleUtils.doubleValue(quoteRecord.getCompulsoryPremium());
        }
        if (calculatePkgLimitRuleConfig.getRuleValue().contains(String.valueOf(MarketingInsuranceType.Enum.COMMERCIAL_2.id))) {
            pkgLimitAmount += DoubleUtils.doubleValue(quoteRecord.getPremium());
        }
        if (calculatePkgLimitRuleConfig.getRuleValue().contains(String.valueOf(MarketingInsuranceType.Enum.AUTO_TAX_3.id))) {
            pkgLimitAmount += DoubleUtils.doubleValue(quoteRecord.getAutoTax());
        }
        return DoubleUtils.displayDoubleValue(pkgLimitAmount);
    }


    private Gift getRealGift(QuoteRecord quoteRecord, String giftConfigStr) {
        String[] giftData = giftConfigStr.split(WebConstants.COMMON_MARKETING_SYMBOL_GIFT_SPLIT);
        GiftType giftType = giftTypeRepo.findFirstById(Long.valueOf(giftData[0]));
        Gift newGift = getGiftTemplate(quoteRecord, giftType);
        newGift.setStatus(GiftStatus.Enum.WAITDELIVERED_6);
        newGift.setGiftDisplay(new DecimalFormat("0.00").format(Double.valueOf(giftData[1])));
        return newGift;
    }

    Gift getCouponGift(QuoteRecord quoteRecord, Double amount) {
        Gift newGift = getGiftTemplate(quoteRecord, GiftType.Enum.CASH_34);
        newGift.setStatus(GiftStatus.Enum.USED_3);
        newGift.setGiftAmount(amount);
        newGift.setGiftDisplay(new DecimalFormat("0.00").format(amount));
        return newGift;
    }

    Gift getGiftTemplate(QuoteRecord quoteRecord, GiftType giftType) {
        Gift newGift = Gift.genGiftTemplate(quoteRecord.getApplicant(), giftType);
        newGift.setUsageRuleClass(this.getClass().getName());
        return newGift;
    }
}
