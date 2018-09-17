package com.cheche365.cheche.scheduletask.service.task

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.scheduletask.model.PartRebateInfo
import com.pingplusplus.model.OrderRefund
import com.pingplusplus.model.OrderRefundCollection
import com.pingplusplus.model.Refund;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.math.BigInteger;
import java.util.*;

@Service
public class PartRebateService {
    Logger logger = LoggerFactory.getLogger(PartRebateService.class);
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    Map<String,List<PartRebateInfo>> getPartRebate(){
    //当天
    Date endTime = DateUtils.getDate(DateUtils.getCustomDate(new Date(),0,0,0,0),DateUtils.DATE_LONGTIME24_PATTERN);
    //前一天
    Date startTime = DateUtils.getDate(DateUtils.getCustomDate(new Date(),-1,0,0,0),DateUtils.DATE_LONGTIME24_PATTERN);

    List<Payment> poList =paymentRepository.findPingPlusByTimeBetween(startTime,endTime);


    List<Refund> partRefund = new ArrayList<>();
    List<PartRebateInfo> partInfo = new ArrayList<>();
    Map<String,List<PartRebateInfo>> partInfoMap = new HashMap<>();
    for(Payment pm : poList){
       if(pm.itpNo != null && pm.thirdpartyPaymentNo != null)
       {
           partRefund.addAll(OrderRefund.list(pm.itpNo).getData());
       }
    }

    for(Refund pr:partRefund){
        PartRebateInfo tag = new PartRebateInfo();
        tag.setOrderStatus(pr.getStatus());
        tag.setOrderNo(pr.getChargeOrderNo().substring(0,pr.getChargeOrderNo().length()-4));
        tag.setChargeOrderNo(pr.getChargeOrderNo());
        tag.setAmount(String.valueOf(pr.getAmount()/100)+"元");
        partInfo.add(tag);
    }
        partInfoMap.put("dataInputAmount",partInfo);
        return partInfoMap;
    }

}
