package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository;
import com.cheche365.cheche.core.service.UnifiedRefundHandler;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xu.yelong on 2016/11/16.
 */
@Service
public class OrderRefundReportService {

    Logger logger = LoggerFactory.getLogger(TelMarketingCenterRefundDataImportService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PurchaseOrderAmendRepository purchaseOrderAmendRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    List<UnifiedRefundHandler> refundPayChannelHandel;

    public Map<String, List<PurchaseOrderInfo>> getRefundOrders() {
        List<PurchaseOrderAmend> purchaseOrderAmendList = getRefundOrderAmends();
        List<PurchaseOrderInfo> refundOrderList = new ArrayList<>();
        List<Long> amendIds = new ArrayList<>();
        Map<String, List<PurchaseOrderInfo>> refundOrderMap = new HashMap();
        Map<String, PurchaseOrderInfo> mchIdStatisticsMap = new HashMap<>();
        for (PurchaseOrderAmend purchaseOrderAmend : purchaseOrderAmendList) {
            amendIds.add(purchaseOrderAmend.getId());
            PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
            Double amount = 0.0;
            List<Payment> payments = paymentRepository.findByPurchaseOrderAmendOrderByChannel(purchaseOrderAmend);
            String paymentChannel = "";
            String mchId = "";
            for (UnifiedRefundHandler r : refundPayChannelHandel) {
                for (Payment payment : payments) {
                    if (r.support(payment)) {
                        amount += payment.getAmount();
                        paymentChannel = payment.getChannel().getFullDescription();
                        mchId = payment.getUpstreamId().getMchId();
                        //非微信支付，mchId置为空。只有wechat有mchId
                        if(!PaymentChannel.Enum.isWeChat(payment.getChannel())){
                            mchId = null;
                        }
                        this.setMchIdAmount(mchIdStatisticsMap, mchId, paymentChannel, payment.getAmount());
                    }
                }
            }
            purchaseOrderInfo.setOrderNo(purchaseOrderAmend.getPurchaseOrder().getOrderNo());
            purchaseOrderInfo.setOperateTime(DateUtils.getDateString(purchaseOrderAmend.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
            purchaseOrderInfo.setPaymentChannel(paymentChannel);
            purchaseOrderInfo.setMchId(mchId);
            purchaseOrderInfo.setAmount(String.valueOf(amount));
            refundOrderList.add(purchaseOrderInfo);
        }
        refundOrderMap.put("refundOrders", refundOrderList);
        refundOrderMap.put("refundOrderStatistics", new ArrayList<>(mchIdStatisticsMap.values()));
        stringRedisTemplate.opsForValue().set(TaskConstants.OVERTIME_REFUND_AMEND_ID_CACHE, CacheUtil.doJacksonSerialize(amendIds));
        return refundOrderMap;
    }


    private void setMchIdAmount(Map<String, PurchaseOrderInfo> mchIdStatisticsMap, String mchId, String paymentChannel, Double amount) {
        if (mchIdStatisticsMap.containsKey(mchId + paymentChannel)) {
            PurchaseOrderInfo purchaseOrderInfo = mchIdStatisticsMap.get(mchId + paymentChannel);
            Double mchAmount = Double.parseDouble(purchaseOrderInfo.getAmount());
            mchAmount += amount;
            purchaseOrderInfo.setAmount(String.valueOf(mchAmount));
            mchIdStatisticsMap.put(mchId + paymentChannel, purchaseOrderInfo);
        } else {
            PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
            purchaseOrderInfo.setMchId(mchId);
            purchaseOrderInfo.setPaymentChannel(paymentChannel);
            purchaseOrderInfo.setAmount(String.valueOf(amount));
            mchIdStatisticsMap.put(mchId + paymentChannel, purchaseOrderInfo);
        }
    }


    private List<PurchaseOrderAmend> getRefundOrderAmends() {

        //查找前一天申请退款还未处理的数据
        Date currDateBefore24 = getLast24HoursDate();
        Date startDate = DateUtils.getCustomDate(new Date(), -1, 0, 0, 0);
        Date endDate = DateUtils.getCustomDate(new Date(), -1, 23, 59, 59);
        List<PurchaseOrderAmend> purchaseOrderAmendList = purchaseOrderAmendRepository.findOvertimeRefundData(OrderTransmissionStatus.Enum.APPLY_FOR_REFUND,
            PaymentType.Enum.FULLREFUND_4, startDate, endDate, currDateBefore24, OrderStatus.Enum.REFUNDING_10, PurchaseOrderAmendStatus.Enum.CREATE);
        List<PurchaseOrder> purchaseOrderList= purchaseOrderAmendList.stream().map(PurchaseOrderAmend::getPurchaseOrder).collect(Collectors.toList());
        StringBuffer orderNos=new StringBuffer();
        purchaseOrderList.forEach(purchaseOrder -> {
            orderNos.append(purchaseOrder.getOrderNo());
        });
        logger.debug("发起退款订单:{}", orderNos);
        return purchaseOrderAmendList;

    }

    private static Date getLast24HoursDate() {
        Calendar calendar = Calendar.getInstance();
         calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
//        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 5);
        return calendar.getTime();
    }
}
