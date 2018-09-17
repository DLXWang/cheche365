package com.cheche365.cheche.operationcenter.service.marketingRule;

import com.alibaba.common.lang.StringUtil;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.operationcenter.model.MarketingQuery;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.operationcenter.web.model.marketing.DiscountByInsurance;
import com.cheche365.cheche.operationcenter.web.model.marketing.DiscountByMoney;
import com.cheche365.cheche.operationcenter.web.model.marketing.DiscountGift;
import com.cheche365.cheche.operationcenter.web.model.marketing.MarketingRuleViewData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.cheche365.cheche.core.model.InsuranceCompany.toInsuranceCompany;

/**
 * Created by chenx on 2016/8/4.
 */

@Service
public class MarketingRuleService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MarketingRuleRepository marketingRuleRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private RuleConfigRepository ruleConfigRepository;
    @Autowired
    private MarketingSharedRepository marketingSharedRepository;

    @Autowired
    private MarketingRuleStatusRepository marketingRuleStatusRepository;

    @Autowired
    private MarketingRepository marketingRepository;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private InternalUserManageService internalUserManageService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MarketingInsuranceTypeRepository marketingInsuranceTypeRepository;

    @Autowired
    private ActivityTypeRepository activityTypeRepository;

    @Autowired
    private GiftTypeRepository giftTypeRepository;
    private static final String OPERATION_CENTER_REFRESH_MARKETINGRULE="operationcenter:controller:refresh:marketingRule";

    public MarketingRuleViewData findMarketingRuleByEdit(Long marketingRuleId){
        MarketingRule marketingRule = marketingRuleRepository.findOne(marketingRuleId);
        MarketingRuleViewData returnData = this.findOne(marketingRuleId);
        List<RuleConfig> ruleConfigList = ruleConfigRepository.findByMarketingRule(marketingRule);
        MarketingRuleViewData viewData = new MarketingRuleViewData();
        returnData.setChannelId(marketingRule.getChannel().getId().toString());
        returnData.setInsuranceCompanyId(marketingRule.getInsuranceCompany().getId().toString());
        returnData.setAreaId(marketingRule.getArea().getId().toString());
        if(returnData.getActivityType().equals(ActivityType.Enum.FULL_REDUCE_4.getId().toString())){
            viewData = this.discountByMoneyForm(ruleConfigList);
            returnData.setDiscountByMoneyList(viewData.getDiscountByMoneyList());
            returnData.setExtraPresentList(viewData.getExtraPresentList());
            returnData.setIsAccumulate(viewData.getIsAccumulate());
            returnData.setNotMoreThan(viewData.getNotMoreThan());
        }else if(returnData.getActivityType().equals(ActivityType.Enum.FULL_SEND_5.getId().toString())){
            viewData = this.presentForm(ruleConfigList);
            returnData.setPresentList(viewData.getPresentList());
            returnData.setExtraPresentList(viewData.getExtraPresentList());
            returnData.setIsAccumulate(viewData.getIsAccumulate());
            returnData.setNotMoreThan(viewData.getNotMoreThan());
        }else if(returnData.getActivityType().equals(ActivityType.Enum.INSURANCE_PACKAGE_DEDUCT_6.getId().toString())){
            viewData = this.discountByInsuranceForm(ruleConfigList);
            returnData.setDiscountByInsurance(viewData.getDiscountByInsurance());
            returnData.setExtraPresentList(viewData.getExtraPresentList());
        }else if(returnData.getActivityType().equals(ActivityType.Enum.DISCOUNT_SEND_7.getId().toString())){
            viewData = this.discountGiftForm(ruleConfigList);
            returnData.setPresentList(viewData.getPresentList());
            returnData.setExtraPresentList(viewData.getExtraPresentList());
            returnData.setDiscountGift(viewData.getDiscountGift());
            return returnData;
        }
        returnData.setFullIncludes(StringUtil.replace(viewData.getFullIncludes(),"&&",","));
        returnData.setInsuranceMust(StringUtil.replace(viewData.getInsuranceMust(),"&&",","));
        return returnData;
    }

    public MarketingRuleViewData findMarketingRuleByOverview(Long marketingRuleId){
        MarketingRuleViewData marketingRuleViewData = this.findOne(marketingRuleId);
        marketingRuleViewData.setActivityInfo(this.activityInfo(marketingRuleId));
        if(!marketingRuleViewData.getActivityType().equals(ActivityType.Enum.DISCOUNT_SEND_7.getId().toString())){
            MarketingRuleViewData fullAndLimit =  this.discountByInsuranceTypeInfo(marketingRuleId);
            marketingRuleViewData.setFullIncludes(fullAndLimit.getFullIncludes());
            marketingRuleViewData.setInsuranceMust(fullAndLimit.getInsuranceMust());
        }
        return marketingRuleViewData;
    }

    private MarketingRuleViewData findOne(Long marketingRuleId){
        MarketingRule marketingRule = marketingRuleRepository.findOne(marketingRuleId);
        MarketingRuleViewData marketingRuleViewData = MarketingRuleViewData.createViewModel(marketingRule);
        String basePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getIosPath());
        if(StringUtil.isNotBlank(marketingRule.getTopImage())){
            marketingRuleViewData.setTopImage(
                resourceService.absoluteUrl(basePath,marketingRule.getTopImage()));
        }
        MarketingShared shared = marketingSharedRepository.findFirstByMarketingRule(marketingRule);
        if(StringUtil.isNotBlank(shared.getSharedIcon())){
            shared.setSharedIcon(
                resourceService.absoluteUrl(basePath,shared.getSharedIcon()));
        }
        marketingRuleViewData.setMarketingShared(shared);
        return marketingRuleViewData;
    }

    /**
     * list by page 查询marketing rule list
     *
     * @param marketingQuery
     * @param pageable
     * @return
     */
    public Page<MarketingRule> findBySpecAndPaginate(MarketingQuery marketingQuery, Pageable pageable) {
        return marketingRuleRepository.findAll(new Specification<MarketingRule>() {
            @Override
            public Predicate toPredicate(Root<MarketingRule> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<MarketingRule> criteriaQuery = cb.createQuery(MarketingRule.class);
                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(marketingQuery.getTitle())) {
                    Path<String> title = root.get("title");
                    predicateList.add(cb.like(title,"%"+ marketingQuery.getTitle() + "%"));
                }
                if(StringUtils.isNotBlank(marketingQuery.getActivityType()) ){
                    Path<String> activityType = root.get("activityType");
                    predicateList.add(cb.equal(activityType, activityTypeRepository.findOne(Long.parseLong(marketingQuery.getActivityType()))));
                }
                if(StringUtils.isNotBlank(marketingQuery.getStatus())){
                    Path<String> status = root.get("status");
                    predicateList.add(cb.equal(status, marketingRuleStatusRepository.findOne(Long.parseLong(marketingQuery.getStatus()))));
                }
                if(StringUtils.isNotBlank(marketingQuery.getArea())){
                    Path<String> area = root.get("area");
                    predicateList.add(cb.equal(area, areaRepository.findOne(Long.parseLong(marketingQuery.getArea()))));
                }
                if(StringUtils.isNotBlank(marketingQuery.getChannelId())){
                    Path<String> channel = root.get("channel");
                    predicateList.add(cb.equal(channel, Channel.toChannel(Long.parseLong(marketingQuery.getChannelId()))));
                }else if(StringUtils.isNotBlank(marketingQuery.getChannel())){
                    if(!marketingQuery.getChannel().equals("all")){
                        List<Channel> channelList = new ArrayList<>();
                        Path<Long> channelPath = root.get("channel").get("id");
                        CriteriaBuilder.In<Long> sourceChannelIn = cb.in(channelPath);
                        if(marketingQuery.getChannel().equals("official")){
                            channelList = Channel.self();
                        }else if(marketingQuery.getChannel().equals("thirdParty")){
                            channelList = Channel.thirdPartnerChannels();
                        }
                        channelList.forEach(channel -> sourceChannelIn.value(channel.getId()));
                        predicateList.add(sourceChannelIn);
                    }
                }
                if(StringUtils.isNotBlank(marketingQuery.getInsuranceCompany())){
                    Path<String> insuranceCompany = root.get("insuranceCompany");
                    predicateList.add(cb.equal(insuranceCompany, Long.parseLong(marketingQuery.getInsuranceCompany())));
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    /**
     * create pageable
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    public Pageable buildPageable(Integer currentPage, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    //统一类 修改添加时候save
    private MarketingRule editRule(MarketingRule hasRule, MarketingShared marketingShared, Area area, Channel channel, InsuranceCompany insuranceCompany, String title, String subtitle, String description){
        hasRule.setOperator(internalUserManageService.getCurrentInternalUser());
        hasRule.setArea(area);
        hasRule.setChannel(channel);
        hasRule.setInsuranceCompany(insuranceCompany);
        hasRule.setTitle(title);
        hasRule.setSubTitle(subtitle);
        hasRule.setDescription(description);
        if(StringUtil.isNotBlank(hasRule.getTopImage())){
            hasRule.setTopImage(StringUtil.substringAfter(hasRule.getTopImage(),resourceService.getProperties().getIosPath()));
        }

        //版本号
        MarketingRule versionRule = marketingRuleRepository.findFirstByAreaAndChannelAndInsuranceCompanyOrderByVersionDesc(area,channel,insuranceCompany);
        if(versionRule != null){
            hasRule.setVersion(versionRule.getVersion() + 1);
        }else{
            hasRule.setVersion(0);
        }

        MarketingShared hasShared = new MarketingShared();
        if(hasRule.getId() != null){
            hasShared = marketingSharedRepository.findFirstByMarketingRule(marketingRuleRepository.findOne(hasRule.getId()));
        }
        if(StringUtil.isNotBlank(marketingShared.getSharedIcon())){
            hasShared.setSharedIcon(StringUtil.substringAfter(marketingShared.getSharedIcon(),resourceService.getProperties().getIosPath()));
        }
        hasShared.setWechatShared(marketingShared.getWechatShared());
        hasShared.setWechatMainTitle(marketingShared.getWechatMainTitle());
        hasShared.setWechatSubTitle(marketingShared.getWechatSubTitle());
        hasShared.setAlipayShared(marketingShared.getAlipayShared());
        hasShared.setAlipayMainTitle(marketingShared.getAlipayMainTitle());
        hasShared.setAlipaySubTitle(marketingShared.getAlipaySubTitle());
        hasShared.setMarketingRule(hasRule);
        try{
            marketingRuleRepository.save(hasRule);
            marketingSharedRepository.save(hasShared);
        }catch (Exception e){
            e.printStackTrace();
        }
        return hasRule;
    }
    /**
     * 用在新建时
     * @param ruleConfigList
     * @param marketingShared
     * @param data
     * @return
     */
    public Long save( MarketingShared marketingShared,MarketingRuleViewData data,List<RuleConfig> ruleConfigList){
        Area area = areaRepository.findById(Long.parseLong(data.getArea()));
        Channel channel = Channel.toChannel(Long.parseLong(data.getChannel()));
        InsuranceCompany insuranceCompany = toInsuranceCompany(Long.parseLong(data.getInsuranceCompany()));

        MarketingRule hasRule = marketingRuleRepository.findFirstByAreaAndChannelAndInsuranceCompanyAndStatus(area,channel,insuranceCompany,MarketingRuleStatus.Enum.PRE_EFFECTIVE_1);
        ActivityType activityType = ActivityType.Enum.getActivityTypeById(Long.parseLong(data.getActivityType()));
        if(hasRule != null){
            ruleConfigRepository.deleteByMarketingRule(hasRule);
        }else{
            hasRule = new MarketingRule();
            hasRule.setStatus(MarketingRuleStatus.Enum.PRE_EFFECTIVE_1);
            hasRule.setCreateTime(new Date());
        }
        hasRule.setActivityType(activityType);
        hasRule.setTopImage(data.getTopImage());
        hasRule.setUpdate_time(new Date());
        hasRule.setEffectiveDate(data.getEffectiveDate() != null ?
            DateUtils.getDate(data.getEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN) : null);
        hasRule.setExpireDate(data.getExpireDate() != null ?
            DateUtils.getDate(data.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN) : null);
        hasRule.setMarketing(marketingRepository.findFirstByCode(WebConstants.COMMON_MARKETING_CODE));
        this.editRule(hasRule,marketingShared,area,channel,insuranceCompany,data.getTitle(),data.getSubTitle(),data.getDescription());
        List<RuleConfig> currentConfigList = new ArrayList<>();
        for(RuleConfig ruleConfig:ruleConfigList){
            RuleConfig currentConfig = new RuleConfig(ruleConfig.getRuleParam(),ruleConfig.getRuleValue());
            currentConfig.setMarketingRule(hasRule);
            currentConfigList.add(currentConfig);
        }
        ruleConfigRepository.save(currentConfigList);
        return null;
    }

    public List<MarketingRuleViewData> historyList(Long id){
        MarketingRule hasRule = marketingRuleRepository.findOne(id);
        if(hasRule == null){
            return null;
        }else{
            List<MarketingRuleViewData> marketingRuleViewDataList = new ArrayList<>();
            List<MarketingRule> ruleList = marketingRuleRepository.findByAreaAndChannelAndInsuranceCompanyOrderByCreateTimeDesc(hasRule.getArea(),hasRule.getChannel(),hasRule.getInsuranceCompany());
            for(int i =0;i<ruleList.size();i++){
                MarketingRule marketingRule =  ruleList.get(i);
                MarketingRuleViewData marketingRuleViewData = MarketingRuleViewData.createViewModel(marketingRule);
                marketingRuleViewData.setMarketingShared(marketingSharedRepository.findFirstByMarketingRule(marketingRule));
                marketingRuleViewData.setActivityInfo(this.activityInfo(marketingRule.getId()));
                marketingRuleViewDataList.add(marketingRuleViewData);
            }
            return marketingRuleViewDataList;
        }
    }

    public List<String> activityInfo(Long marketingRuleId){
        List<String> returnList = new ArrayList<>();
        MarketingRule marketingRule = marketingRuleRepository.findOne(marketingRuleId);
        List<RuleConfig> ruleConfigList = ruleConfigRepository.findByMarketingRuleOrderByRuleParamAsc(marketingRule);
        if(marketingRule.getActivityType().getId().equals(ActivityType.Enum.FULL_REDUCE_4.getId())){
            for (int i=0;i<ruleConfigList.size();i++) {
                RuleConfig ruleConfig = ruleConfigList.get(i);
                if(ruleConfig.getRuleParam().equals(RuleParam.Enum.REDUCE_FULL_RULE_1)){
                    String[] ruleList = StringUtil.split(ruleConfig.getRuleValue(),"&&");
                    for(String rule:ruleList){
                        String ruleConfigWord = "满" + StringUtils.replace(rule,"_","减");
                        returnList.add(ruleConfigWord);
                    }
                }else if(ruleConfig.getRuleParam().equals(RuleParam.Enum.REDUCE_TOP_RULE_3)){
                    returnList.add("可累计 最高减免" + ruleConfig.getRuleValue() + "元");
                }else if(ruleConfig.getRuleParam().equals(RuleParam.Enum.REDUCE_OTHER_SEND_RULE_6)){
                    returnList.addAll(this.giftList(ruleConfig.getRuleValue(),"再享"));
                }
            }
        }else if(marketingRule.getActivityType().getId().equals( ActivityType.Enum.FULL_SEND_5.getId())){
            for (int i=0;i<ruleConfigList.size();i++) {
                RuleConfig ruleConfig = ruleConfigList.get(i);
                if(ruleConfig.getRuleParam().equals(RuleParam.Enum.SEND_FULL_RULE_7)){
                    returnList.addAll(this.giftList(ruleConfig.getRuleValue()));
                }else if(ruleConfig.getRuleParam().equals(RuleParam.Enum.SEND_TOP_RULE_9)){
                    returnList.add("最高赠送" + ruleConfig.getRuleValue() + "元礼品");
                }else if(ruleConfig.getRuleParam().equals(RuleParam.Enum.SEND_OTHER_SEND_RULE_12)){
                    returnList.addAll(this.giftList(ruleConfig.getRuleValue(),"再享"));
                }
            }
        }else if(marketingRule.getActivityType().getId().equals(ActivityType.Enum.INSURANCE_PACKAGE_DEDUCT_6.getId())){
            StringBuffer formatInsurancePercent = new StringBuffer();
            for (int i=0;i<ruleConfigList.size();i++) {
                RuleConfig ruleConfig = ruleConfigList.get(i);
                if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.DEDUCT_INSURANCE_TYPE_RULE_13)){
                    returnList.add("减免的险种 " + this.insuranceTypeList(ruleConfig.getRuleValue()));
                }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.DEDUCT_TOP_PKG_RULE_14)){
                    formatInsurancePercent.append("最高不超过 " + this.insuranceTypeList(ruleConfig.getRuleValue()));
                }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.DEDUCT_PERCENT_RULE_15)){
                    formatInsurancePercent.append(" 的 " + ruleConfig.getRuleValue() + "%");
                    returnList.add(formatInsurancePercent.toString());
                }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.DEDUCT_OTHER_SEND_RULE_18)){
                    returnList.addAll(this.giftList(ruleConfig.getRuleValue(),"再享"));
                }
            }
        }else if(marketingRule.getActivityType().getId().equals(ActivityType.Enum.DISCOUNT_SEND_7.getId())){
            for (int i=0;i<ruleConfigList.size();i++) {
                RuleConfig ruleConfig = ruleConfigList.get(i);
                if (ruleConfig.getRuleParam().equals(RuleParam.Enum.DISCOUNT_SEND_CALCULATE_PKG_LIMIT_TYPE_RULE_19)) {
                    returnList.add("门槛的险种 " + this.insuranceTypeList(ruleConfig.getRuleValue()));
                }else if(ruleConfig.getRuleParam().equals( RuleParam.Enum.DISCOUNT_SEND_CALCULATE_PKG_LIMIT_RULE_20)) {
                    returnList.add("门槛钱数 " + ruleConfig.getRuleValue() + "元");
                }else if(ruleConfig.getRuleParam().equals(RuleParam.Enum.DISCOUNT_SEND_INSURANCE_TYPE_PERCENT_RULE_22)) {
                    StringBuffer returnWords = new StringBuffer();
                    returnWords.append("优惠:");
                    String[] ruleList = StringUtil.split(ruleConfig.getRuleValue(),"&&");
                    for(String rule:ruleList){
                        returnWords.append("赠送 ");
                        String before = StringUtil.substringBefore(rule,"_");
                        String after = StringUtil.substringAfter(rule,"_");
                        if(before.equals(MarketingInsuranceType.Enum.COMPULSORY_1.getId().toString())){
                            returnWords.append("交强险的" + after + "% 现金； ");
                        }else if(before.equals(MarketingInsuranceType.Enum.COMMERCIAL_2.getId().toString())){
                            returnWords.append("商业险的" + after + "% 现金； ");
                        }else if(before.equals(MarketingInsuranceType.Enum.AUTO_TAX_3.getId().toString())){
                            returnWords.append("车船税的" + after + "% 现金； ");
                        }
                    }
                    returnList.add(returnWords.toString());
                }else if(ruleConfig.getRuleParam().equals( RuleParam.Enum.DISCOUNT_SEND_TOP_PKG_RULE_23)) {//封顶
                    returnList.add("封顶 最高赠送" + ruleConfig.getRuleValue() + "元");
                }else if(ruleConfig.getRuleParam().equals(RuleParam.Enum.DISCOUNT_SEND_OTHER_SEND_RULE_24)) {//其他附加优惠
                    returnList.addAll(this.giftList(ruleConfig.getRuleValue(),"再享"));
                }
            }
        }
        return returnList;
    }

    private List<String> giftList(String giftValue){
        return this.giftList(giftValue,"");
    }

    private List<String> giftList(String giftValue,String prefix){
        List<String> returnList = new ArrayList<>();
        String[] ruleList = StringUtil.split(giftValue,"&&");
        for(String rule:ruleList){
            String full = StringUtils.substringBefore(rule,"_");
            String discount = StringUtils.substringAfter(rule,"|");
            String giftIdStr = StringUtils.substringBetween(rule,"_","|");
            if(giftIdStr.equals("null") || giftIdStr.equals("0")){
                continue;
            }
            Long giftId = Long.parseLong(giftIdStr);
            GiftType giftType = getGift(giftId);
            returnList.add(prefix + "满" + full + "送" + discount + "元" + giftType.getName());
        }
        return returnList;
    }

    public MarketingRuleViewData discountByInsuranceTypeInfo(Long marketingRuleId){
        RuleConfig must = new RuleConfig();
        RuleConfig full = new RuleConfig();
        try{
            must = ruleConfigRepository.findByInsuranceTypes(marketingRuleId,Arrays.asList(RuleParam.Enum.MUST_BY_INSURANCE_TYPES));
            full = ruleConfigRepository.findByInsuranceTypes(marketingRuleId, Arrays.asList(RuleParam.Enum.FULL_BY_INSURANCE_TYPES));
        }catch(Exception e){
            logger.error(" getDiscountInsuranceInfo marketingRuleId: ->{}",marketingRuleId,e);
        }
        MarketingRuleViewData data = new MarketingRuleViewData();
        data.setInsuranceMust(this.insuranceTypeList(must.getRuleValue()));
        data.setFullIncludes(this.insuranceTypeList(full.getRuleValue()));
        return data;
    }

    //通过2&&3&&4获取保险列表
    private String insuranceTypeList(String insuranceTypes){
        List<String> returnStr = new ArrayList<>();
        String[] typeList = StringUtil.split(insuranceTypes,"&&");
        for(String type:typeList){
            MarketingInsuranceType insuranceType = MarketingInsuranceType.Enum.getMarketingInsuranceTypeById(Long.parseLong(type));
            returnStr.add(insuranceType.getName());
        }
        return StringUtils.join(returnStr,"," );
    }

    //修改时获取discount内容
    public MarketingRuleViewData discountByMoneyForm(List<RuleConfig> ruleConfigList){
        MarketingRuleViewData returnData = new MarketingRuleViewData();
        List<DiscountByMoney> discountByMoneyList = new ArrayList<>();
        returnData.setIsAccumulate(0);

        for (int i=0;i<ruleConfigList.size();i++) {
            RuleConfig ruleConfig = ruleConfigList.get(i);
            if(ruleConfig.getRuleParam()  .equals( RuleParam.Enum.REDUCE_FULL_RULE_1)){
                String[] ruleList = StringUtil.split(ruleConfig.getRuleValue(),"&&");
                for(String rule:ruleList){
                    DiscountByMoney discountByMoney = new DiscountByMoney();
                    discountByMoney.setFull(Double.parseDouble(StringUtil.substringBefore(rule,"_")));
                    discountByMoney.setDiscount(Double.parseDouble(StringUtil.substringAfter(rule,"_")));
                    discountByMoneyList.add(discountByMoney);
                }
                returnData.setDiscountByMoneyList(discountByMoneyList);
            }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.REDUCE_TOP_RULE_3)){
                returnData.setIsAccumulate(1);
                returnData.setNotMoreThan(Double.parseDouble(ruleConfig.getRuleValue()));
            }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.REDUCE_OTHER_SEND_RULE_6)){
                returnData.setExtraPresentList(ruleConfig.getRuleValue());
            }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.REDUCE_ORDER_PKG_LIMIT_RULE_4)){
                returnData.setInsuranceMust(ruleConfig.getRuleValue());
            }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.REDUCE_CALCULATE_PKG_LIMIT_RULE_5)){
                returnData.setFullIncludes(ruleConfig.getRuleValue());
            }
        }
        return returnData;
    }

    //discountByInsurance内容
    public MarketingRuleViewData discountByInsuranceForm(List<RuleConfig> ruleConfigList){
        MarketingRuleViewData returnData = new MarketingRuleViewData();
        DiscountByInsurance discountByInsurance = new DiscountByInsurance();
        for (int i=0;i<ruleConfigList.size();i++) {
            RuleConfig ruleConfig = ruleConfigList.get(i);
            if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.DEDUCT_INSURANCE_TYPE_RULE_13)){
                discountByInsurance.setInsuranceType(StringUtil.replace(ruleConfig.getRuleValue(),"&&",","));
            }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.DEDUCT_TOP_PKG_RULE_14)){
                discountByInsurance.setNotMoreThanInsuranceType(StringUtil.replace(ruleConfig.getRuleValue(),"&&",","));
            }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.DEDUCT_PERCENT_RULE_15)){
                discountByInsurance.setNotMoreThan(Double.parseDouble(ruleConfig.getRuleValue()));
            }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.DEDUCT_OTHER_SEND_RULE_18)){
                returnData.setExtraPresentList(ruleConfig.getRuleValue());
            }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.DEDUCT_ORDER_PKG_LIMIT_RULE_16)){
                returnData.setInsuranceMust(ruleConfig.getRuleValue());
            }else if(ruleConfig.getRuleParam() .equals( RuleParam.Enum.DEDUCT_CALCULATE_PKG_LIMIT_RULE_17)){
                returnData.setFullIncludes(ruleConfig.getRuleValue());
            }
        }
        returnData.setDiscountByInsurance(discountByInsurance);
        return returnData;
    }

    //修改时获取送礼内容
    public MarketingRuleViewData presentForm(List<RuleConfig> ruleConfigList){
        MarketingRuleViewData returnData = new MarketingRuleViewData();
        returnData.setIsAccumulate(0);
        for (int i=0;i<ruleConfigList.size();i++) {
            RuleConfig ruleConfig = ruleConfigList.get(i);
            if (ruleConfig.getRuleParam().equals( RuleParam.Enum.SEND_FULL_RULE_7)) {
                returnData.setPresentList(ruleConfig.getRuleValue());
            } else if (ruleConfig.getRuleParam().equals(RuleParam.Enum.SEND_TOP_RULE_9)) {
                returnData.setIsAccumulate(1);
                returnData.setNotMoreThan(Double.parseDouble(ruleConfig.getRuleValue()));
            } else if (ruleConfig.getRuleParam().equals(RuleParam.Enum.SEND_OTHER_SEND_RULE_12)) {
                returnData.setExtraPresentList(ruleConfig.getRuleValue());
            }else if(ruleConfig.getRuleParam().equals( RuleParam.Enum.SEND_ORDER_PKG_LIMIT_RULE_10)){
                returnData.setInsuranceMust(ruleConfig.getRuleValue());
            }else if(ruleConfig.getRuleParam().equals(RuleParam.Enum.SEND_CALCULATE_PKG_LIMIT_RULE_11)){
                returnData.setFullIncludes(ruleConfig.getRuleValue());
            }
        }
        return returnData;
    }

    //修改时获取送礼内容
    public MarketingRuleViewData discountGiftForm(List<RuleConfig> ruleConfigList){
        DiscountGift discountGift = new DiscountGift();
        MarketingRuleViewData returnData = new MarketingRuleViewData();
        for (int i=0;i<ruleConfigList.size();i++) {
            RuleConfig ruleConfig = ruleConfigList.get(i);
            if (ruleConfig.getRuleParam().equals(RuleParam.Enum.DISCOUNT_SEND_CALCULATE_PKG_LIMIT_TYPE_RULE_19)) {
                discountGift.setNotMoreThanInsuranceTypeByGift(StringUtil.replace(ruleConfig.getRuleValue(),"&&",","));
            }else if(ruleConfig.getRuleParam().equals( RuleParam.Enum.DISCOUNT_SEND_CALCULATE_PKG_LIMIT_RULE_20)) {
                discountGift.setNotMoreThanPerc(ruleConfig.getRuleValue());
            }else if(ruleConfig.getRuleParam().equals(RuleParam.Enum.DISCOUNT_SEND_INSURANCE_TYPE_PERCENT_RULE_22)) {
                String[] ruleList = StringUtil.split(ruleConfig.getRuleValue(),"&&");
                for(String rule:ruleList){
                    String before = StringUtil.substringBefore(rule,"_");
                    String after = StringUtil.substringAfter(rule,"_");
                    if(before.equals(MarketingInsuranceType.Enum.COMPULSORY_1.getId().toString())){
                        discountGift.setInsuranceMust(after);
                    }else if(before.equals(MarketingInsuranceType.Enum.COMMERCIAL_2.getId().toString())){
                        discountGift.setCommercial(after);
                    }else if(before.equals(MarketingInsuranceType.Enum.AUTO_TAX_3.getId().toString())){
                        discountGift.setVehicleTax(after);
                    }
                }
            }else if(ruleConfig.getRuleParam().equals(RuleParam.Enum.DISCOUNT_SEND_TOP_PKG_RULE_23)) {//其他附加优惠
                discountGift.setNotMoreThanMoney(ruleConfig.getRuleValue());
            }else if(ruleConfig.getRuleParam().equals(RuleParam.Enum.DISCOUNT_SEND_OTHER_SEND_RULE_24)) {//其他附加优惠
                returnData.setExtraPresentList(ruleConfig.getRuleValue());
            }
        }
        returnData.setDiscountGift(discountGift);
        return returnData;
    }


    public void refreshMarketingRules(String[] ruleIds) {
        stringRedisTemplate.opsForList().leftPush(OPERATION_CENTER_REFRESH_MARKETINGRULE, CacheUtil.doJacksonSerialize(Arrays.asList(ruleIds)));
    }

    public List<ActivityType> activityTypeList(){
        return activityTypeRepository.findAll();
    }

    public List<MarketingInsuranceType> insuranceTypeList(){
        List<MarketingInsuranceType> returnList = new ArrayList<>();
        Iterable<MarketingInsuranceType> marketingInsuranceTypeItera = marketingInsuranceTypeRepository.findAll();
        marketingInsuranceTypeItera.forEach(insuranceType -> returnList.add(insuranceType));
        return returnList;
    }

    public List<GiftType> giftTypeList(){
        return giftTypeRepository.findMarketingRuleGift();
    }
    public List<GiftType> extraGiftTypeList(){
        return giftTypeRepository.findMarketingRuleExtraGift();
    }

    public GiftType getGift(Long id){
        return giftTypeRepository.findOne(id);
    }
}
