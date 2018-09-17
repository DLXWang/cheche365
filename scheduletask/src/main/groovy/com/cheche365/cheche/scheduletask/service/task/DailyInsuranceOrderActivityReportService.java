package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 安心用户下单和用户活动信息查询
 * Created by Luly on 2017/3/14.
 */
@Service
public class DailyInsuranceOrderActivityReportService {
    Logger logger = LoggerFactory.getLogger(DailyInsuranceOrderActivityReportService.class);
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String COLLECTION_NAME = "moApplicationLog";

    public Map<String, List<PurchaseOrderInfo>> getDailyOrderActivityData() {

        //安心报价用户数量
        List<PurchaseOrderInfo> offerInfoList = getOfferUserAmount();
        //按状态统计安心订单
        List<Object[]> orderList = purchaseOrderRepository.findDataByPurchaseOrderStatus();
        logger.debug("获取符合条件的安心订单总量为{}", orderList.size());
        List<PurchaseOrderInfo> orderInfoList = handleDataList(orderList);
        //安心活动情况统计
        List<Object[]> activityList = new ArrayList<>();
        List<PurchaseOrderInfo> activityInfoList = dealActivityList(activityList);
        //领红包情况统计
        List<Object[]> redPacketList = new ArrayList<>();
        List<PurchaseOrderInfo> redPacketInfoList = handleDataList(redPacketList);

        Map<String, List<PurchaseOrderInfo>> purchaseOrderInfoListMap = new HashMap<>();
        purchaseOrderInfoListMap.put("offerInfoList",offerInfoList);
        purchaseOrderInfoListMap.put("orderInfoList",orderInfoList);
        purchaseOrderInfoListMap.put("activityInfoList",activityInfoList);
        purchaseOrderInfoListMap.put("redPacketInfoList",redPacketInfoList);
        return purchaseOrderInfoListMap;
    }

    private List<PurchaseOrderInfo> getOfferUserAmount(){
        List<PurchaseOrderInfo> dataList = new ArrayList<PurchaseOrderInfo>();
        PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();

        /******************   原MySql逻辑    start  ******************/
        /*purchaseOrderInfo.setAmount(String.valueOf(applicationLogRepository.findByLogMessageAndLogType()));*/
        /******************   原MySql逻辑    end  ******************/


        /******************   MongoDB逻辑    start  ******************/
        purchaseOrderInfo.setAmount(String.valueOf(getUserAmount()));
        /******************   MongoDB逻辑    end  ******************/

        dataList.add(purchaseOrderInfo);
        return dataList;
    }

    private Integer getUserAmount(){
        Date endDate = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        Date startDate = DateUtils.getDayStartTime(endDate);

        /**
         * mongoRepository，没查到如何使用distinct进行去重，在此使用API查询distinct后再求数量
         * **/
        Criteria criteria = Criteria.where("logType._id").is(LogType.Enum.Quote_Cache_Record_31.getId())
                                    .and("createTime").gt(startDate).lt(endDate)
                                    .and("logMessage.insuranceCompany.id").is(65000L);
        Query query = Query.query(criteria);
        return mongoTemplate.getCollection(COLLECTION_NAME).distinct("user", query.getQueryObject()).size();
    }

    private List<PurchaseOrderInfo> handleDataList( List<Object[]> orderList){
        List<PurchaseOrderInfo> dataList = new ArrayList<PurchaseOrderInfo>();
        if (!CollectionUtils.isEmpty(orderList)) {
            for (Object[] obj : orderList) {
                PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
                purchaseOrderInfo.setOrderStatus(String.valueOf(obj[0]));
                purchaseOrderInfo.setAmount(String.valueOf(obj[1]));
                dataList.add(purchaseOrderInfo);
            }
        }
        return dataList;
    }

    private List<PurchaseOrderInfo> dealActivityList( List<Object[]> paramsList){
        List<PurchaseOrderInfo> dataList = new ArrayList<PurchaseOrderInfo>();
        if (!CollectionUtils.isEmpty(paramsList)) {
            for (Object[] obj : paramsList) {
                PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
                purchaseOrderInfo.setFieldName("红包领取");
                purchaseOrderInfo.setAmount(String.valueOf(obj[1]));
                dataList.add(purchaseOrderInfo);
            }
        }
        return dataList;
    }
}
