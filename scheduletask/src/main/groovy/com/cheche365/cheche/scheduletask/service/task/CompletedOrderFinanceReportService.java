package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoweifu on 2015/12/4.
 */
@Service
public class CompletedOrderFinanceReportService {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RedisTemplate redisTemplate;


    public List<PurchaseOrderInfo> getPurchaseOrderInfos() {
        // 查询订单范围日期 type:1-第一次查询，查询所有订单；2-非第一次查询，查询一周订单
        int type = 1;
        boolean notExist = redisTemplate.opsForValue().setIfAbsent("first.completed.order", 1);
        if(!notExist) {
            type = 2;
        }
        List<PurchaseOrder> purchaseOrderList = purchaseOrderService.findCompletedOrderByWeek(type);

        // 客服订单数据
        List<PurchaseOrderInfo> purchaseOrderInfoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(purchaseOrderList)) {
            for (PurchaseOrder purchaseOrder : purchaseOrderList) {
                PurchaseOrderInfo purchaseOrderInfo = getPurchaseOrderInfo(purchaseOrder);
                purchaseOrderInfoList.add(purchaseOrderInfo);
            }
        }
        return purchaseOrderInfoList;
    }

    // 支付日期、渠道（代理、个人、大客户等，能具体到哪一个代理人和大客户最好）、姓名、车牌号、优惠前金额、优惠后金额
    private PurchaseOrderInfo getPurchaseOrderInfo(PurchaseOrder purchaseOrder) {
        PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
        // 支付日期
        List<PaymentChannel> paymentChannels = new ArrayList<>();
        paymentChannels.addAll(PaymentChannel.Enum.ONLINE_CHANNELS);
        Payment payment = paymentRepository.findFirstByChannelInAndPurchaseOrderOrderByIdDesc(paymentChannels, purchaseOrder);
        if(payment != null) {
            purchaseOrderInfo.setPaymentDate(DateUtils.getDateString(payment.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        } else {
            purchaseOrderInfo.setPaymentDate("");
        }
        // 渠道，代理、个人、大客户等，能具体到哪一个代理人和大客户最好
        purchaseOrderInfo.setSource(purchaseOrderService.getUserSource(purchaseOrder));
        // 车主
        purchaseOrderInfo.setOwner(purchaseOrder.getAuto().getOwner());
        // 车牌
        purchaseOrderInfo.setLicenseNo(purchaseOrder.getAuto().getLicensePlateNo());
        // 优惠前金额，应付金额
        purchaseOrderInfo.setPayableAmount(String.valueOf(purchaseOrder.getPayableAmount()));
        // 优惠后金额，实付金额
        purchaseOrderInfo.setPaidAmount(String.valueOf(purchaseOrder.getPaidAmount()));
        // 支付日期

        return purchaseOrderInfo;
    }
}
