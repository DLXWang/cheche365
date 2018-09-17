package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.core.model.OrderOperationInfo;
import com.cheche365.cheche.core.model.OrderTransmissionStatus;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.core.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理用户登录信息中城市为空的数据
 * Created by xu.cxy on 2016/10/12.
 */
@Service
public class OrderTransmissionStatusChgSyncTask extends BaseTask {

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private PaymentRepository paymentRepository;


    private Logger logger = LoggerFactory.getLogger(OrderTransmissionStatusChgSyncTask.class);

    @Override
    protected void doProcess() throws Exception {
        List<OrderOperationInfo> orderOperationInfoList = orderOperationInfoRepository.findByCurrentStatus(OrderTransmissionStatus.Enum.UNCONFIRMED);
        List<OrderOperationInfo> chgOrderList = new ArrayList<>();
        logger.debug("change unpaid order to unpaidStatus,unconfirmList size=>{}" , orderOperationInfoList.size());
        for(int i=0;i<orderOperationInfoList.size();i++){
            OrderOperationInfo orderOperationInfo = orderOperationInfoList.get(i);
            Payment payment = paymentRepository.findFirstByChannelAndPurchaseOrderOrderByIdDesc(orderOperationInfo.getPurchaseOrder().getChannel(),orderOperationInfo.getPurchaseOrder());
            if(payment != null && payment.getStatus().getId().equals(PaymentStatus.Enum.NOTPAYMENT_1.getId())){
                orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.UNPAID);
                chgOrderList.add(orderOperationInfo);
            }
        }
        orderOperationInfoRepository.save(chgOrderList);
        logger.debug("change unpaid order to unpaidStatus success size is =>{}", chgOrderList.size());
    }
}
