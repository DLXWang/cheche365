package com.cheche365.cheche.core.service;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.GiftRepository;
import com.cheche365.cheche.core.repository.InsurancePurchaseOrderRebateRepository;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderGiftRepository;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.core.util.BigDecimalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mahong on 2015/6/23.
 */
@Service
@Transactional
public class PurchaseOrderGiftService {

    @Autowired
    private PurchaseOrderGiftRepository purchaseOrderGiftRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private InsurancePurchaseOrderRebateRepository insurancePurchaseOrderRebateRepository;

    public List<PurchaseOrderGift> assembleAndSavePurchaseOrderGift(PurchaseOrder order, List<Gift> couponGifts, List<Gift> realGifts) {
        List<PurchaseOrderGift> purchaseOrderGifts = new ArrayList<>();
        if (couponGifts != null && !couponGifts.isEmpty()) {
            purchaseOrderGifts.addAll(assemblePurchaseOrderGift(order, couponGifts, false));
        }
        if (realGifts != null && !realGifts.isEmpty()) {
            purchaseOrderGifts.addAll(assemblePurchaseOrderGift(order, realGifts, true));
        }
        return this.save(purchaseOrderGifts);
    }

    public List<PurchaseOrderGift> assemblePurchaseOrderGift(PurchaseOrder order, List<Gift> gifts, boolean givenAfterOrder) {
        List<PurchaseOrderGift> purchaseOrderGifts = new ArrayList<>();
        Iterator<Gift> iterator = gifts.iterator();
        while (iterator.hasNext()) {
            Gift gift = iterator.next();
            PurchaseOrderGift purchaseOrderGift = new PurchaseOrderGift();
            purchaseOrderGift.setPurchaseOrder(order);
            purchaseOrderGift.setGift(gift);
            purchaseOrderGift.setGivenAfterOrder(givenAfterOrder);
            purchaseOrderGifts.add(purchaseOrderGift);
        }
        return purchaseOrderGifts;
    }

    public List<PurchaseOrderGift> save(List<PurchaseOrderGift> purchaseOrderGifts) {
        if (purchaseOrderGifts == null || purchaseOrderGifts.isEmpty()) {
            return null;
        }
        return (List<PurchaseOrderGift>) purchaseOrderGiftRepository.save(purchaseOrderGifts);
    }

