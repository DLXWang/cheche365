package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.DailyInsuranceRepository;
import com.cheche365.cheche.core.repository.DailyRestartInsuranceRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Luly on 2017/2/8.
 * 按天买车险用户数据情况统计
 */
@Service
public class DailyBuyInsuranceReportService {
    Logger logger = LoggerFactory.getLogger(DailyBuyInsuranceReportService.class);
    @Autowired
    private DailyInsuranceRepository dailyInsuranceRepository;
    @Autowired
    private DailyRestartInsuranceRepository dailyRestartInsuranceRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public Map<String, List<PurchaseOrderInfo>> getDailyBuyData() {

        //按状态统计历史按天买车险信息
        List<Object[]> historyList = purchaseOrderRepository.findHistoryDataByStatus();
        //按状态统计昨天按天买车险信息
        List<Object[]> orderList = purchaseOrderRepository.findDataByStatus();
        logger.debug("获取昨天按天买车险总量信息为{}", orderList.size());
        Map<String, List<OrderInfoExt>> orderDataMap = new HashMap();
        if (!CollectionUtils.isEmpty(orderList)) {
            List<OrderInfoExt> orderInfoList = convertToOrderInfoExt(orderList);
            orderDataMap = orderInfoList.stream().collect(Collectors.groupingBy(OrderInfoExt::getDescription));
        }
        //获得昨天和历史总量信息
        List<PurchaseOrderInfo> orderDataList = getDailyAndHistoryInfo(historyList, orderDataMap);
        //昨天下单订单号和状态详情
        List<PurchaseOrderInfo> orderNoAndStatusList = getOrderNoAndStatusInfo(orderList);

        //获取用户停驶行为数据(包含重复订单号)
        List<Object[]> stopList = dailyInsuranceRepository.findStopData();
        Map<String, List<StopAndRestartedExt>> stopDataMap = new HashMap();
        if (!CollectionUtils.isEmpty(stopList)) {
            List<StopAndRestartedExt> stopDataList = convertToStopOrRestartedInfo(stopList);
            stopDataMap = stopDataList.stream().collect(Collectors.groupingBy(StopAndRestartedExt::getOrderNo));
        }
        //处理用户停驶行为数据
        List<PurchaseOrderInfo> stopDataList = getStopOrRestartInfo(stopDataMap);
        logger.debug("获取用户停驶行为数据总量为{}", stopDataList.size());

        //获取用户复驶行为数据(包含重复订单号)
        List<Object[]> restartedList = dailyRestartInsuranceRepository.findRestartedData();
        Map<String, List<StopAndRestartedExt>> restartedDataMap = new HashMap();
        if (!CollectionUtils.isEmpty(restartedList)) {
            List<StopAndRestartedExt> restartedData = convertToStopOrRestartedInfo(restartedList);
            restartedDataMap = restartedData.stream().collect(Collectors.groupingBy(StopAndRestartedExt::getOrderNo));
        }
        //处理用户复驶行为数据
        List<PurchaseOrderInfo> restartedDataList = getStopOrRestartInfo(restartedDataMap);
        logger.debug("获取用户复驶行为数据总量为{}", restartedDataList.size());

        Map<String, List<PurchaseOrderInfo>> purchaseOrderInfoListMap = new HashMap<>();
        purchaseOrderInfoListMap.put("orderDataList",orderDataList);
        purchaseOrderInfoListMap.put("orderNoAndStatusList",orderNoAndStatusList);
        purchaseOrderInfoListMap.put("stopDataList",stopDataList);
        purchaseOrderInfoListMap.put("restartedDataList",restartedDataList);
        return purchaseOrderInfoListMap;
    }



    //获取按天买车险昨天和历史全部数据方法
    private List<PurchaseOrderInfo> getDailyAndHistoryInfo(List<Object[]> historyData, Map<String, List<OrderInfoExt>> orderDataMap) {
        List<PurchaseOrderInfo> dataList = new ArrayList<PurchaseOrderInfo>();
        if (!CollectionUtils.isEmpty(historyData)) {
            for (Object[] obj : historyData) {
                PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
                purchaseOrderInfo.setOrderStatus(String.valueOf(obj[0]));
                purchaseOrderInfo.setAmount(String.valueOf(obj[1]));//amount 作为按天买车险历史数量
                if (!CollectionUtils.isEmpty(historyData) && orderDataMap.containsKey(String.valueOf(obj[0]))) {
                    purchaseOrderInfo.setFieldName(String.valueOf(orderDataMap.get(obj[0]).size()));//fieldName 作为作为按天买车险每天数量
                }else{
                    purchaseOrderInfo.setFieldName("0");
                }
                dataList.add(purchaseOrderInfo);
            }
        }
        return dataList;
    }

