package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.CompulsoryInsuranceService;
import com.cheche365.cheche.core.service.InsuranceService;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.QuoteRecordService;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 财务台帐
 * 时间、订单号、车主、车牌号、支付状态、支付方式、地区、保险公司、减免优惠类型、减免优惠类型（金额）、线下优惠类型、线下优惠类型（金额）、支付金额、保费总额、商业险、交强险、车船税、出单机构、服务费、代理（含渠道）、返佣
 * Created by sunhuazhong on 2016/5/30.
 */
@Service
public class FinancialAccountingService {

    Logger logger = LoggerFactory.getLogger(FinancialAccountingService.class);
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private InsurancePurchaseOrderRebateRepository insurancePurchaseOrderRebateRepository;
    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private QuoteRecordService quoteRecordService;
    @Autowired
    private InsuranceService insuranceService;
    @Autowired
    private CompulsoryInsuranceService compulsoryInsuranceService;
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public List<PurchaseOrderInfo> getPurchaseOrderInfos() {
        List<PurchaseOrderInfo> purchaseOrderInfoList = new ArrayList<>();
        String previousTime = stringRedisTemplate.opsForValue().get(TaskConstants.FINANCIAL_ACCOUNTING_PREVIOUS_TIME_CACHE);
        logger.debug("财务台帐报表上次查询的时间:{}", previousTime);
        Date startTime = null;
        Date endTime = null;
        if (StringUtils.isEmpty(previousTime)) {
            Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
            startTime = DateUtils.getCustomDate(currentTime, -1, 0, 0, 0);
            endTime = DateUtils.getCustomDate(currentTime, -1, 23, 59, 59);
        } else {
            startTime = DateUtils.getDate(previousTime, DateUtils.DATE_LONGTIME24_START_PATTERN);
            endTime = DateUtils.getCustomDate(DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN), -1, 23, 59, 59);
        }
        logger.debug("财务台帐报表查询的开始时间:{}，结束时间:{}",
                DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN),
                DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN));
        Pageable pageable = new PageRequest(TaskConstants.PAGE_NUMBER, TaskConstants.PAGE_SIZE, new Sort("purchaseOrder"));
        Page<PurchaseOrder> purchaseOrderPage = purchaseOrderRepository.findPageDataByCurrentStatusAndTime(OrderTransmissionStatus.Enum.PAID_AND_FINISH_ORDER, startTime, endTime, pageable);
        List<PurchaseOrder> purchaseOrderList = purchaseOrderPage.getContent();

        logger.debug("在{}到{}范围内的财务台帐订单第{}页的数量为{}",
                DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN),
                DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN),
                pageable.getPageNumber() + 1, purchaseOrderList.size());
        while (!CollectionUtils.isEmpty(purchaseOrderList)) {
            purchaseOrderList.forEach(purchaseOrder -> purchaseOrderInfoList.add(getPurchaseOrderInfo(purchaseOrder)));
            if (purchaseOrderList.size() < TaskConstants.PAGE_SIZE) {
                break;
            }
            pageable = pageable.next();
            purchaseOrderPage = purchaseOrderRepository.findPageDataByCurrentStatusAndTime(OrderTransmissionStatus.Enum.PAID_AND_FINISH_ORDER, startTime, endTime, pageable);
            purchaseOrderList = purchaseOrderPage.getContent();
            logger.debug("在{}到{}范围内的财务台帐订单第{}页的数量为{}",
                    DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN),
                    DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN),
                    pageable.getPageNumber() + 1, purchaseOrderList.size());
        }
        stringRedisTemplate.opsForValue().set(TaskConstants.FINANCIAL_ACCOUNTING_PREVIOUS_TIME_CACHE, DateUtils.getCurrentDateString(DateUtils.DATE_LONGTIME24_START_PATTERN));
        return purchaseOrderInfoList;
    }

    private PurchaseOrderInfo getPurchaseOrderInfo(PurchaseOrder purchaseOrder) {
        PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
        purchaseOrderInfo.setOrderTime(DateUtils.getDateString(
                purchaseOrder.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));//时间: orderTime
        purchaseOrderInfo.setOrderNo(purchaseOrder.getOrderNo());// 订单号: orderNo
        purchaseOrderInfo.setOwner(purchaseOrder.getAuto().getOwner());//车主: owner
        purchaseOrderInfo.setLicenseNo(purchaseOrder.getAuto().getLicensePlateNo());//车牌号: licenseNo
        purchaseOrderInfo.setChannel(purchaseOrder.getSourceChannel() == null ? "无" : purchaseOrder.getSourceChannel().getDescription());
        //设置订单金额信息
        setOrderAmountInfo(purchaseOrder, purchaseOrderInfo);
        //设置订单优惠信息
        setOrderDiscountInfo(purchaseOrder, purchaseOrderInfo);
        //设置订单佣金信息
        setOrderRebateInfo(purchaseOrder, purchaseOrderInfo);
        return purchaseOrderInfo;
    }

    private void setOrderAmountInfo(PurchaseOrder purchaseOrder, PurchaseOrderInfo purchaseOrderInfo) {
        QuoteRecord quoteRecord = quoteRecordService.getById(purchaseOrder.getObjId());
        Insurance insurance = insuranceService.findByQuoteRecord(quoteRecord);
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceService.findByQuoteRecord(quoteRecord);
        purchaseOrderInfo.setArea(quoteRecord.getArea() == null ? "无" : quoteRecord.getArea().getName());//地区: area
        purchaseOrderInfo.setInsuranceCompany(quoteRecord.getInsuranceCompany().getName());//保险公司: insuranceCompany
        purchaseOrderInfo.setPaymentStatus("已出单");//支付状态: paymentStatus
        purchaseOrderInfo.setPaymentChannel(purchaseOrder.getChannel().getName());//支付方式: paymentChannel
        purchaseOrderInfo.setPaidAmount(String.valueOf(purchaseOrder.getPaidAmount()));//支付金额: paidAmount
        purchaseOrderInfo.setPayableAmount(String.valueOf(purchaseOrder.getPayableAmount()));//保费总额: payableAmount
        purchaseOrderInfo.setCommecialPremium((insurance == null || insurance.getPremium() == null) ?
                "" : String.valueOf(insurance.getPremium()));//商业险: commercialAmount
        purchaseOrderInfo.setCompulsoryPremium((compulsoryInsurance == null || compulsoryInsurance.getCompulsoryPremium() == null) ?
                "" : String.valueOf(compulsoryInsurance.getCompulsoryPremium()));//交强险: compulsoryAmount
        purchaseOrderInfo.setAutoTax((compulsoryInsurance == null || compulsoryInsurance.getAutoTax() == null) ?
                "" : String.valueOf(compulsoryInsurance.getAutoTax())); //车船税: autoTax
    }

    private void setOrderDiscountInfo(PurchaseOrder purchaseOrder, PurchaseOrderInfo purchaseOrderInfo) {
        Map<String, List<Map<String, String>>> resultMap = purchaseOrderService.findOrderDetails(purchaseOrder);
        List<Map<String, String>> paymentResultList = resultMap.get(PurchaseOrder.DiscountEnum.PAYMENT_DISCOUNT);
        if (!CollectionUtils.isEmpty(paymentResultList)) {
            String paymentDiscountType = "";
            Double paymentDiscountAmount = 0.00;
            for (Map<String, String> paymentResultMap : paymentResultList) {
                paymentDiscountType += getValue(paymentResultMap.get(PurchaseOrder.DiscountEnum.PAYMENT_DISCOUNT_TYPE)) + "、";
                paymentDiscountAmount = DoubleUtils.add(paymentDiscountAmount, Double.valueOf(paymentResultMap.get(PurchaseOrder.DiscountEnum.PAYMENT_DISCOUNT_AMOUNT)));
            }
            purchaseOrderInfo.setPaymentDiscountType(paymentDiscountType.length() > 0 ?
                    paymentDiscountType.substring(0, paymentDiscountType.length() - 1) : paymentDiscountType);//减免优惠类型: paymentDiscountType
            purchaseOrderInfo.setPaymentDiscountAmount(String.valueOf(paymentDiscountAmount));//减免优惠类型（金额）: paymentDiscountAmount
        }
        List<Map<String, String>> giftResultList = resultMap.get(PurchaseOrder.DiscountEnum.GIFT_DISCOUNT);
        if (!CollectionUtils.isEmpty(giftResultList)) {
            String giftDiscountType = "";
            String giftDiscountAmount = "";
            for (int i = 0; i < giftResultList.size(); i++) {
                Map<String, String> giftResultMap = giftResultList.get(i);
                giftDiscountType += getValue(giftResultMap.get(PurchaseOrder.DiscountEnum.GIFT_DISCOUNT_TYPE));
                giftDiscountAmount += getValue(giftResultMap.get(PurchaseOrder.DiscountEnum.GIFT_DISCOUNT_AMOUNT));
                if (i != giftResultList.size() - 1) {
                    giftDiscountType += "\n";
                    giftDiscountAmount += "\n";
                }
            }
            purchaseOrderInfo.setGiftDiscountType(giftDiscountType);//线下优惠类型: giftDiscountType
            purchaseOrderInfo.setGiftDiscountAmount(giftDiscountAmount);//线下优惠类型（金额）: giftDiscountAmount
        }
    }

    private void setOrderRebateInfo(PurchaseOrder purchaseOrder, PurchaseOrderInfo purchaseOrderInfo) {
        InsurancePurchaseOrderRebate orderRebate = insurancePurchaseOrderRebateRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (orderRebate != null) {
            String downChannelName = "";//下游渠道名称
            String upChannelName = "";//上游渠道名称
            if (orderRebate.getUpRebateChannel() != null && orderRebate.getUpChannelId() != null
                    && RebateChannel.Enum.REBATE_CHANNEL_AGENT.getId().equals(orderRebate.getUpRebateChannel().getId())) {
                upChannelName = agentRepository.findOne(orderRebate.getUpChannelId()).getName();
            }
            if (orderRebate.getDownRebateChannel() != null && orderRebate.getDownChannelId() != null
                    && RebateChannel.Enum.REBATE_CHANNEL_INSTITUTION.getId().equals(orderRebate.getDownRebateChannel().getId())) {
                downChannelName = institutionRepository.findOne(orderRebate.getDownChannelId()).getName();
            }
            purchaseOrderInfo.setDownRebateChannel(orderRebate.getDownRebateChannel() == null ?
                    "无" : downChannelName);//出单机构
            purchaseOrderInfo.setDownRebateAmount(String.valueOf(DoubleUtils.add(
                    orderRebate.getDownCommercialAmount(),
                    orderRebate.getDownCompulsoryAmount())));//服务费
            purchaseOrderInfo.setUpRebateChannel(orderRebate.getUpRebateChannel() == null ?
                    "无" : orderRebate.getUpRebateChannel().getName() + ":" + upChannelName);//代理（含渠道）
            purchaseOrderInfo.setUpRebateAmount(String.valueOf(DoubleUtils.add(
                    orderRebate.getUpCommercialAmount(),
                    orderRebate.getUpCompulsoryAmount())));//返佣

            //出单机构费率
            String downRebateStr = "";
            String upRebateStr = "";
            if (orderRebate.getDownChannelId() != null) {
                downRebateStr = "商业险费率:" + orderRebate.getDownCommercialRebate() + ",交强险费率:" + orderRebate.getDownCompulsoryRebate();
            }
            if (orderRebate.getUpChannelId() != null) {
                upRebateStr = "商业险费率:" + orderRebate.getUpCommercialRebate() + ",交强险费率:" + orderRebate.getUpCompulsoryRebate();
            }
            purchaseOrderInfo.setDownRebateChannelRebate(downRebateStr);
            purchaseOrderInfo.setAgentRebate(upRebateStr);
        }
    }

    private String getValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }
}
