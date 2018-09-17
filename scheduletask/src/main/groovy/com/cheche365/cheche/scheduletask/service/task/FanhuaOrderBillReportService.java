package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.GiftType;
import com.cheche365.cheche.core.model.OrderSourceType;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by wangshaobin on 2017/5/27.
 */
@Service
public class FanhuaOrderBillReportService {
    Logger logger = LoggerFactory.getLogger(FanhuaOrderBillReportService.class);

    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";

    private static final String JD_CARD = "京东卡";
    private static final String FUEL_CARD = "加油卡";
    private static final String GIFT_DETAIL = "giftDetail";
    private static final String GIFT_DATA = "giftData";

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private FanhuaOrderGiftReportService fanhuaOrderGiftReportService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 获取泛华成单对账信息
     * **/
    public Map<String,List<PurchaseOrderInfo>> getFanhuaOrderBillData(){
        Map<String, Date> dateMap = fanhuaOrderGiftReportService.getTimeParams(TaskConstants.FANHUA_ORDER_BILL_SYNC_TIME);
        //数据的获取
        List<OrderStatus> statusList = Arrays.asList(OrderStatus.Enum.FINISHED_5);
        List<Object[]> list = purchaseOrderRepository.findFanhuaOrderBillData(dateMap.get(START_TIME), dateMap.get(END_TIME), statusList, OrderSourceType.Enum.PLANTFORM_BX_5);
        logger.debug("获取时间点大于{}的泛华成单对账报表数据为{}条", DateUtils.getDateString(dateMap.get(START_TIME), DateUtils.DATE_LONGTIME24_PATTERN), list.size());
        //数据的拼装
        LinkedHashMap<String, Map> formatData = formatGiftData(list);
        Map<String,List<PurchaseOrderInfo>> excelInfo = getFanhuaOrderBillDataExcelSheetInfo(formatData);
        //更新redis
        stringRedisTemplate.opsForValue().set(TaskConstants.FANHUA_ORDER_BILL_SYNC_TIME, DateUtils.getDateString(dateMap.get(END_TIME), DateUtils.DATE_LONGTIME24_PATTERN));
        return excelInfo;
    }

    private Map<String,List<PurchaseOrderInfo>> getFanhuaOrderBillDataExcelSheetInfo(LinkedHashMap<String, Map> billData){
        Map<String,List<PurchaseOrderInfo>> purchaseOrderInfoListMap = new HashMap<>();
        List<PurchaseOrderInfo> purchaseOrderfoList = new ArrayList<PurchaseOrderInfo>();
        if (!billData.isEmpty()){
            for (Map.Entry<String, Map> entry : billData.entrySet()){
                Map orderMap = entry.getValue();
                Object[] obj = (Object[]) orderMap.get("orderData");
                PurchaseOrderInfo info = new PurchaseOrderInfo();
                info.setIssueTime(StringUtil.defaultNullStr(obj[7]));
                info.setPaymentPlatform(StringUtil.defaultNullStr(obj[8]));
                info.setOwner(StringUtil.defaultNullStr(obj[9]));
                info.setLicenseNo(StringUtil.defaultNullStr(obj[10]));
                info.setOrderNo(StringUtil.defaultNullStr(obj[11]));
                info.setPremiumSum(StringUtil.defaultNullStr(obj[12]));
                info.setCompulsoryPremium(StringUtil.defaultNullStr(obj[13]));
                info.setAutoTax(StringUtil.defaultNullStr(obj[14]));
                info.setCommecialPremium(StringUtil.defaultNullStr(obj[15]));
                info.setDamagePremium(StringUtil.defaultNullStr(obj[16]));
                info.setPaymentChannel(StringUtil.defaultNullStr(obj[17]));
                //拼装各类礼品信息
                Map giftMap = (Map) orderMap.get(GIFT_DATA);
                info.setPaymentDiscountAmount(StringUtil.defaultNullStr(giftMap.get("paymentDiscount")));
                info.setOfflineCashBackSum(StringUtil.defaultNullStr(giftMap.get("cashBack")));
                info.setFuelCard(StringUtil.defaultNullStr(giftMap.get(FUEL_CARD)));
                info.setJdCard(StringUtil.defaultNullStr(giftMap.get(JD_CARD)));

                String gift = giftMap.containsKey(GIFT_DETAIL) ? String.join("；", (List) giftMap.get(GIFT_DETAIL)) : "";
                info.setGiftDetail(StringUtil.defaultNullStr(gift));
                purchaseOrderfoList.add(info);
            }
        }
        purchaseOrderInfoListMap.put("billData", purchaseOrderfoList);
        logger.debug("拼装到Excel的泛华成单对账报表数据为{}条", purchaseOrderfoList.size());
        return purchaseOrderInfoListMap;
    }

    /**
     * 将一个订单有多个礼品的情况，进行礼品的合并
     * @param list
     * @return
     */
    private LinkedHashMap<String, Map> formatGiftData(List<Object[]> list){
        LinkedHashMap<String, Map> map = new LinkedHashMap<String, Map>();
        if (CollectionUtils.isNotEmpty(list)){
            for (Object[] obj : list){
                String orderNo = StringUtil.defaultNullStr(obj[11]);
                Map orderData = map.containsKey(orderNo) ? map.get(orderNo) : new HashMap<>();
                Map giftDetailsMap = orderData.containsKey(GIFT_DATA) ? (Map) orderData.get(GIFT_DATA) : new HashMap<>();
                setGiftDetail(giftDetailsMap, obj);
                orderData.put("orderData", obj);//有用数据为排除礼品之外的数据
                orderData.put(GIFT_DATA, giftDetailsMap);
                map.put(orderNo, orderData);
            }
        }
        return map;
    }

    private void setGiftDetail(Map giftDetailsMap, Object[] obj){
        if(obj[11]!=null){
            String giftTypeId = StringUtil.defaultNullStr(obj[18]);
            String category = StringUtil.defaultNullStr(obj[19]);
            String giftTypeName = StringUtil.defaultNullStr(obj[4]);
            String giftAmount = StringUtil.defaultNullStr(obj[1]);
            if(JD_CARD.equals(giftTypeName) || FUEL_CARD.equals(giftTypeName))//加油卡或者京东卡
                giftDetailsMap.put(giftTypeName, giftTypeName);
            else if(GiftType.Enum.CASH_36.getId().toString().equals(giftTypeId))//返现
                giftDetailsMap.put("cashBack", giftAmount);
            else if(!"4".equals(category) && !"6".equals(category))//减免
                giftDetailsMap.put("paymentDiscount", giftAmount);
            else if("6".equals(category)){//实物礼品
                List<String> giftList = giftDetailsMap.containsKey(GIFT_DETAIL) ? (List) giftDetailsMap.get(GIFT_DETAIL) : new ArrayList<>();
                fanhuaOrderGiftReportService.setGiftDetail(giftList, obj);
                giftDetailsMap.put(GIFT_DETAIL, giftList);
            }
        }
    }
}
