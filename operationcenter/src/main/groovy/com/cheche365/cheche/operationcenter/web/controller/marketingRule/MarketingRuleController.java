package com.cheche365.cheche.operationcenter.web.controller.marketingRule;

import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.operationcenter.model.MarketingQuery;
import com.cheche365.cheche.operationcenter.service.marketingRule.MarketingRuleService;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.operationcenter.web.model.marketing.DiscountByMoney;
import com.cheche365.cheche.operationcenter.web.model.marketing.MarketingRuleViewData;
import com.cheche365.cheche.operationcenter.web.model.marketing.Present;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenx on 2016/8/4.
 */
@RestController
@RequestMapping("/operationcenter/marketingRule")
public class MarketingRuleController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MarketingRuleService marketingRuleService;

    @RequestMapping(value = "/activityTypeList", method = RequestMethod.GET)
    public List<ActivityType> activityTypeList(){
        return marketingRuleService.activityTypeList();
    }

    @RequestMapping(value = "/insuranceTypeList", method = RequestMethod.GET)
    public List<MarketingInsuranceType> insuranceTypeList(){
        return marketingRuleService.insuranceTypeList();
    }

    @RequestMapping(value = "/giftTypeList", method = RequestMethod.GET)
    public List<GiftType> giftTypeList(){
        return marketingRuleService.giftTypeList();
    }

    @RequestMapping(value = "/extraGiftTypeList", method = RequestMethod.GET)
    public List<GiftType> extraGiftTypeList(){
        return marketingRuleService.extraGiftTypeList();
    }

    @RequestMapping(value = "/statusList", method = RequestMethod.GET)
    public List<MarketingRuleStatus> getStatusList(){
        Iterable<MarketingRuleStatus> statusIterable = MarketingRuleStatus.Enum.ALL;
        List<MarketingRuleStatus> statusList = new ArrayList<MarketingRuleStatus>();
        statusIterable.forEach(
            status ->{
                statusList.add(status);}
        );
        return statusList;
    }

    @RequestMapping(value = "/findOneEdit", method = RequestMethod.GET)
    public MarketingRuleViewData getMarketingRuleEdit(@RequestParam(value = "id", required = true) Long id){
        return marketingRuleService.findMarketingRuleByEdit(id);
    }

    @RequestMapping(value = "/findOneOverview", method = RequestMethod.GET)
    public MarketingRuleViewData getMarketingRuleOverview(@RequestParam(value = "id", required = true) Long id){
        return marketingRuleService.findMarketingRuleByOverview(id);
    }

    @RequestMapping(value = "/historyList", method = RequestMethod.GET)
    public List<MarketingRuleViewData> historyList(@RequestParam(value = "id", required = true) Long id) {
        return marketingRuleService.historyList(id);
    }

    @RequestMapping(value = "/marketingList", method = RequestMethod.GET)
    //@VisitorPermission("op0501")//权限的地方
    public DataTablesPageViewModel<MarketingRuleViewData> marketingList(MarketingQuery query) {
        return marketingRuleList(query);
    }

    public DataTablesPageViewModel<MarketingRuleViewData> marketingRuleList(MarketingQuery query){
        Page<MarketingRule> rulePage = marketingRuleService.findBySpecAndPaginate(query, marketingRuleService.buildPageable(query.getCurrentPage(), query.getPageSize()));
        PageInfo pageInfo=new PageInfo();
        pageInfo.setTotalElements(rulePage.getTotalElements());
        pageInfo.setTotalPage(rulePage.getTotalPages());
        List<MarketingRuleViewData> list = new ArrayList<>();
        for(MarketingRule rule:rulePage.getContent()){
            MarketingRuleViewData data = MarketingRuleViewData.createViewModel(rule);
            List<String> info = marketingRuleService.activityInfo(rule.getId());
            data.setActivityInfo(info);
            list.add(data);
        }
        return new DataTablesPageViewModel<>(rulePage.getTotalElements(),rulePage.getTotalElements(),query.getDraw(),list);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    //@VisitorPermission("or030201")//permission得换
    @Transactional
    public ResultModel add(@Valid MarketingRuleViewData viewData, BindingResult bindingResult) {
        viewData.setDescription(viewData.getDescription()+"|"+viewData.getUserGuide());
        List<RuleConfig> ruleConfigList = this.sortRuleConfig(viewData);
        if(ruleConfigList == null){
            logger.debug("add new marketingRule, validation has error");
            return new ResultModel(false,"请将信息填写完整");
        }
        MarketingShared marketingShared = viewData.getMarketingShared();
        this.saveAdd(ruleConfigList,marketingShared,viewData);
        return new ResultModel(true,"添加成功");
    }

    public MarketingShared sortMarketingShared(MarketingShared marketingShared){
        MarketingShared formatShared = new MarketingShared();
        if(marketingShared.getAlipayShared().equals(true)){
            formatShared.setAlipayShared(true);
            formatShared.setAlipayMainTitle(marketingShared.getAlipayMainTitle());
            formatShared.setAlipaySubTitle(marketingShared.getAlipaySubTitle());
        }else{
            formatShared.setAlipayShared(false);
        }
        if(marketingShared.getWechatShared().equals(true)){
            formatShared.setWechatShared(true);
            formatShared.setWechatMainTitle(marketingShared.getWechatMainTitle());
            formatShared.setWechatSubTitle(marketingShared.getWechatSubTitle());
        }else{
            formatShared.setWechatShared(false);
        }
        formatShared.setSharedIcon(marketingShared.getSharedIcon());
        return formatShared;
    }

    public void saveAdd(List<RuleConfig> ruleConfigList,MarketingShared marketingShared,MarketingRuleViewData marketingRuleViewData){
        String[] areas = StringUtils.split(marketingRuleViewData.getArea(),",");
        String[] channels = StringUtils.split(marketingRuleViewData.getChannel(),",");
        String[] insuranceCompanys = StringUtils.split(marketingRuleViewData.getInsuranceCompany(),",");
        for(String area:areas){
            for(String channel:channels){
                for(String insuranceCompany:insuranceCompanys){
                    //ActivityType activityType = marketingRuleService.findActivityTypeById(Long.parseLong(marketingRuleViewData.getActivityType()));
                    marketingRuleViewData.setArea(area);
                    marketingRuleViewData.setChannel(channel);
                    marketingRuleViewData.setInsuranceCompany(insuranceCompany);
                    marketingRuleService.save(this.sortMarketingShared(marketingShared),marketingRuleViewData,ruleConfigList);
                }
            }
        }
    }

    private List<RuleConfig> setActivityExtra(String[] activityTypeInfos,RuleParam ruleParam,List<Present> extraPresentList){
        List<RuleConfig> ruleConfigList = new ArrayList<RuleConfig>();
        if(activityTypeInfos != null){
            for(int i=0;i<activityTypeInfos.length;i++){
                if(StringUtils.equals(activityTypeInfos[i],"extraPresent")){
                    ruleConfigList.add(new RuleConfig(ruleParam,this.sortDiscountMore(extraPresentList)));
                }
            }
        }
        return ruleConfigList;
    }

    public  List<RuleConfig> sortRuleConfig(MarketingRuleViewData viewData){
        List<RuleConfig> ruleConfigList = new ArrayList<RuleConfig>();
        String[] activityTypeInfos = StringUtils.split(viewData.getActivityTypeInfo(),",");
        if(viewData.getActivityType().equals(ActivityType.Enum.FULL_REDUCE_4.getId().toString())){
            ruleConfigList.addAll(this.setActivityExtra(activityTypeInfos,RuleParam.Enum.REDUCE_OTHER_SEND_RULE_6,viewData.getExtraPresentList()));
            List<DiscountByMoney> discountByMoneyList = viewData.getDiscountByMoneyList();
            StringBuffer discount = new StringBuffer();
            String splitTag = "";
            for(DiscountByMoney discountByMoney:discountByMoneyList){
                //如果输入的数据有误 则直接返回
                if(DiscountByMoney.checkEmpty(discountByMoney)){
                    continue;
                }
                discount.append(splitTag + discountByMoney.getFull() + "_" + discountByMoney.getDiscount());
                splitTag = "&&";
            }
            ruleConfigList.add(new RuleConfig(RuleParam.Enum.REDUCE_FULL_RULE_1,discount.toString()));
            if(viewData.getIsAccumulate() == 1){
                ruleConfigList.add(new RuleConfig(RuleParam.Enum.REDUCE_IS_REGULAR_RULE_2,RuleParam.Enum.TRUE));
                ruleConfigList.add(new RuleConfig(RuleParam.Enum.REDUCE_TOP_RULE_3,viewData.getNotMoreThan().toString()));
            }else{
                ruleConfigList.add(new RuleConfig(RuleParam.Enum.REDUCE_IS_REGULAR_RULE_2,RuleParam.Enum.FALSE));
            }

            ruleConfigList.add(new RuleConfig(RuleParam.Enum.REDUCE_ORDER_PKG_LIMIT_RULE_4,StringUtils.replace(viewData.getInsuranceMust(), ",", "&&")));
            ruleConfigList.add(new RuleConfig(RuleParam.Enum.REDUCE_CALCULATE_PKG_LIMIT_RULE_5,StringUtils.replace(viewData.getFullIncludes(), ",", "&&")));
        }else if(viewData.getActivityType().equals(ActivityType.Enum.FULL_SEND_5.getId().toString())){
            ruleConfigList.addAll(this.setActivityExtra(activityTypeInfos,RuleParam.Enum.SEND_OTHER_SEND_RULE_12,viewData.getExtraPresentList()));
            List<Present> presentList = viewData.getPresentList();
            ruleConfigList.add(new RuleConfig(RuleParam.Enum.SEND_FULL_RULE_7,this.sortDiscountMore(presentList)));
            if(viewData.getIsAccumulate() == 1){
                ruleConfigList.add(new RuleConfig(RuleParam.Enum.SEND_IS_REGULAR_RULE_8,RuleParam.Enum.TRUE));
                ruleConfigList.add(new RuleConfig(RuleParam.Enum.SEND_TOP_RULE_9,viewData.getNotMoreThan().toString()));
            }else{
                ruleConfigList.add(new RuleConfig(RuleParam.Enum.SEND_IS_REGULAR_RULE_8,RuleParam.Enum.FALSE));
            }
            ruleConfigList.add(new RuleConfig(RuleParam.Enum.SEND_ORDER_PKG_LIMIT_RULE_10,StringUtils.replace(viewData.getInsuranceMust(), ",", "&&")));//购买险种限制
            ruleConfigList.add(new RuleConfig(RuleParam.Enum.SEND_CALCULATE_PKG_LIMIT_RULE_11,StringUtils.replace(viewData.getFullIncludes(), ",", "&&")));//满额险种限制
        }else if(viewData.getActivityType().equals(ActivityType.Enum.INSURANCE_PACKAGE_DEDUCT_6.getId().toString())){
            ruleConfigList.addAll(this.setActivityExtra(activityTypeInfos,RuleParam.Enum.DEDUCT_OTHER_SEND_RULE_18,viewData.getExtraPresentList()));
            ruleConfigList.add(new RuleConfig(RuleParam.Enum.DEDUCT_INSURANCE_TYPE_RULE_13,StringUtils.replace(viewData.getDiscountByInsurance().getInsuranceType(),",","&&")));
            ruleConfigList.add(new RuleConfig(RuleParam.Enum.DEDUCT_TOP_PKG_RULE_14,StringUtils.replace(viewData.getDiscountByInsurance().getNotMoreThanInsuranceType(),",","&&")));
            ruleConfigList.add(new RuleConfig(RuleParam.Enum.DEDUCT_PERCENT_RULE_15,viewData.getDiscountByInsurance().getNotMoreThan() + ""));

            ruleConfigList.add(new RuleConfig(RuleParam.Enum.DEDUCT_ORDER_PKG_LIMIT_RULE_16,StringUtils.replace(viewData.getInsuranceMust(), ",", "&&")));//购买险种限制
            ruleConfigList.add(new RuleConfig(RuleParam.Enum.DEDUCT_CALCULATE_PKG_LIMIT_RULE_17,StringUtils.replace(viewData.getFullIncludes(), ",", "&&")));//满额险种限制
        }else if(viewData.getActivityType().equals(ActivityType.Enum.DISCOUNT_SEND_7.getId().toString())){
//            public static  RuleParam DISCOUNT_SEND_CALCULATE_PKG_LIMIT_TYPE_RULE_19;//满额险种限制  【门槛  -> 非必填】
//            public static  RuleParam DISCOUNT_SEND_CALCULATE_PKG_LIMIT_RULE_20;//折扣满额限制  【门槛  -> 非必填】
//            public static  RuleParam DISCOUNT_SEND_INSURANCE_TYPE_RULE_21;//购买险种限制 【必填】
//            public static  RuleParam DISCOUNT_SEND_INSURANCE_TYPE_PERCENT_RULE_22;//赠送险种百分比 【必填】
//            public static  RuleParam DISCOUNT_SEND_TOP_PKG_RULE_23;//最高赠送限制   【封顶  ->非必填】
//            public static  RuleParam DISCOUNT_SEND_OTHER_SEND_RULE_24;//其他附加优惠  【额外赠送  ->非必填】

//            ruleConfigList.addAll(this.setActivityExtra(activityTypeInfos,RuleParam.Enum.DEDUCT_OTHER_SEND_RULE,viewData.getExtraPresentList()));
//            ruleConfigList.add(new RuleConfig(RuleParam.Enum.DEDUCT_INSURANCE_TYPE_RULE,StringUtil.replace(viewData.getDiscountByInsurance().getInsuranceType(),",","&&")));
//            ruleConfigList.add(new RuleConfig(RuleParam.Enum.DEDUCT_TOP_PKG_RULE,StringUtil.replace(viewData.getDiscountByInsurance().getNotMoreThanInsuranceType(),",","&&")));
//            ruleConfigList.add(new RuleConfig(RuleParam.Enum.DEDUCT_PERCENT_RULE,viewData.getDiscountByInsurance().getNotMoreThan() + ""));
            if(!StringUtil.isNull(viewData.getDiscountGift().getNotMoreThanPerc())){
                ruleConfigList.add(new RuleConfig(RuleParam.Enum.DISCOUNT_SEND_CALCULATE_PKG_LIMIT_TYPE_RULE_19,StringUtils.replace(viewData.getDiscountGift().getNotMoreThanInsuranceTypeByGift(),",","&&")));
                ruleConfigList.add(new RuleConfig(RuleParam.Enum.DISCOUNT_SEND_CALCULATE_PKG_LIMIT_RULE_20,viewData.getDiscountGift().getNotMoreThanPerc()));
            }
            List insuranceTypePercentRule = new ArrayList<>();
            if(!StringUtil.isNull(viewData.getDiscountGift().getInsuranceMust())){
                insuranceTypePercentRule.add(MarketingInsuranceType.Enum.COMPULSORY_1.getId() + "_" + viewData.getDiscountGift().getInsuranceMust());
            }
            if(!StringUtil.isNull(viewData.getDiscountGift().getCommercial())){
                insuranceTypePercentRule.add(MarketingInsuranceType.Enum.COMMERCIAL_2.getId() + "_"  + viewData.getDiscountGift().getCommercial());
            }
            if(!StringUtil.isNull(viewData.getDiscountGift().getVehicleTax())){
                insuranceTypePercentRule.add(MarketingInsuranceType.Enum.AUTO_TAX_3.getId() + "_"  + viewData.getDiscountGift().getVehicleTax());
            }
            ruleConfigList.add(new RuleConfig(RuleParam.Enum.DISCOUNT_SEND_INSURANCE_TYPE_PERCENT_RULE_22,StringUtils.join(insuranceTypePercentRule,"&&")));
            if(!StringUtil.isNull(viewData.getDiscountGift().getNotMoreThanMoney())){
                ruleConfigList.add(new RuleConfig(RuleParam.Enum.DISCOUNT_SEND_TOP_PKG_RULE_23,viewData.getDiscountGift().getNotMoreThanMoney()));
            }
            ruleConfigList.addAll(this.setActivityExtra(activityTypeInfos,RuleParam.Enum.DISCOUNT_SEND_OTHER_SEND_RULE_24,viewData.getExtraPresentList()));
//            ruleConfigList.add(new RuleConfig(RuleParam.Enum.DISCOUNT_SEND_INSURANCE_TYPE_RULE_21,insuranceMust));//购买险种限制
//            ruleConfigList.add(new RuleConfig(RuleParam.Enum.DISCOUNT_SEND_CALCULATE_PKG_LIMIT_TYPE_RULE_19,insuranceFull));//满额险种限制
        }
        return  ruleConfigList;
    }

    public String sortDiscountMore(List<Present> presentList){
        StringBuffer discount = new StringBuffer();
        String splitTag = "";
        for(Present present:presentList){
            if(Present.checkEmpty(present)){
                continue;
            }
            discount.append(splitTag);
            discount.append(present.getFull() + "_" + present.getPresent()+"|"+ present.getDiscount());
            splitTag="&&";
        }
        return discount.toString();
    }



    @RequestMapping(value = "/refreshRules", method = RequestMethod.POST)
    public ResultModel refreshRules(@RequestParam(value = "refresh", required = false)String refresh){
        if(StringUtils.isEmpty(refresh)){
            return new ResultModel(false,"刷新失败");
        }else{
            String[] ids = StringUtils.split(refresh,",");
            marketingRuleService.refreshMarketingRules(ids);
            return new ResultModel(true,"刷新成功");
        }
    }
}
