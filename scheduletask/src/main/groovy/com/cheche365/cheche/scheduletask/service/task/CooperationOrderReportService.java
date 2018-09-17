package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.OrderCooperationInfoService;
import com.cheche365.cheche.core.service.OrderOperationInfoService;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by xu.yelong on 2016-03-25.
 */
@Service
public class CooperationOrderReportService {

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;

    public Map getPurchaseOrderInfo(){
        List<Long> statusList=Arrays.asList(OrderStatus.Enum.PAID_3.getId(),OrderStatus.Enum.DELIVERED_4.getId());
        List<Long> sourceList= this.getSourceList();
        List<OrderOperationInfo> orderOperationInfoList = orderOperationInfoService.findCooperationOrderByOperateStatus(statusList,sourceList);
        List<OrderCooperationInfo> orderCooperationInfoList=orderCooperationInfoService.findCooperationOrderByOperateStatus(statusList,sourceList);
        List purchaseOrderInfoList=new ArrayList<>();
        Map purchaseOrderInfoMap=new HashMap();
        for (OrderOperationInfo orderOperationInfo : orderOperationInfoList) {
            PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
            PurchaseOrder purchaseOrder=orderOperationInfo.getPurchaseOrder();
            purchaseOrderInfo.setSource(purchaseOrder.getSourceChannel().getDescription());
            purchaseOrderInfo.setOrderNo(purchaseOrder.getOrderNo());
            purchaseOrderInfo.setOrderTime(DateUtils.getDateString(purchaseOrder.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
            purchaseOrderInfo.setOperateStatus(orderOperationInfo.getCurrentStatus().getStatus());
            purchaseOrderInfo.setAssigner(orderOperationInfo.getOwner().getName());
            purchaseOrderInfoList.add(purchaseOrderInfo);
        }
        purchaseOrderInfoMap.put("orderOperationInfo",purchaseOrderInfoList);
        purchaseOrderInfoList=new ArrayList<>();
        for(OrderCooperationInfo orderCooperationInfo:orderCooperationInfoList){
            PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
            PurchaseOrder purchaseOrder=orderCooperationInfo.getPurchaseOrder();
            purchaseOrderInfo.setSource(purchaseOrder.getSourceChannel().getDescription());
            purchaseOrderInfo.setOrderNo(purchaseOrder.getOrderNo());
            purchaseOrderInfo.setOrderTime(DateUtils.getDateString(purchaseOrder.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
            purchaseOrderInfo.setOperateStatus(orderCooperationInfo.getStatus().getStatus());
            purchaseOrderInfo.setAssigner(orderCooperationInfo.getAssigner().getName());
            purchaseOrderInfoList.add(purchaseOrderInfo);
        }
        purchaseOrderInfoMap.put("orderCooperationInfo",purchaseOrderInfoList);
        return purchaseOrderInfoMap;
    }

    /**
     * 获取第三方来源渠道ID
     * @return
     */
    private List<Long> getSourceList(){
        List<Long> sourceList=new ArrayList<>();
        Channel.thirdPartnerChannels().forEach(channel -> sourceList.add(channel.getId()));
        Channel.orderCenterChannels().forEach(channel -> {
            if(!channel.getId().equals(Channel.Enum.ORDER_CENTER_11)){
                sourceList.add(channel.getId());
            }
        });
        return sourceList;
    }
}
