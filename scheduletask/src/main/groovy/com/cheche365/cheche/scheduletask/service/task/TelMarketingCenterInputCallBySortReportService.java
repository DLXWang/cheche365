package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.repository.TelMarketingCenterRepeatRepository;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by Luly on 2016/12/22.
 */
@Service
public class TelMarketingCenterInputCallBySortReportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterInputCallBySortReportService.class);
    @Autowired
    private TelMarketingCenterRepeatRepository telMarketingCenterRepeatRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根据渠道和地区获取进库量和跟进信息
     *
     * @return
     */
    public Map<String, List<PurchaseOrderInfo>> getDataBySort() {
        Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        Date startTime = DateUtils.getCustomDate(currentTime, -1, 17, 0, 0);
        Date endTime = DateUtils.getCustomDate(currentTime, -0, 17, 0, 0);

        //按渠道统计获取查询时间内的进库量
        List<Object[]> channelInputAmount = telMarketingCenterRepeatRepository.findInputAmountByChannel(startTime, endTime);
        //按渠道统计获取查询时间内的拨打量
        List<Object[]> channelCallAmount = telMarketingCenterRepeatRepository.findCallAmountByChannel(startTime, endTime);
        //按地区统计获取查询时间内的进库量
        List<Object[]> areaInputAmount = telMarketingCenterRepeatRepository.findInputAmountByArea(startTime, endTime);
        //按地区统计获取查询时间内的拨打量
        List<Object[]> areaCallAmount = telMarketingCenterRepeatRepository.findCallAmountByArea(startTime, endTime);
        //按行为统计获取查询时间内的进库量
        List<Object[]> behaviorInputAmount = telMarketingCenterRepeatRepository.findInputAmountByBehavior(startTime, endTime);
        //按行为统计获取查询时间内的拨打量
        List<Object[]> behaviorCallAmount = telMarketingCenterRepeatRepository.findCallAmountByBehavior(startTime, endTime);
        //按新用户行为统计获取查询时间内的进库量
        List<Object[]> newUserBehaviorInputAmount = telMarketingCenterRepeatRepository.findInputAmountByNewUserBehavior(startTime, endTime);
        //按新用户行为统计获取查询时间内的拨打量
        List<Object[]> newUserBehaviorCallAmount = telMarketingCenterRepeatRepository.findCallAmountByNewUserBehavior(startTime, endTime);

        Map<String, List<PurchaseOrderInfo>> purchaseOrderInfoListMap = new HashMap<>();

        List<PurchaseOrderInfo> channelAmount = getDataSourceExcelSheetInfo(channelInputAmount, channelCallAmount);
        List<PurchaseOrderInfo> areaAmount = getDataSourceExcelSheetInfo(areaInputAmount, areaCallAmount);

        List<PurchaseOrderInfo> behaviorAmount = getDataSourceExcelSheetInfo(behaviorInputAmount, behaviorCallAmount);
        List<PurchaseOrderInfo> newUserBehaviorAmount = getDataSourceExcelSheetInfo(newUserBehaviorInputAmount, newUserBehaviorCallAmount);

        purchaseOrderInfoListMap.put("channelAmount", channelAmount);
        purchaseOrderInfoListMap.put("areaAmount", areaAmount);
        purchaseOrderInfoListMap.put("behaviorAmount", behaviorAmount);
        purchaseOrderInfoListMap.put("newUserBehaviorAmount", newUserBehaviorAmount);
        return purchaseOrderInfoListMap;
    }

    private List<PurchaseOrderInfo> getDataSourceExcelSheetInfo(List<Object[]> InputAmount, List<Object[]> CallAmount) {
        List<PurchaseOrderInfo> sortAmount = new ArrayList<PurchaseOrderInfo>();
        if (!CollectionUtils.isEmpty(InputAmount)) {
            Map channelCall = getCallMap(CallAmount);//获取分类名称和对应拨打量
            for (Object[] obj : InputAmount) {
                PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
                purchaseOrderInfo.setFieldName(String.valueOf(obj[0]));
                purchaseOrderInfo.setInputAmount(String.valueOf(obj[1]));
                if (channelCall.containsKey(String.valueOf(obj[0]))) {
                    purchaseOrderInfo.setCallAmount(String.valueOf(channelCall.get(obj[0])));
                }else{
                    purchaseOrderInfo.setCallAmount("0");
                }
                sortAmount.add(purchaseOrderInfo);
            }
        }
        return sortAmount;
    }

    private Map getCallMap(List<Object[]> callAmountSort) {
        Map callAmount = new HashMap();
        if (!CollectionUtils.isEmpty(callAmountSort)) {
            callAmountSort.forEach(obj -> {
                callAmount.put(String.valueOf(obj[0]), String.valueOf(obj[1]));
            });
        }
        return callAmount;
    }
}
