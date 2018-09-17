package com.cheche365.cheche.operationcenter.web.model.marketing;

/**
 * Created by chenxiangyin on 2017/5/16.
 */
public class DiscountByInsurance{
    String insuranceType;
    String notMoreThanInsuranceType;
    Double notMoreThan;
    public Double getNotMoreThan() {   return notMoreThan;  }

    public void setNotMoreThan(Double notMoreThan) {  this.notMoreThan = notMoreThan; }

    public String getInsuranceType() {  return insuranceType;  }

    public void setInsuranceType(String insuranceType) {  this.insuranceType = insuranceType; }

    public String getNotMoreThanInsuranceType() {  return notMoreThanInsuranceType;   }

    public void setNotMoreThanInsuranceType(String notMoreThanInsuranceType) {   this.notMoreThanInsuranceType = notMoreThanInsuranceType;  }

//    @Override
//    public List<RuleConfig> sortRuleConfig(MarketingRuleViewData viewData){
//        return null;
//    }    @Override
//    public List<RuleConfig> sortRuleConfig(MarketingRuleViewData viewData){
//        return null;
//    }
//    @Override
//    public List<String> activityInfo(MarketingRule marketingRule, List<RuleConfig> ruleConfigList){
//        List<String> returnList = new ArrayList<>();
//        List<RuleConfig> ruleConfigList = ruleConfigRepository.findByMarketingRuleOrderByRuleParamAsc(marketingRule);
//
//        for (int i=0;i<ruleConfigList.size();i++) {
//            RuleConfig ruleConfig = ruleConfigList.get(i);
//            if(ruleConfig.getRuleParam().getId() == RuleParam.Enum.REDUCE_FULL_RULE.getId()){
//                String[] ruleList = StringUtil.split(ruleConfig.getRuleValue(),"&&");
//                for(String rule:ruleList){
//                    String ruleConfigWord = "满" + StringUtils.replace(rule,"_","减");
//                    returnList.add(ruleConfigWord);
//                }
//            }else if(ruleConfig.getRuleParam().getId() == RuleParam.Enum.REDUCE_TOP_RULE.getId()){
//                returnList.add("可累计 最高减免" + ruleConfig.getRuleValue() + "元");
//            }else if(ruleConfig.getRuleParam().getId() == RuleParam.Enum.REDUCE_OTHER_SEND_RULE.getId()){
//                returnList.addAll(this.giftList(ruleConfig.getRuleValue(),"再享"));
//            }
//        }
//    }
}
