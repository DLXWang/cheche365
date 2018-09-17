package com.cheche365.cheche.marketing.service.activity;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.MarketingRuleRepository;
import com.cheche365.cheche.core.repository.RuleConfigRepository;
import com.cheche365.cheche.core.service.gift.ConfigurableRule;
import com.cheche365.cheche.core.service.gift.RuleFactory;
import com.cheche365.cheche.marketing.model.AttendResult;
import com.cheche365.cheche.marketing.service.MarketingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mahong on 2016/8/5.
 */
@Service
@Transactional
public class Service201608002 extends MarketingService {

    private MarketingRuleRepository mrRepo;

    private RuleConfigRepository rcRepo;

    private RuleFactory ruleFactory;

    public Service201608002 (MarketingRuleRepository mrRepo, RuleConfigRepository rcRepo, RuleFactory ruleFactory){
        this.mrRepo = mrRepo;
        this.rcRepo = rcRepo;
        this.ruleFactory = ruleFactory;
    }

    @Override
    protected String activityName() {
        return "运营配置活动";
    }

    @Override
    public void preCheck(Marketing marketing, String mobile, Channel clientType) {
        if (marketing == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "营销活动不存在");
        }
        if (new Date().before(marketing.getBeginDate())) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "活动尚未开始");
        }
        if (new Date().after(marketing.getEndDate())) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "活动已结束");
        }
    }

    @Override
    public Boolean checkSupportInsurancePackage(QuoteRecord quoteRecord) {
        return true;
    }

    @Override
    public Boolean checkSupportMarketing(Marketing marketing, QuoteRecord quoteRecord) {
        if (!super.checkSupportMarketing(marketing, quoteRecord)) {
            return false;
        }

        MarketingRule marketingRule = mrRepo.findFirstByAreaAndChannelAndInsuranceCompanyAndStatus(quoteRecord.getArea(), quoteRecord.getChannel(), quoteRecord.getInsuranceCompany(), MarketingRuleStatus.Enum.EFFECTIVE_2);
        if (quoteRecord.getApplicant() == null || marketingRule == null) {
            return false;
        }

        ConfigurableRule commonMarketingRuleDealClass = ruleFactory.findRuleService(marketingRule.getActivityType().getId());
        List<RuleConfig> ruleConfigs = rcRepo.findByMarketingRule(marketingRule);
        if (!commonMarketingRuleDealClass.meetRuleConfig(quoteRecord, ruleConfigs)) {
            return false;
        }
        marketing.setName(marketingRule.getTitle());
        marketing.setShortName(marketingRule.getTitle());
        marketing.setDescription(marketingRule.getSubTitle());
        return true;
    }

    @Override
    public AttendResult attend(Marketing marketing, User user, Channel channel,  Map<String, Object> context) {
        return null;
    }

    public List<Gift> attend(Marketing marketing, User user, Channel channel, QuoteRecord quoteRecord) {
        MarketingRule marketingRule = mrRepo.findFirstByAreaAndChannelAndInsuranceCompanyAndStatus(quoteRecord.getArea(), quoteRecord.getChannel(), quoteRecord.getInsuranceCompany(), MarketingRuleStatus.Enum.EFFECTIVE_2);
        if (marketingRule == null) {
            return null;
        }
        ConfigurableRule ruleService = ruleFactory.findRuleService(marketingRule.getActivityType().getId());
        List<RuleConfig> ruleConfigs = rcRepo.findByMarketingRule(marketingRule);
        if (!ruleService.meetRuleConfig(quoteRecord, ruleConfigs)) {
            return null;
        }
        return ruleService.generateCouponGifts(quoteRecord, ruleConfigs);
    }
}
