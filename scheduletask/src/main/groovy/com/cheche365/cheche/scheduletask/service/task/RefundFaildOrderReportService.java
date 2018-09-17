package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.OrderOperationInfo;
import com.cheche365.cheche.core.model.OrderTransmissionStatus;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinJianBin on 2017/2/10.
 */
@Service
public class RefundFaildOrderReportService {
    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    public List<PurchaseOrderInfo> getEmailInfoList() {

        OrderTransmissionStatus refundFailed = OrderTransmissionStatus.Enum.REFUND_FAILED;
        List<OrderOperationInfo> refundFaildList = orderOperationInfoRepository.findByCurrentStatus(refundFailed);


        List<PurchaseOrderInfo> purchaseOrderInfoList = new ArrayList<>();
        for (int i = 0; i < refundFaildList.size(); i++) {
            OrderOperationInfo orderOperationInfo = refundFaildList.get(i);
            PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
            int num = i + 1;
            purchaseOrderInfo.setMchId(num + "");
            purchaseOrderInfo.setOrderNo(orderOperationInfo.getPurchaseOrder().getOrderNo());
            purchaseOrderInfo.setTime(DateUtils.getDateString(orderOperationInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
            purchaseOrderInfo.setAssigner(orderOperationInfo.getAssigner().getName());

            purchaseOrderInfoList.add(purchaseOrderInfo);
        }

        return purchaseOrderInfoList;
    }

}
