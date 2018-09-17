package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.agent.ChannelAgentPurchaseOrderRebate;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.InstitutionRebateHistoryService;
import com.cheche365.cheche.core.service.PurchaseOrderGiftService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.scheduletask.model.ChebaoyiLedgerInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 车保易台账
 * Created by liulu on 2018/7/30.
 */
@Service
public class ChebaoyiLedgerService {

    private Logger logger = LoggerFactory.getLogger(ChebaoyiLedgerService.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private PurchaseOrderAttributeRepository purchaseOrderAttributeRepository;
    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;
    @Autowired
    private InsuranceRepository insuranceRepository;
    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;
    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;
    @Autowired
    private ChannelAgentPurchaseOrderRebateRepository channelAgentPurchaseOrderRebateRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private InstitutionRebateHistoryService institutionRebateHistoryService;
    @Autowired
    private InsurancePurchaseOrderRebateRepository insurancePurchaseOrderRebateRepository;

    public Map<String, List<ChebaoyiLedgerInfo>> getContentParam(Date startTime, Date endTime) {
        List<Channel> toAChannels = Channel.agents();
        //查询toA当天出单成功台账信息
        List<PurchaseOrder> chebaoyiList = purchaseOrderRepository.findChebaoyi(startTime, endTime);
        List<ChebaoyiLedgerInfo> resultList = new ArrayList<ChebaoyiLedgerInfo>();
        logger.debug("车保易报表：{}至{}时期内查的数据{}条", startTime, endTime, chebaoyiList.size());
        for (PurchaseOrder po : chebaoyiList) {
            //判断toa渠道
            if (toAChannels.contains(po.getSourceChannel())) {
                String prov = "";
                String city = "";
                String district = "";
                //拼接邮件所需字段
                ChebaoyiLedgerInfo flag = new ChebaoyiLedgerInfo();
                logger.debug("车保易报表：构建订单{}开始", po.getOrderNo());
                //付款单位
                //备注

                List<ChannelAgentPurchaseOrderRebate> cprs = channelAgentPurchaseOrderRebateRepository.findByPurchaseOrder(po.getId());
                Double compulsoryRebate = 0.0;
                Double commercialRebate = 0.0;
                Double amount = 0.0;
                for (ChannelAgentPurchaseOrderRebate cpr : cprs) {
                    amount = rebateAmount(amount, cpr.getCommercialAmount());
                    amount = rebateAmount(amount, cpr.getCompulsoryAmount());
                    compulsoryRebate = rebateAmount(compulsoryRebate, cpr.getCompulsoryRebate());
                    commercialRebate = rebateAmount(commercialRebate, cpr.getCommercialRebate());
                }
                //交强点位
                flag.setCompulsoryPointLocation(compulsoryRebate.toString());
                //商业点位
                flag.setCommecialPointLocation(commercialRebate.toString());
                //点位优惠金额
                flag.setPointLocationSum(amount.toString());

                if (cprs.size() > 0) {
                    //销售总监姓名
                    flag.setSalesDirector(cprs.get(0).getChannelAgent().getUser().getName());
                    //销售总监交强点位
                    flag.setDirectorCompulsoryPointLocation(String.valueOf(cprs.get(0).getCompulsoryRebate()));
                    //销售总监商业点位
                    flag.setDirectorCommecialPointLocation(String.valueOf(cprs.get(0).getCommercialRebate()));
                    //销售总监钱包入账金额
                    flag.setSalesDirectorBillingSum(rebateAmountString(cprs.get(0).getCommercialAmount(), cprs.get(0).getCompulsoryAmount()));
                }
                if (cprs.size() > 1) {
                    //销售经理姓名
                    flag.setSalesManager(cprs.get(1).getChannelAgent().getUser().getName());
                    //销售经理交强点位
                    flag.setManagerCompulsoryPointLocation(String.valueOf(cprs.get(1).getCompulsoryRebate()));
                    //销售经理商业点位
                    flag.setManagerCommecialPointLocation(String.valueOf(cprs.get(1).getCommercialRebate()));
                    //销售经理钱包入账金额
                    flag.setSalesManagerBillingSum(rebateAmountString(cprs.get(1).getCommercialAmount(), cprs.get(1).getCompulsoryAmount()));
                }
                if (cprs.size() > 2) {
                    //业务员姓名
                    flag.setSalesman(cprs.get(2).getChannelAgent().getUser().getName());
                    //业务员交强点位
                    flag.setSalesmanCompulsoryPointLocation(String.valueOf(cprs.get(2).getCompulsoryRebate()));
                    //业务员商业点位
                    flag.setSalesmanCommecialPointLocation(String.valueOf(cprs.get(2).getCommercialRebate()));
                    //业务员钱包入账金额
                    flag.setSalesmanBillingSum(rebateAmountString(cprs.get(2).getCommercialAmount(), cprs.get(2).getCompulsoryAmount()));
                }
                OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(po);
                flag.setIssueTime(DateUtils.getDateString(orderOperationInfo.getConfirmOrderDate(), DateUtils.DATE_LONGTIME24_PATTERN));//出单时间
                flag.setPlateNum(po.getAuto().getLicensePlateNo());
                flag.setPlatform(po.getSourceChannel().getDescription()); //平台
                flag.setOwner(po.getAuto().getOwner()); //车主
                flag.setOrderNo(po.getOrderNo()); //订单号
                List<Gift> gifts = purchaseOrderGiftService.findGiftByPurchaseOrder(po);
                if (CollectionUtils.isNotEmpty(gifts)) {
                    for (Gift gift : gifts) {
                        int quantity = gift.getQuantity() == null ? 1 : gift.getQuantity();
                        if (gift.getGiftAmount() == null || gift.getGiftAmount() == 0.00) {
                            String giftStr = StringUtil.defaultNullStr(gift.getGiftDisplay()) + "*" + quantity;
                            if (NumberUtils.isNumber(gift.getGiftDisplay())) {
                                giftStr = String.valueOf(Double.parseDouble(gift.getGiftDisplay()) * quantity);
                            }
                            if (gift.getGiftType().getName().equals("加油卡")) {
                                flag.setFuelCard(giftStr); //加油卡金额
                            } else if (gift.getGiftType().getName().equals("京东卡")) {
                                flag.setJdCard(giftStr); //京东卡金额
                            } else {
                                flag.setGiftDetail(StringUtil.convertNull(flag.getGiftDetail()) + gift.getGiftType().getName()
                                    + "：" + (StringUtils.isEmpty(gift.getGiftDisplay()) ? "*" : gift.getGiftDisplay() + "元 * ")
                                    + quantity + (gift.getUnit() == null ? "" : gift.getUnit()) + ";"); //实物礼品
                            }
                        } else {
                            Double giftAmount = StringUtils.isEmpty(flag.getActivityFavour()) ? 0.00 : Double.valueOf(flag.getActivityFavour());
                            flag.setActivityFavour(String.valueOf(giftAmount + (gift.getGiftAmount() * quantity))); //活动优惠
                        }
                    }
                }
                List<Payment> paymentList = paymentRepository.findPaidPayments(po);
                if(paymentList!=null && paymentList.size()>0) {
                    flag.setPaymentChannel(paymentList.get(paymentList.size() - 1).getChannel().getFullDescription());//付款方式
                }
                PurchaseOrderAttribute poa = purchaseOrderAttributeRepository.findFirstByPurchaseOrder(po);
                if (poa != null) {
                    flag.setOrderAccount(poa.getValue()); //出单账号
                }
                flag.setAgentAccount(po.getApplicant().getMobile()); //代理人登录账号
                CompulsoryInsurance ci = compulsoryInsuranceRepository.findByQuoteRecordId(po.getObjId());
                Insurance ins = insuranceRepository.findByQuoteRecordId(po.getObjId());
                flag.setJiaoshangNo(ci != null ? ci.getPolicyNo() : ""); //交强险保单号
                flag.setInsuranceNo(ins != null ? ins.getPolicyNo() : ""); //商业险保单号
                InsuranceCompany ic = ins != null ? ins.getInsuranceCompany() : (ci != null ? ci.getInsuranceCompany() : null);
                flag.setCompany(ic != null ? ic.getName() : null); //保险公司
                String jiaoqiang = String.valueOf(ci != null ? ci.getCompulsoryPremium() : 0);
                String shangye = String.valueOf(ins != null ? ins.getPremium() : 0);
                String chechan = String.valueOf(ci != null ? ci.getAutoTax() : 0);
                flag.setCompulsoryPremium(jiaoqiang); //交强险
                flag.setAutoTax(chechan); //车船税
                flag.setCommecialPremium(shangye); //商业险
                flag.setPremiumSum(rebateAmountString(rebateAmount(ci != null ? ci.getCompulsoryPremium() : 0, ci != null ? ci.getAutoTax() : 0), ins != null ? ins.getPremium() : 0));//保费总额
                Area orderCity = areaRepository.findOne(po.getArea().getId());
                flag.setArea(orderCity.getName());//出单城市

                InsurancePurchaseOrderRebate insurancePurchaseOrderRebate=insurancePurchaseOrderRebateRepository.findFirstByPurchaseOrder(po);
                Long institutionId =null;
                if(insurancePurchaseOrderRebate!=null) {
                    institutionId = insurancePurchaseOrderRebate.getDownChannelId();
                }
                List<InstitutionRebateHistory> institutionRebateHistoryList=institutionRebateHistoryService.ListByAreaAndInsuranceCompanyAndDateTime(orderCity,ic.getId(),orderOperationInfo.getConfirmOrderDate());
                if(!org.springframework.util.CollectionUtils.isEmpty(institutionRebateHistoryList)) {
                    for (InstitutionRebateHistory institutionRebateHistory : institutionRebateHistoryList) {
                        InstitutionRebate institutionRebate = new InstitutionRebate();
                        String[] properties = {"institution", "area", "insuranceCompany", "commercialRebate", "compulsoryRebate"};
                        BeanUtil.copyPropertiesContain(institutionRebateHistory, institutionRebate, properties);
                        if (institutionId == institutionRebate.getInstitution().getId()) {
                            flag.setInstitution(institutionRebate.getInstitution().getName());//出单机构
                        }
                    }
                }
//                flag.setInstitution(ins != null && ins.getInstitution() != null ? ins.getInstitution().getName() : (ci != null && ci.getInstitution() != null ? ci.getInstitution().getName() : null));//出单机构
//                //TODO: hardcode
                if (flag.getInstitution() == null && ic != null) {
                    if (ic.isTaikang()) {
                        flag.setInstitution("泰康在线财产保险股份有限公司");
                    } else if (ic.isZhongAn()) {
                        flag.setInstitution("众安在线财产保险股份有限公司");
                    } else if (ic.isAnXin()) {
                        flag.setInstitution("安心财产保险股份有限公司");
                    } else if (ic.isHuaAn()) {
                        flag.setInstitution("华安财产保险股份有限公司");
                    }
                }

                flag.setGetMailed(po.getDeliveryAddress().getName()); //收件人
                if (StringUtils.isNotBlank(po.getDeliveryAddress().getProvince())) {
                    Area provA = areaRepository.findById(Long.parseLong(po.getDeliveryAddress().getProvince()));
                    if (provA != null) prov = provA.getName();
                }
                if (StringUtils.isNotBlank(po.getDeliveryAddress().getCity())) {
                    Area cityA = areaRepository.findById(Long.parseLong(po.getDeliveryAddress().getCity()));
                    if (cityA != null) city = cityA.getName();
                }
                if (StringUtils.isNotBlank(po.getDeliveryAddress().getDistrict())) {
                    Area districtA = areaRepository.findById(Long.parseLong(po.getDeliveryAddress().getDistrict()));
                    if (districtA != null) district = districtA.getName();
                }
                flag.setGetAddress(prov + city + district + po.getDeliveryAddress().getStreet()); //收货地址
                flag.setCustomerPhone(po.getDeliveryAddress().getMobile()); //收件人电话
                flag.setMailAddress(po.getApplicant().getEmail()); //电子邮箱

                resultList.add(flag);

                logger.debug("车保易报表：构建订单{}完成", po.getOrderNo());

            }
        }
        Map<String, List<ChebaoyiLedgerInfo>> orderExportInfoMap = new HashMap<>();
        if (resultList != null && resultList.size() > 0) {
            orderExportInfoMap.put("chebaoyiLedgerData", resultList);
        }
        return orderExportInfoMap;
    }

    private static String rebateAmountString(Double amount1, Double amount2) {
        return rebateAmount(amount1, amount2).toString();
    }

    private static Double rebateAmount(Double amount1, Double amount2) {
        Double sum = 0.0;
        sum += amount1 == null ? 0 : org.apache.commons.lang3.math.NumberUtils.toDouble(amount1.toString(), 0);
        sum += amount2 == null ? 0 : org.apache.commons.lang3.math.NumberUtils.toDouble(amount2.toString(), 0);
        return sum;
    }
}
