package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.OrderOperationInfo;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.QuoteRecordService;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liufei on 2016/1/13.
 */
@Service
public class InsuranceOrderReportService {

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    public List<PurchaseOrderInfo> getPurchaseOrderInfos(){
        List<PurchaseOrderInfo> purchaseOrderInfoList = new ArrayList<>();
        String dateStr= DateUtils.getCurrentDateString("yyyy-MM-dd")+"%";
        List<OrderOperationInfo> insuranceOrderList=orderOperationInfoRepository.findOrderOperationInfoByDate(dateStr);
        if(!CollectionUtils.isEmpty(insuranceOrderList)){
            for(OrderOperationInfo orderOperationInfo:insuranceOrderList){
                purchaseOrderInfoList.add(getPurchaseOrderInfo(orderOperationInfo));
            }
        }
        return purchaseOrderInfoList;
    }

    private PurchaseOrderInfo getPurchaseOrderInfo(OrderOperationInfo orderOperationInfo){
        PurchaseOrderInfo purchaseOrderInfo=new PurchaseOrderInfo();
        PurchaseOrder purchaseOrder=orderOperationInfo.getPurchaseOrder();
        QuoteRecord quoteRecord=quoteRecordService.getById(purchaseOrder.getObjId());
        // 订单号
        purchaseOrderInfo.setOrderNo(purchaseOrder.getOrderNo());
        //日期
        purchaseOrderInfo.setOrderTime(DateUtils.getDateString(
                purchaseOrder.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        //姓名
        purchaseOrderInfo.setOwner(purchaseOrder.getAuto().getOwner());
        //车牌号
        purchaseOrderInfo.setLicenseNo(purchaseOrder.getAuto().getLicensePlateNo());
        //保险公司
        purchaseOrderInfo.setInsuranceCompany(quoteRecord.getInsuranceCompany().getName());
        //状态
        purchaseOrderInfo.setOrderStatus(orderOperationInfo.getCurrentStatus().getStatus());
        //渠道
        purchaseOrderInfo.setSource(purchaseOrderService.getUserSource(purchaseOrder));
        //负责人
        purchaseOrderInfo.setAssigner(orderOperationInfo.getAssigner().getName());
        //差价
        Double discountAmount= DoubleUtils.sub(purchaseOrder.getPayableAmount(), purchaseOrder.getPaidAmount());
        purchaseOrderInfo.setDiscountAmount(String.valueOf(discountAmount));
        return purchaseOrderInfo;
    }
}
