package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.DailyInsuranceRepository;
import com.cheche365.cheche.core.repository.DailyRestartInsuranceRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.service.PurchaseOrderGiftService;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000;

/**
 * Created by wangshaobin on 2017/4/5.
 */
@Service
public class AnswernUltimoInsuranceReportService {
    Logger logger = LoggerFactory.getLogger(AnswernUltimoInsuranceReportService.class);

    private static final String EMAIL_KEY = "answernUltimoData";

    @Autowired
    private DailyInsuranceRepository dailyInsuranceRepository;

    @Autowired
    private DailyRestartInsuranceRepository dailyRestartInsuranceRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    public Map<String,List<PurchaseOrderInfo>> getAnswernUltimoCompleteOrderData(){
        Date latestMonth = DateUtils.getAroundMonthsDay(DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN), -1);
        Date startTime = DateUtils.getMonthFirstDay(latestMonth);
        Date endTime = DateUtils.getDayEndTime(DateUtils.getMonthLastDay(latestMonth));
        //进行数据的获取
        //1、 获取安心上月活动分享信息
        List<Object[]> offerDatas =purchaseOrderRepository.findAnswernOrderData(startTime,endTime,OrderStatus.Enum.FINISHED_5, InsuranceCompany.Enum.ANSWERN_65000);
        logger.debug("从{}开始，获取到安心上月订单数量为{}", startTime, offerDatas.size());
        //2、 获取安心上月停复驶信息
        List<Object[]> insuranceDatas = dailyInsuranceRepository.findAnswernUltimoData(startTime, endTime, ANSWERN_65000, DailyInsuranceStatus.Enum.STOP_CALCULATE);
        logger.debug("从{}开始，获取到安心上月停驶信息条目为{}", startTime, insuranceDatas.size());
        List<Object[]> restartInsuranceDatas = dailyRestartInsuranceRepository.findAnswernUltimoData(startTime, endTime, ANSWERN_65000, DailyInsuranceStatus.Enum.RESTART_INSURED);
        logger.debug("从{}开始，获取到安心上月复驶信息条目为{}", startTime, restartInsuranceDatas.size());
        Map<String,List<PurchaseOrderInfo>> map = getEmptyExcelMap();
        //封装活动分享信息到Excel中
        pushOfferDataToEmail(offerDatas, map);
        //封装停复驶信息到Excel中，标识1表示停驶、0表示复驶
        pushInsuranceDataToEmail(insuranceDatas, map, 1);
        pushInsuranceDataToEmail(restartInsuranceDatas, map, 0);
        return map;
    }

    private Map<String,List<PurchaseOrderInfo>> getEmptyExcelMap(){
        Map<String,List<PurchaseOrderInfo>> orderInfoListMap = new HashMap<>();
        List<PurchaseOrderInfo> orderInfos = new ArrayList<PurchaseOrderInfo>();
        orderInfoListMap.put(EMAIL_KEY, orderInfos);
        return orderInfoListMap;
    }

    /**
     * 将活动分享信息封装到邮件中
     * @param insuranceDatas
     * @return
     */
    private void pushOfferDataToEmail(List<Object[]> insuranceDatas, Map<String,List<PurchaseOrderInfo>> orderInfoListMap){
        List<PurchaseOrderInfo> orderInfos = orderInfoListMap.get(EMAIL_KEY);
        List<PurchaseOrderInfo> dataInfos = new ArrayList<PurchaseOrderInfo>();
        for(Object[] insuranceData : insuranceDatas){
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
            PurchaseOrder purchaseOrder=purchaseOrderRepository.findOne(Long.parseLong(insuranceData[12].toString()));
            List<Gift> gifts =purchaseOrderGiftService.findGiftByPurchaseOrder(purchaseOrder);
            if(CollectionUtils.isNotEmpty(gifts)){
                for(Gift gift:gifts){
                    int quantity = gift.getQuantity() == null ? 1 : gift.getQuantity();
                    if (gift.getGiftAmount() == null || gift.getGiftAmount() == 0.00) {
                        String giftStr=StringUtil.defaultNullStr(gift.getGiftDisplay())+"*"+quantity;
                        if(NumberUtils.isNumber(gift.getGiftDisplay())){
                            giftStr=String.valueOf(Double.parseDouble(gift.getGiftDisplay())*quantity);
                        }
                        if(gift.getGiftType().getName().equals("加油卡")){
                            orderInfo.setFuelCard(giftStr);
                        }else if(gift.getGiftType().getName().equals("京东卡")){
                            orderInfo.setJdCard(giftStr);
                        }else{
                            orderInfo.setGiftDetail(StringUtil.convertNull(orderInfo.getGiftDetail())+gift.getGiftType().getName()
                                + "：" +(StringUtils.isEmpty(gift.getGiftDisplay())?"*":gift.getGiftDisplay() + "元 * ")
                                + quantity + (gift.getUnit() == null ? "" : gift.getUnit())+";");
                        }
                    } else {
                        Double giftAmount=StringUtils.isEmpty(orderInfo.getActivityFavour())? 0.00:Double.valueOf(orderInfo.getActivityFavour());
                        orderInfo.setActivityFavour(String.valueOf(giftAmount+(gift.getGiftAmount()*quantity)));
                    }
                }
            }
            dataInfos.add(orderInfo);
        }
        logger.debug("最终拼装到邮件中安心月报表的活动分享信息数据条目为{}", dataInfos.size());
        orderInfos.addAll(dataInfos);
        orderInfos.add(new PurchaseOrderInfo());//添加一个空行
        orderInfoListMap.put(EMAIL_KEY, orderInfos);
    }

    /**
     * 将停复驶信息封装到邮件中
     * @param insuranceDatas
     * @param orderInfoListMap
     * @param insuranceFlag
     */
    private void pushInsuranceDataToEmail(List<Object[]> insuranceDatas, Map<String,List<PurchaseOrderInfo>> orderInfoListMap, Integer insuranceFlag){
        List<PurchaseOrderInfo> orderInfos = orderInfoListMap.get(EMAIL_KEY);
        List<PurchaseOrderInfo> dataInfos = new ArrayList<PurchaseOrderInfo>();
        for(Object[] insuranceData : insuranceDatas){
            PurchaseOrderInfo orderInfo = new PurchaseOrderInfo();
            orderInfo.setIssueTime(StringUtil.formatTimeToString(insuranceData[0]));
            orderInfo.setPaymentPlatform(StringUtil.defaultNullStr(insuranceData[1]));
            orderInfo.setOwner(StringUtil.defaultNullStr(insuranceData[2]));
            orderInfo.setLicenseNo(StringUtil.defaultNullStr(insuranceData[3]));
            orderInfo.setOrderNo(StringUtil.defaultNullStr(insuranceData[4]));
            orderInfo.setPremiumSum(StringUtil.defaultNullStr(insuranceData[5]));
            orderInfo.setCompulsoryPremium(StringUtil.defaultNullStr(insuranceData[6]));
            orderInfo.setAutoTax(StringUtil.defaultNullStr(insuranceData[7]));
            orderInfo.setCommecialPremium(StringUtil.defaultNullStr(insuranceData[8]));
            orderInfo.setDamagePremium(StringUtil.defaultNullStr(insuranceData[9]));
            orderInfo.setActivityFavour(StringUtil.defaultNullStr(insuranceData[10]));
            dataInfos.add(orderInfo);
        }
        logger.debug("最终拼装到邮件中安心月报表的{}信息数据条目为{}", insuranceFlag == 1 ? "停驶":"复驶", dataInfos.size());
        orderInfos.addAll(dataInfos);
        if(insuranceFlag == 1)//封装停驶信息后，添加空行；复驶信息后没必要再添加空白行
            orderInfos.add(new PurchaseOrderInfo());
        orderInfoListMap.put(EMAIL_KEY, orderInfos);
    }
}
