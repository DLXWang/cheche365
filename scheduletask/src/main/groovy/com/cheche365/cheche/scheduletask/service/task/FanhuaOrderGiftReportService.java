package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.GiftType;
import com.cheche365.cheche.core.model.OrderSourceType;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.repository.GiftTypeRepository;
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
 * Created by wangshaobin on 2017/5/31.
 */
@Service
public class FanhuaOrderGiftReportService {
    Logger logger = LoggerFactory.getLogger(FanhuaOrderGiftReportService.class);

    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String GIFT_DATA = "giftData";

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
	
    @Autowired
    private GiftTypeRepository giftTypeRepository;

    /**
     * 获取泛华礼品对账信息
     * **/
    public Map<String,List<PurchaseOrderInfo>> getFanhuaOrderGiftData(){
        Map<String, Date> dateMap = getTimeParams(TaskConstants.FANHUA_ORDER_GIFT_SYNC_TIME);
        //数据的获取
        List<OrderStatus> statusList = Arrays.asList(OrderStatus.Enum.FINISHED_5);
        List<Object[]> list = purchaseOrderRepository.findFanhuaOrderGiftData(dateMap.get(START_TIME), dateMap.get(END_TIME), statusList, OrderSourceType.Enum.PLANTFORM_BX_5);
        logger.debug("获取时间点大于{}的泛华成单礼品报表数据为{}条", DateUtils.getDateString(dateMap.get(START_TIME), DateUtils.DATE_LONGTIME24_PATTERN), list.size());
        LinkedHashMap<String, Map> formatData = formatGiftData(list);
        Map<String,List<PurchaseOrderInfo>> excelInfo = getFanhuaOrderGiftDataExcelSheetInfo(formatData);
        //数据的拼装
        //更新redis
        stringRedisTemplate.opsForValue().set(TaskConstants.FANHUA_ORDER_GIFT_SYNC_TIME, DateUtils.getDateString(dateMap.get(END_TIME), DateUtils.DATE_LONGTIME24_PATTERN));
        return excelInfo;
    }

    /**
     * 将一个订单有多个礼品的情况，进行礼品的合并
     * @param list
     * @return
     */
    private LinkedHashMap<String, Map> formatGiftData(List<Object[]> list){
        LinkedHashMap<String, Map> map = new LinkedHashMap<String, Map>();
        if (CollectionUtils.isNotEmpty(list))
            for (Object[] obj : list){
                String orderNo = StringUtil.defaultNullStr(obj[9]);
                Map orderData = map.containsKey(orderNo) ? map.get(orderNo) : new HashMap<>();
                List<String> giftDetailsList = orderData.containsKey(GIFT_DATA) ? (List) orderData.get(GIFT_DATA) : new ArrayList<>();
                String category = StringUtil.defaultNullStr(obj[21]);
                if("4".equals(category) || "6".equals(category))//根据订单详情中的礼品信息取值逻辑，只有category为4或者6的，才进行展示
                    setGiftDetail(giftDetailsList, obj);
                orderData.put("orderData", obj);//有用数据为排除礼品之外的数据
                orderData.put(GIFT_DATA, giftDetailsList);//一个订单对应多条礼品信息时，进行礼品的拼接
                map.put(orderNo, orderData);
            }
        return map;
    }

