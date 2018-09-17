package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.OrderOperationInfo;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by xu.yelong on 2016/12/16.
 */
@Service
public class QuestionableOrderReportService {

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    public Map<String, Object> queryQestionableOrder(){
        Date startDate = DateUtils.getCustomDate(new Date(),-1,0,0,0);
        Date endDate = DateUtils.getCustomDate(new Date(),-1,23,59,59);

        List<OrderOperationInfo> orderOperationInfos=orderOperationInfoRepository.findQestionableOrderByUpdateTime(startDate,endDate);
        List<Map<String, String>> infoList = new ArrayList<>();
        for(OrderOperationInfo orderOperationInfo:orderOperationInfos){
            Map tempMap = new HashMap();
            tempMap.put("orderId",orderOperationInfo.getPurchaseOrder().getId());
            tempMap.put("orderNo",orderOperationInfo.getPurchaseOrder().getOrderNo());
            tempMap.put("orderStatus",orderOperationInfo.getPurchaseOrder().getStatus().getStatus());
            tempMap.put("orderTransmissionStatus",orderOperationInfo.getCurrentStatus().getStatus());
            infoList.add(tempMap);
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("startDate", DateUtils.getDateString(startDate, DateUtils.DATE_LONGTIME24_PATTERN));//统计开始时间
        paramMap.put("endDate", DateUtils.getDateString(endDate, DateUtils.DATE_LONGTIME24_PATTERN));//统计结束时间
        paramMap.put("infoList", infoList);//统计信息
        return paramMap;
    }
}
