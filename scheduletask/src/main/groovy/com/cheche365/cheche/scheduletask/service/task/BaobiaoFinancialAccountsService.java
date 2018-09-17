package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.Gift;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.service.PurchaseOrderGiftService;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.AnswernUltimoInsuranceReportService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BaobiaoFinancialAccountsService {

    Logger logger = LoggerFactory.getLogger(AnswernUltimoInsuranceReportService.class);

    private static final String EMAIL_KEY = "baobiaoData";
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    public Map<String, List<PurchaseOrderInfo>> getAnswernUltimoCompleteOrderData() {
        Date latestMonth = DateUtils.getAroundMonthsDay(DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN), -1);
        Date startTime = DateUtils.getMonthFirstDay(latestMonth);
        Date endTime = DateUtils.getDayEndTime(DateUtils.getMonthLastDay(latestMonth));
        //获取保骉上月活动分享信息
        List<Object[]> offerDatas = purchaseOrderRepository.findBaobiaoOrderData(startTime, endTime, OrderStatus.Enum.FINISHED_5, InsuranceCompany.Enum.ZHONGAN_50000);

        logger.debug("从{}开始，获取到安心上月订单数量为{}", startTime, offerDatas.size());
        Map<String, List<PurchaseOrderInfo>> map = getEmptyExcelMap();
        //封装活动分享信息到Excel中
        pushOfferDataToEmail(offerDatas, map);
        return map;
    }

    private Map<String, List<PurchaseOrderInfo>> getEmptyExcelMap() {
        Map<String, List<PurchaseOrderInfo>> orderInfoListMap = new HashMap<>();
        List<PurchaseOrderInfo> orderInfos = new ArrayList<PurchaseOrderInfo>();
        orderInfoListMap.put(EMAIL_KEY, orderInfos);
        return orderInfoListMap;
    }

    /**
     * 将活动分享信息封装到邮件中
     *
     * @param insuranceDatas
     * @return
     */
    private void pushOfferDataToEmail(List<Object[]> insuranceDatas, Map<String, List<PurchaseOrderInfo>> orderInfoListMap) {
        List<PurchaseOrderInfo> orderInfos = orderInfoListMap.get(EMAIL_KEY);
        List<PurchaseOrderInfo> dataInfos = new ArrayList<PurchaseOrderInfo>();
        for (Object[] insuranceData : insuranceDatas) {
            PurchaseOrderInfo orderInfo = new PurchaseOrderInfo();
            String issureTime = StringUtil.formatTimeToString(insuranceData[0]);
            orderInfo.setIssueTime(issureTime);
            orderInfo.setPaymentPlatform(StringUtil.defaultNullStr(insuranceData[1]));
            orderInfo.setOwner(StringUtil.defaultNullStr(insuranceData[2]));
            orderInfo.setLicenseNo(StringUtil.defaultNullStr(insuranceData[3]));
            orderInfo.setOrderNo(StringUtil.defaultNullStr(insuranceData[4]));
            orderInfo.setPremiumSum(StringUtil.defaultNullStr(insuranceData[5]));
            orderInfo.setCompulsoryPremium(StringUtil.defaultNullStr(insuranceData[6]));
            orderInfo.setAutoTax(StringUtil.defaultNullStr(insuranceData[7]));
            orderInfo.setCommecialPremium(StringUtil.defaultNullStr(insuranceData[8]));
            orderInfo.setPaymentChannel(StringUtil.defaultNullStr(insuranceData[9]));
            orderInfo.setIsNewAuto(StringUtil.defaultNullStr(insuranceData[10]));
            orderInfo.setDamagePremium(StringUtil.defaultNullStr(insuranceData[11]));
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(Long.parseLong(insuranceData[12].toString()));
            List<Gift> gifts = purchaseOrderGiftService.findGiftByPurchaseOrder(purchaseOrder);
            if (CollectionUtils.isNotEmpty(gifts)) {
                for (Gift gift : gifts) {
                    int quantity = gift.getQuantity() == null ? 1 : gift.getQuantity();
                    if (gift.getGiftAmount() == null || gift.getGiftAmount() == 0.00) {
                        String giftStr = StringUtil.defaultNullStr(gift.getGiftDisplay()) + "*" + quantity;
                        if (NumberUtils.isNumber(gift.getGiftDisplay())) {
                            giftStr = String.valueOf(Double.parseDouble(gift.getGiftDisplay()) * quantity);
                        }
                        if (gift.getGiftType().getName().equals("加油卡")) {
                            orderInfo.setFuelCard(giftStr);
                        } else if (gift.getGiftType().getName().equals("京东卡")) {
                            orderInfo.setJdCard(giftStr);
                        } else {
                            orderInfo.setGiftDetail(StringUtil.convertNull(orderInfo.getGiftDetail()) + gift.getGiftType().getName()
                                + "：" + (StringUtils.isEmpty(gift.getGiftDisplay()) ? "*" : gift.getGiftDisplay() + "元 * ")
                                + quantity + (gift.getUnit() == null ? "" : gift.getUnit()) + ";");
                        }
                    } else {
                        Double giftAmount = StringUtils.isEmpty(orderInfo.getActivityFavour()) ? 0.00 : Double.valueOf(orderInfo.getActivityFavour());
                        orderInfo.setActivityFavour(String.valueOf(giftAmount + (gift.getGiftAmount() * quantity)));
                    }
                }
            }
            dataInfos.add(orderInfo);
        }
        logger.debug("最终拼装到邮件中保骉月报表的活动分享信息数据条目为{}", dataInfos.size());
        orderInfos.addAll(dataInfos);
        orderInfos.add(new PurchaseOrderInfo());
        orderInfoListMap.put(EMAIL_KEY, orderInfos);
    }

}