    private Map<String,List<PurchaseOrderInfo>> getFanhuaOrderGiftDataExcelSheetInfo(LinkedHashMap<String, Map> giftData){
        Map<String,List<PurchaseOrderInfo>> purchaseOrderInfoListMap = new HashMap<>();
        List<PurchaseOrderInfo> purchaseOrderfoList = new ArrayList<PurchaseOrderInfo>();
        if (!giftData.isEmpty()){
            for (Map.Entry<String, Map> entry : giftData.entrySet()){
                Map orderMap = entry.getValue();
                Object[] obj = (Object[]) orderMap.get("orderData");
                PurchaseOrderInfo info = new PurchaseOrderInfo();
                info.setChannel(StringUtil.defaultNullStr(obj[7]));
                info.setArea(StringUtil.defaultNullStr(obj[8]));
                info.setOrderNo(StringUtil.defaultNullStr(obj[9]));
                info.setMobile(StringUtil.defaultNullStr(obj[10]));
                info.setLicenseNo(StringUtil.defaultNullStr(obj[11]));
                info.setInsuredName(StringUtil.defaultNullStr(obj[12]));
                info.setInsuranceCompany(StringUtil.defaultNullStr(obj[13]));
                info.setCompulsoryPremium(StringUtil.defaultNullStr(obj[14]));
                info.setCommecialPremium(StringUtil.defaultNullStr(obj[15]));
                info.setComment("");
                info.setLinkMan(StringUtil.defaultNullStr(obj[16]));
                info.setLinkPhone(StringUtil.defaultNullStr(obj[17]));
                StringBuffer address = new StringBuffer();
                address.append(StringUtil.defaultNullStr(obj[22]))//省
                    .append(StringUtil.defaultNullStr(obj[18]))//市
                    .append(StringUtil.defaultNullStr(obj[19]))//区
                    .append(StringUtil.defaultNullStr(obj[20]));//街道
                info.setDeliveryAddress(address.toString());
                String gift = String.join("；", (List) orderMap.get(GIFT_DATA));
                info.setGiftDetail(gift);
                purchaseOrderfoList.add(info);
            }
        }
        purchaseOrderInfoListMap.put(GIFT_DATA, purchaseOrderfoList);
        logger.debug("拼装到Excel的泛华成单礼品报表数据为{}条", purchaseOrderfoList.size());
        return purchaseOrderInfoListMap;
    }

    public Map<String, Date> getTimeParams(String redisKey){
        /**
         * 定时任务首次执行为昨天0点到今天0点，并将执行日期记录到redis
         * 之后再次执行从redis中读取上次执行日期的0点，到当前日期的0点
         * **/
        String logStr = TaskConstants.FANHUA_ORDER_GIFT_SYNC_TIME.equals(redisKey) ? "泛华成单礼品报表" : "泛华成单对账报表";
        Map<String, Date> map = new HashMap<String, Date>();
        Date startTime = null;
        Date now = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        Date endTime = DateUtils.getDate(now, DateUtils.DATE_LONGTIME24_START_PATTERN);//今天的0点
        String previousTimeStr = stringRedisTemplate.opsForValue().get(redisKey);
        logger.debug("从redis中获取{}的定时任务上次执行时间为{}", logStr, previousTimeStr);
        if(!StringUtils.isEmpty(previousTimeStr))
            startTime = DateUtils.getDate(previousTimeStr,DateUtils.DATE_LONGTIME24_PATTERN);//上次执行日期的0点
        else //如果定时任务第一次执行，redis中没有上次执行时间，默认取昨天零点
            startTime = DateUtils.getDate(DateUtils.getCustomDate(now,-1,0,0,0), DateUtils.DATE_LONGTIME24_PATTERN);//昨天的0点
        map.put(START_TIME, startTime);
        map.put(END_TIME, endTime);
        return map;
    }

    public void setGiftDetail(List<String> giftDetailsList, Object[] obj){
        /**
         *  0            1            2                3           4           5           6
         * g.quantity,g.gift_amount,g.gift_type,g.gift_content,gt.`name`,g.gift_display,g.unit
         * **/
        int quantity = obj[0] == null ? 1 : Integer.parseInt(String.valueOf(obj[0]));
        if (obj[1] == null) {
			GiftType giftType = null;
            if (obj[2] != null)
                giftType = giftTypeRepository.findOne(Long.parseLong(String.valueOf(obj[2])));
            giftDetailsList.add(
                (BeanUtil.equalsID(giftType, GiftType.Enum.INSURE_GIVE_GIFT_PACK_29) ? StringUtil.defaultNullStr(obj[3]) : StringUtil.defaultNullStr(obj[4]))
                + "：" + quantity + (obj[6] == null ? "" : StringUtil.defaultNullStr(obj[6])));
        } else {
            giftDetailsList.add(obj[4] + "：" + StringUtil.defaultNullStr(obj[5]) + " * " + quantity + (obj[6] == null ? "" : StringUtil.defaultNullStr(obj[6])));
        }
    }
}