    public List<Gift> findGiftByPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null)
            return null;

        List<Gift> giftList = new ArrayList<>();
        List<PurchaseOrderGift> purchaseOrderGiftList = purchaseOrderGiftRepository.findByPurchaseOrder(purchaseOrder);
        if (purchaseOrderGiftList != null) {
            purchaseOrderGiftList.forEach(purchaseOrderGift -> {
                giftList.add(purchaseOrderGift.getGift());
            });
        }

        return giftList;
    }

    public String getGiftDetail(PurchaseOrder purchaseOrder) {
        String giftDetails = "无";
        List<Gift> giftList = findGiftByPurchaseOrder(purchaseOrder);
        if (CollectionUtils.isNotEmpty(giftList)) {
            List<String> giftDetailsList = new ArrayList<>();
            giftList.forEach(gift -> {
                int quantity = gift.getQuantity() == null ? 1 : gift.getQuantity();
                if (gift.getGiftAmount() == null) {
                    giftDetailsList.add(StringUtil.defaultNullStr(gift.getGiftDisplay()) + (BeanUtil.equalsID(gift.getGiftType(), GiftType.Enum.INSURE_GIVE_GIFT_PACK_29) ? gift.getGiftContent() : gift.getGiftType().getName())
                            + "*" + quantity + (gift.getUnit() == null ? "" : gift.getUnit()));
                } else {
                    giftDetailsList.add(gift.getGiftType().getName()
                            + "：" + gift.getGiftDisplay() + "元 * "
                            + quantity + (gift.getUnit() == null ? "" : gift.getUnit()));
                }
            });
            giftDetails = String.join("、", giftDetailsList);
        }
        return giftDetails;
    }

    public String getGiftInfo(Long orderId, PurchaseOrder purchaseOrder) {
        //车车优惠
        List<Payment> discountList = findByPurchaseOrderAndPaymentType(orderId, PaymentType.Enum.DISCOUNT_5);
        //车车承担
        List<Payment> chechePayList = findByPurchaseOrderAndPaymentType(orderId, PaymentType.Enum.CHECHEPAY_6);
        //gift
        List<Gift> gifts = giftRepository.findMaterialGiftByOrder(orderId);
        //泛华承担
        List<Payment> fanhuaPayList = findByPurchaseOrderAndPaymentType(orderId,PaymentType.Enum.BAOXIANPAY_8);
        //奖励金(如果是代理人渠道,从InsurancePurchaseOrderRebate表中取奖励金)
        Channel channel = purchaseOrder.getSourceChannel();
        InsurancePurchaseOrderRebate insurancePurchaseOrderRebate = null;
        if (channel.isAgentChannel()) {
            insurancePurchaseOrderRebate = insurancePurchaseOrderRebateRepository.findFirstByPurchaseOrder(purchaseOrder);
        }
        return getInfoStr(discountList, chechePayList,fanhuaPayList, gifts, insurancePurchaseOrderRebate);
    }

    public String getHistoryGiftInfo(PurchaseOrderHistory purchaseOrderHistory) {
        //车车优惠 purchaseOrderHistory和Type获取
        List<Payment> discountList = findByPurchaseOrderHistoryAndPaymentType(purchaseOrderHistory, PaymentType.Enum.DISCOUNT_5);
        //车车承担 通过purchaseOrderHistory和Type获取
        List<Payment> chechePayList = findByPurchaseOrderHistoryAndPaymentType(purchaseOrderHistory, PaymentType.Enum.CHECHEPAY_6);
        //gift 通过Purchase_Order_history去purchase_order_gift_history中查找
        List<Gift> gifts = giftRepository.findGiftByOrderHistory(purchaseOrderHistory);
        //泛华承担
        List<Payment> fanhuaPayList = findByPurchaseOrderHistoryAndPaymentType(purchaseOrderHistory,PaymentType.Enum.BAOXIANPAY_8);
        return getInfoStr(discountList, chechePayList, fanhuaPayList,gifts);
    }

    private String getInfoStr(List<Payment> discountList, List<Payment> chechePayList,List<Payment> fanhuaPayList, List<Gift> gifts) {
        return getInfoStr(discountList, chechePayList, fanhuaPayList,gifts, null);
    }

    private String getInfoStr(List<Payment> discountList, List<Payment> chechePayList,List<Payment> fanhuaPayList, List<Gift> gifts, InsurancePurchaseOrderRebate insurancePurchaseOrderRebate) {
        Double discountAmount = sumPayments(discountList);
        Double chechePayAmount = sumPayments(chechePayList);
        Double fanhuaPayAmount = sumPayments(fanhuaPayList);
        StringBuilder sb = new StringBuilder();
        if (discountAmount > 0)
            sb.append("优惠：").append(discountAmount).append("元；");
        if (chechePayAmount > 0)
            sb.append("车车承担：").append(chechePayAmount).append("元；");
        if(fanhuaPayAmount > 0)
            sb.append("泛华承担：").append(fanhuaPayAmount).append("元；");
        if(CollectionUtils.isNotEmpty(gifts)){
            String giftsInfo = setGiftsInfo(gifts);
            sb.append("礼品:").append(giftsInfo).append("；");
        }
        if (insurancePurchaseOrderRebate != null) {
            Double rebate = BigDecimalUtil.add(insurancePurchaseOrderRebate.getUpCommercialAmount(), insurancePurchaseOrderRebate.getUpCompulsoryAmount()).doubleValue();
            boolean isNotZero = DoubleUtils.isNotZero(rebate);
            if (isNotZero) {
                sb.append("奖励金：").append(rebate).append("元；");
            }
        }
        return StringUtils.isEmpty(sb.toString()) ? "无" : sb.toString();
    }

    public List<Payment> findByPurchaseOrderAndPaymentType(Long orderId, PaymentType paymentType) {
        return paymentRepository.findByPurchaseOrderAndPaymentTypeAndStatus(orderId, paymentType, PaymentStatus.Enum.PAYMENTSUCCESS_2);
    }

    public List<Payment> findByPurchaseOrderHistoryAndPaymentType(PurchaseOrderHistory purchaseOrderHistory, PaymentType paymentType) {
        return paymentRepository.findByPurchaseOrderHistoryAndPaymentType(purchaseOrderHistory, paymentType);
    }

    private Double sumPayments(List<Payment> payments) {
        Double sum = 0.00;
        if (CollectionUtils.isNotEmpty(payments)) {
            for (Payment payment : payments) {
                sum += payment.getAmount();
            }
        }
        return sum;
    }

    private String setGiftsInfo(List<Gift> giftList) {
        List<String> giftDetailsList = new ArrayList<>();
        for (Gift gift : giftList) {
            int quantity = gift.getQuantity() == null ? 1 : gift.getQuantity();
            if (gift.getGiftAmount() == null) {
                giftDetailsList.add(StringUtil.defaultNullStr(gift.getGiftDisplay()) + (BeanUtil.equalsID(gift.getGiftType(), GiftType.Enum.INSURE_GIVE_GIFT_PACK_29) ? gift.getGiftContent() : gift.getGiftType().getName())
                        + "*" + quantity + (gift.getUnit() == null ? "" : gift.getUnit()));
            } else {
                giftDetailsList.add(gift.getGiftType().getName() + "：" + gift.getGiftDisplay() + " * "
                        + quantity + (gift.getUnit() == null ? "" : gift.getUnit()));
            }
        }
        return String.join("、", giftDetailsList);
    }
}
