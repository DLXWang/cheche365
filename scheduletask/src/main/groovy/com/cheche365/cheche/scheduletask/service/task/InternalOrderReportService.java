package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.AddressService;
import com.cheche365.cheche.core.service.PurchaseOrderGiftService;
import com.cheche365.cheche.core.service.QuoteRecordService;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liufei on 2016/1/14.
 */
@Service
public class InternalOrderReportService {

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    @Autowired
    private MarketingSuccessRepository marketingSuccessRepository;

    @Autowired
    private GiftCodeRepository giftCodeRepository;

    @Autowired
    private AddressService addressService;

    public List<PurchaseOrderInfo> getPurchaseOrderInfos(){
        List<PurchaseOrderInfo> purchaseOrderInfoList = new ArrayList<>();
        String dateStr = DateUtils.getCurrentDateString("yyyy-MM-dd")+"%";
        List<OrderOperationInfo> internalOrderList=orderOperationInfoRepository.findOrderOperationInfoByDate(dateStr);

        if(!CollectionUtils.isEmpty(internalOrderList)){
            Integer serialNumber=0;
            for(OrderOperationInfo orderOperationInfo:internalOrderList){
                purchaseOrderInfoList.add(getPurchaseOrderInfo(orderOperationInfo,++serialNumber));
            }
        }
        return purchaseOrderInfoList;
    }

    private PurchaseOrderInfo getPurchaseOrderInfo(OrderOperationInfo orderOperationInfo,Integer serialNumber){
        PurchaseOrderInfo purchaseOrderInfo=new PurchaseOrderInfo();
        PurchaseOrder purchaseOrder=orderOperationInfo.getPurchaseOrder();
        QuoteRecord quoteRecord=quoteRecordService.getById(purchaseOrder.getObjId());
        Insurance insurance = insuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        Double commercialPremium = 0.0;//商业险
        String insuredName="";
        if (insurance != null) {
            commercialPremium = (insurance.getPremium() == null) ? 0.0 : insurance.getPremium();
            insuredName = insurance.getInsuredName() == null? "" : insurance.getInsuredName();
        }
        Double compulsoryPremium = 0.0;//交强险
        Double autoTax = 0.0;//车船税
        if (compulsoryInsurance != null) {
            compulsoryPremium = (compulsoryInsurance.getCompulsoryPremium() == null) ? 0.0 : compulsoryInsurance.getCompulsoryPremium();
            autoTax = (compulsoryInsurance.getAutoTax() == null) ? 0.0 : compulsoryInsurance.getAutoTax();
            if(StringUtils.isEmpty(insuredName)) {
                insuredName = compulsoryInsurance.getInsuredName() == null? "" : compulsoryInsurance.getInsuredName();
            }
        }
        //序号
        purchaseOrderInfo.setSerialNumber(String.valueOf(serialNumber));
        //保险公司
        purchaseOrderInfo.setInsuranceCompany(quoteRecord.getInsuranceCompany().getName());
        //下单日期
        purchaseOrderInfo.setOrderTime(DateUtils.getDateString(purchaseOrder.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        //车主
        purchaseOrderInfo.setOwner(purchaseOrder.getAuto().getOwner());
        //被保险人
        purchaseOrderInfo.setInsuredName(insuredName);
        //车牌号
        purchaseOrderInfo.setLicenseNo(purchaseOrder.getAuto().getLicensePlateNo());
        //订单号
        purchaseOrderInfo.setOrderNo(purchaseOrder.getOrderNo());
        //总保费
        purchaseOrderInfo.setPayableAmount(String.valueOf(purchaseOrder.getPayableAmount()));
        //交强险
        purchaseOrderInfo.setCompulsoryPremium(String.valueOf(compulsoryPremium));
        //车船税
        purchaseOrderInfo.setAutoTax(String.valueOf(autoTax));
        //商业险
        purchaseOrderInfo.setCommecialPremium(String.valueOf(commercialPremium));
        //电话
        purchaseOrderInfo.setLinkPhone(purchaseOrder.getApplicant().getMobile());
        //活动政策
        purchaseOrderInfo.setActivity(getActivityName(purchaseOrder));
        //礼品信息
        purchaseOrderInfo.setGiftDetail(purchaseOrderGiftService.getGiftDetail(purchaseOrder).replaceAll("、", "\n"));
        //元，优惠活动金额
        Double discountAmount= DoubleUtils.sub(purchaseOrder.getPayableAmount(), purchaseOrder.getPaidAmount());
        purchaseOrderInfo.setDiscountAmount(String.valueOf(discountAmount));
        //补贴率
        purchaseOrderInfo.setSubsidyRate(getSubsidyRate(DoubleUtils.add(getSumAmount(purchaseOrder), discountAmount), purchaseOrder.getPayableAmount(), autoTax));
        //收货地址
        purchaseOrderInfo.setDeliveryAddress(addressService.getAddress(purchaseOrder));
        return purchaseOrderInfo;
    }

    /**
     * 获取活动政策
     *
     * @param purchaseOrder
     * @return
     */
    private String getActivityName(PurchaseOrder purchaseOrder) {
        List<Gift> giftList = purchaseOrderGiftService.findGiftByPurchaseOrder(purchaseOrder);
        StringBuffer activityName=new StringBuffer();
        if(!CollectionUtils.isEmpty(giftList)){
            SourceType sourceType;
            for (Gift gift : giftList) {
                sourceType = gift.getSourceType();
                if(sourceType==null){
                    continue;
                }
                if (sourceType.getId() == SourceType.Enum.WECHATRED_2.getId()) {
                    //微信活动
                    MarketingSuccess marketingSuccess = marketingSuccessRepository.findOne(gift.getSource());
                    if (marketingSuccess != null) {
                        activityName.append(marketingSuccess.getMarketing().getName());
                    }
                }else if(sourceType.getId()==SourceType.Enum.GIFT_CODE_4.getId()){
                    //兑换码
                    GiftCode giftCode=giftCodeRepository.findOne(gift.getSource());
                    if(giftCode==null||giftCode.getExchangeWay()==null){
                        continue;
                    }
                    GiftCodeExchangeWay giftCodeExchangeWay=giftCode.getExchangeWay();
                    activityName.append(giftCodeExchangeWay.getName());
                }
                activityName.append("\n");
            }
            return activityName.substring(0,activityName.lastIndexOf("\n"));
        }
        return "无";
    }

    /**
     * 获取总金额
     *
     * @param purchaseOrder
     * @return
     */
    private Double getSumAmount(PurchaseOrder purchaseOrder) {
        List<Gift> giftList = purchaseOrderGiftService.findGiftByPurchaseOrder(purchaseOrder);
        Double giftAmount=0.0;
        if(!CollectionUtils.isEmpty(giftList)){
            for(Gift gift:giftList) {
                if (gift.getGiftAmount() != null) {
                    giftAmount = DoubleUtils.add(giftAmount, DoubleUtils.mul(gift.getGiftAmount(), gift.getQuantity() + 0.0, 2));
                }
            }
        }
        return giftAmount;
    }

    /**
     * 获取补贴率
     *
     * @param sumAmount
     * @param compulsoryInsuranceFee
     * @param commecialInsurance
     * @return
     */
    private String getSubsidyRate(Double sumAmount, Double compulsoryInsuranceFee, Double commecialInsurance) {
        Double Subsidy = DoubleUtils.div(sumAmount, DoubleUtils.sub(compulsoryInsuranceFee, commecialInsurance), 2);
        return DoubleUtils.mul(Subsidy, 100.00, 2) + "%";
    }

}