    //昨天下单订单号和状态详情方法
    private List<PurchaseOrderInfo> getOrderNoAndStatusInfo(List<Object[]> dataList) {
        List<PurchaseOrderInfo> OrderNoAndStatusList = new ArrayList<PurchaseOrderInfo>();
        for (Object[] obj: dataList){
            PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
            purchaseOrderInfo.setOrderStatus(String.valueOf(obj[0]));
            purchaseOrderInfo.setOrderNo(String.valueOf(obj[1]));
            OrderNoAndStatusList.add(purchaseOrderInfo);
        }
        return OrderNoAndStatusList;
    }


    private List<PurchaseOrderInfo> getStopOrRestartInfo(Map<String, List<StopAndRestartedExt>> dataInfoMap) {
        List<PurchaseOrderInfo> dataList = new ArrayList<PurchaseOrderInfo>();
        if (!CollectionUtils.isEmpty(dataInfoMap)) {
            for (String orderNo : dataInfoMap.keySet()) {
                PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
                purchaseOrderInfo.setOrderNo(orderNo);
                purchaseOrderInfo.setSubmitTime(dataInfoMap.get(orderNo).get(0).getBeginDate());
                purchaseOrderInfo.setExpireTime(dataInfoMap.get(orderNo).get(0).getEndDate());
                purchaseOrderInfo.setAmount(dataInfoMap.get(orderNo).get(0).getTotalAmount().toString());//amount 作为按天买车险历史数量
                purchaseOrderInfo.setDays(dataInfoMap.get(orderNo).get(0).getDay());
                purchaseOrderInfo.setOperateTime(dataInfoMap.get(orderNo).get(0).getOperateTime());
                String insuranceName=dataInfoMap.get(orderNo).get(0).getInsuranceName();
                for(int i=1;i<dataInfoMap.get(orderNo).size();i++){
                    insuranceName += "," + dataInfoMap.get(orderNo).get(i).getInsuranceName();  //obj[0]为按天买车险历史数据的订单状态
                }
                purchaseOrderInfo.setFieldName(insuranceName);
                dataList.add(purchaseOrderInfo);
            }
        }
        return dataList;
    }

    //对OrderInfoExt属性赋值，返回存储其对象的List
    public List convertToOrderInfoExt(List<Object[]> orderInfoList) {
        List<OrderInfoExt> orderInfo = new ArrayList<>();
        for (Object[] obj : orderInfoList) {
            OrderInfoExt orderInfoExt = new OrderInfoExt();
            orderInfoExt.setDescription(String.valueOf(obj[0]));
            orderInfoExt.setOrderNo(String.valueOf(obj[1]));
            orderInfo.add(orderInfoExt);
        }
        return orderInfo;
    }
    //对StopAndRestartedExt类的属性赋值，返回存储其对象的List
    public List convertToStopOrRestartedInfo(List<Object[]> stopRestartList) {
        List<StopAndRestartedExt> stopAndRestartedList = new ArrayList<>();
        for (Object[] obj : stopRestartList) {
            StopAndRestartedExt stopAndRestartedExt = new StopAndRestartedExt();
            stopAndRestartedExt.setOrderNo(String.valueOf(obj[0]));
            stopAndRestartedExt.setBeginDate(String.valueOf(obj[1]));
            stopAndRestartedExt.setEndDate(String.valueOf(obj[2]));
            stopAndRestartedExt.setDay(String.valueOf(obj[3]));
            stopAndRestartedExt.setTotalAmount(Double.parseDouble(obj[4].toString()));
            stopAndRestartedExt.setInsuranceName(String.valueOf(obj[5]));
            stopAndRestartedExt.setOperateTime(String.valueOf(obj[6]).substring(0,19));
            stopAndRestartedList.add(stopAndRestartedExt);
        }
        return stopAndRestartedList;
    }
}

class OrderInfoExt {
    private String description;//状态名称
    private String orderNo;//订单号

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}

class StopAndRestartedExt{
    private String orderNo;//订单号
    private String beginDate;
    private String endDate;
    private String day;//停驶（复驶）天数
    private String insuranceName;//订单号
    private Double totalAmount; // 应退(应交)保费总额
    private String operateTime; // 操作时间

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

}
