package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.BusinessActivityRepository;
import com.cheche365.cheche.core.repository.GiftCodeRepository;
import com.cheche365.cheche.core.repository.MarketingSuccessRepository;
import com.cheche365.cheche.core.service.PurchaseOrderGiftService;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.task.CompletedOrderOperationReportTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by liufei on 2016/1/13.
 */
@Service
public class CompletedOrderOperationReportService {


    Logger logger = LoggerFactory.getLogger(CompletedOrderOperationReportTask.class);
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private MarketingSuccessRepository marketingSuccessRepository;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    @Autowired
    private GiftCodeRepository giftCodeRepository;

    @Autowired
    private BusinessActivityRepository businessActivityRepository;


    public Map<String, List<PurchaseOrderInfo>> getPurchaseOrderInfos() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        List<PurchaseOrder> purchaseOrderList;
        if (hour == 0) {
            purchaseOrderList = find24HourOrder();
        } else {
            purchaseOrderList = find40HourOrder();
        }
        if (!CollectionUtils.isEmpty(purchaseOrderList)) {
            List<PurchaseOrderInfo> useGiftOrder = new ArrayList<>();
            List<PurchaseOrderInfo> normalOrder = new ArrayList<>();
            PurchaseOrderInfo purchaseOrderInfo;
            for (PurchaseOrder purchaseOrder : purchaseOrderList) {
                purchaseOrderInfo = getExcelSheetRowInfo(purchaseOrder);
                if (purchaseOrderInfo.getGiftSource() == null) {
                    normalOrder.add(purchaseOrderInfo);
                } else {
                    useGiftOrder.add(purchaseOrderInfo);
                }
            }
            Map<String, List<PurchaseOrderInfo>> purchaseOrderInfoListMap = new HashMap<>();
            purchaseOrderInfoListMap.put("usedGift", useGiftOrder);
            purchaseOrderInfoListMap.put("unusedGift", normalOrder);
            return purchaseOrderInfoListMap;
        }
        return null;
    }


    private List<PurchaseOrder> find24HourOrder() {
        Date currentDate = new Date();
        Date startDate = DateUtils.getCustomDate(currentDate, -1, 0, 0, 0);
        Date endDate = DateUtils.getCustomDate(currentDate, 0, 0, 0, 0);
        List<PurchaseOrder> purchaseOrderList = purchaseOrderService.findCompletedOrderByCreateTimeBetween(startDate, endDate);
        if (logger.isDebugEnabled()) {
            logger.debug("运营中心成单统计，24小时查询出单量: -> {}", purchaseOrderList.size());
        }
        return purchaseOrderList;

    }

    private List<PurchaseOrder> find40HourOrder() {
        Date currentDate = new Date();
        Date startDate = DateUtils.getCustomDate(currentDate, -1, 0, 0, 0);
        Date endDate = DateUtils.getCustomDate(currentDate, 0, 16, 0, 0);
//        Date startDate=DateUtils.getDate("2015-11-11", "yyyy-MM-dd");
//        Date endDate=DateUtils.getDate("2015-12-11", "yyyy-MM-dd");
        List<PurchaseOrder> purchaseOrderList = purchaseOrderService.findCompletedOrderByCreateTimeBetween(startDate, endDate);
        if (logger.isDebugEnabled()) {
            logger.debug("运营中心成单统计，40小时查询出单量: -> {}", purchaseOrderList.size());
        }
        return purchaseOrderList;
    }

    private PurchaseOrderInfo getExcelSheetRowInfo(PurchaseOrder purchaseOrder) {
        PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
        purchaseOrderInfo.setOrderNo(purchaseOrder.getOrderNo());
        purchaseOrderInfo.setLicenseNo(purchaseOrder.getAuto() == null ? "" : purchaseOrder.getAuto().getLicensePlateNo());
        purchaseOrderInfo.setChannel(purchaseOrder.getSourceChannel() == null ? "" : purchaseOrder.getSourceChannel().getDescription());
        purchaseOrderInfo.setPaymentPlatform(purchaseOrder.getSourceChannel() == null ? "" : purchaseOrder.getSourceChannel().getName());
        purchaseOrderInfo.setPaymentChannel(purchaseOrder.getChannel() == null ? "" : purchaseOrder.getChannel().getName());
        purchaseOrderInfo.setPayableAmount(String.valueOf(purchaseOrder.getPayableAmount()));
        purchaseOrderInfo.setPaidAmount(String.valueOf(purchaseOrder.getPaidAmount()));
        purchaseOrderInfo.setRegisterChannel(purchaseOrder.getApplicant().getRegisterChannel() == null ? "" : purchaseOrder.getApplicant().getRegisterChannel().getDescription());
        purchaseOrderInfo.setSource(purchaseOrderService.getUserSource(purchaseOrder));
        List<Gift> giftList = purchaseOrderGiftService.findGiftByPurchaseOrder(purchaseOrder);
        if (!CollectionUtils.isEmpty(giftList)) {
            SourceType sourceType;
            StringBuffer giftSourceBuffer = new StringBuffer();
            StringBuffer timeBuffer = new StringBuffer();
            StringBuffer marketingNameBuffer = new StringBuffer();
            for (Gift gift : giftList) {
                sourceType = gift.getSourceType();
                if (sourceType == null) {
                    continue;
                }
                if (sourceType.getId() == SourceType.Enum.WECHATRED_2.getId()) {
                    //微信活动
                    MarketingSuccess marketingSuccess = marketingSuccessRepository.findOne(gift.getSource());
                    if (marketingSuccess == null || marketingSuccess.getSourceChannel() == null) {
                        continue;
                    }
                    String sourceChannel = marketingSuccess.getSourceChannel();
                    BusinessActivity businessActivity = businessActivityRepository.findFirstByCode(sourceChannel);
                    if (businessActivity != null) {
                        giftSourceBuffer.append(businessActivity.getName()).append("(").append(sourceChannel).append(")");
                    } else {
                        giftSourceBuffer.append(sourceChannel);
                    }
                    timeBuffer.append(DateUtils.getDateString(marketingSuccess.getEffectDate(), DateUtils.DATE_LONGTIME24_PATTERN));
                    marketingNameBuffer.append(marketingSuccess.getMarketing().getName());
                } else if (sourceType.getId() == SourceType.Enum.PURCHASE_ORDER_1.getId()) {
                    //订单
                    giftSourceBuffer.append(SourceType.Enum.PURCHASE_ORDER_1.getName());
                    timeBuffer.append(DateUtils.getDateString(purchaseOrder.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
                } else if (sourceType.getId() == SourceType.Enum.GIFT_CODE_4.getId()) {
                    //兑换码
                    GiftCode giftCode = giftCodeRepository.findOne(gift.getSource());
                    if (giftCode == null || giftCode.getExchangeWay() == null) {
                        continue;
                    }
                    GiftCodeExchangeWay giftCodeExchangeWay = giftCode.getExchangeWay();
                    giftSourceBuffer.append(SourceType.Enum.GIFT_CODE_4.getName());
                    timeBuffer.append(DateUtils.getDateString(giftCode.getExchangeTime(), DateUtils.DATE_LONGTIME24_PATTERN));
                    marketingNameBuffer.append(giftCodeExchangeWay.getName());
                }
                giftSourceBuffer.append(",");
                timeBuffer.append(",");
                marketingNameBuffer.append(",");
            }
            purchaseOrderInfo.setGiftSource(giftSourceBuffer.length() > 0 ? giftSourceBuffer.substring(0, giftSourceBuffer.lastIndexOf(",")) : "");
            purchaseOrderInfo.setTime(timeBuffer.length() > 0 ? timeBuffer.substring(0, timeBuffer.lastIndexOf(",")) : "");
            purchaseOrderInfo.setMarketingName(marketingNameBuffer.length() > 0 ? marketingNameBuffer.substring(0, marketingNameBuffer.lastIndexOf(",")) : "");
        }
        return purchaseOrderInfo;
    }
}
