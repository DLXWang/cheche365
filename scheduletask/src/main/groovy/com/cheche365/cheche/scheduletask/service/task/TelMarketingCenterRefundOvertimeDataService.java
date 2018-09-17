package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository;
import com.cheche365.cheche.core.service.OrderProcessHistoryService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import com.cheche365.cheche.core.service.UnifiedRefundHandler;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TelMarketingCenterRefundOvertimeDataService {

    Logger logger = LoggerFactory.getLogger(TelMarketingCenterRefundOvertimeDataService.class);
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PurchaseOrderAmendRepository purchaseOrderAmendRepository;
    @Autowired
    private InternalUserRepository internalUserRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;
    @Autowired
    List<UnifiedRefundHandler> refundPayChannelHandel;
    @Autowired
    private OrderProcessHistoryService orderProcessHistoryService;


    /**
     * 处理申请退款48小时候还没处理的数据
     */
    public void processOvertimeRefundOrderData() {
        String refundAmendIdStr = stringRedisTemplate.opsForValue().get(TaskConstants.OVERTIME_REFUND_AMEND_ID_CACHE);
        logger.debug("schedule task starting--> process overtime refund purchase order data,start from id --> [{}]", refundAmendIdStr);
        if (StringUtils.isEmpty(refundAmendIdStr)) {
            return;
        }
        List<Long> refundAmendIds = CacheUtil.doListJacksonDeserialize(refundAmendIdStr, Long.class);
        if (CollectionUtils.isEmpty(refundAmendIds)) {
            return;
        }
        List<PurchaseOrderAmend> purchaseOrderAmendList = purchaseOrderAmendRepository.findByIds(refundAmendIds);
        updatePurchanseOrderAmendStatus(purchaseOrderAmendList);
        stringRedisTemplate.delete(TaskConstants.OVERTIME_REFUND_AMEND_ID_CACHE);
        refundAmendIdStr = stringRedisTemplate.opsForValue().get(TaskConstants.OVERTIME_REFUND_AMEND_ID_CACHE);
        logger.debug("schedule task starting finish--> process overtime refund purchase order data,start from id --> [{}]", refundAmendIdStr);
    }

    /**
     * 更新超时的订单的状态为退款成功
     *
     * @param purchaseOrderAmendList
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePurchanseOrderAmendStatus(List<PurchaseOrderAmend> purchaseOrderAmendList) {
        InternalUser operator = internalUserRepository.findFirstByName("system");

        for (PurchaseOrderAmend purchaseOrderAmend : purchaseOrderAmendList) {
            OrderOperationInfo orderOperationInfo = purchaseOrderAmend.getOrderOperationInfo();
            if (!orderOperationInfo.getCurrentStatus().getId().equals(OrderTransmissionStatus.Enum.APPLY_FOR_REFUND.getId())) {
                continue;
            }
            List<Payment> payments = paymentRepository.findByPurchaseOrderAmend(purchaseOrderAmend);
            if(CollectionUtils.isEmpty(payments)){
                return;
            }
            try {
                //调接口退款
                Boolean sended = this.refund(payments);
                logger.info("退款订单超时自动发起退款,amendId-->[{}],退款渠道-->[{}],渠道受理结果-->[{}],paymentId-->[{}],purchaseOrderNo-->[{}]", purchaseOrderAmend.getId(), payments.get(0).getChannel().getDescription(), sended, payments.get(0).getId(), payments.get(0).getPurchaseOrder().getOrderNo());
                if (sended) {//受理成功,等待监听器获取退款结果
                    //更新电销中心状态为退款确认
                    this.refreshStatus(orderOperationInfo.getPurchaseOrder(), TelMarketingCenterStatus.Enum.REFUND_CONFIRM, null);
                    //给当前订单加锁,不能再进行取消退款操作
                    stringRedisTemplate.opsForSet().add("syn.refunding.order.id", purchaseOrderAmend.getOrderOperationInfo().getPurchaseOrder().getOrderNo());
                    logger.debug("待退款订单已锁定：{}",purchaseOrderAmend.getOrderOperationInfo().getPurchaseOrder().getOrderNo());
                } else {
                    PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
                    //修改电销中心状态
                    this.refreshStatus(purchaseOrder, TelMarketingCenterStatus.Enum.REFUND_CONFIRM, null);
                    //更新出单中心状态
                    this.updateOrderTransmissionStatus(payments.get(0), OrderTransmissionStatus.Enum.REFUND_FAILED, operator);
                    logger.debug("待退款订单已失败：{}",purchaseOrderAmend.getOrderOperationInfo().getPurchaseOrder().getOrderNo());
                }
            } catch (Exception e) {
                logger.error("退款订单超时自动发起退款异常,amendId-->[{}],退款渠道-->[{}],paymentId-->[{}],purchaseOrderNo-->[{}]", purchaseOrderAmend.getId(), payments.get(0).getChannel().getDescription(), payments.get(0).getId(), payments.get(0).getPurchaseOrder().getOrderNo(), e);
                PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
                //修改电销中心状态
                this.refreshStatus(purchaseOrder, TelMarketingCenterStatus.Enum.REFUND_CONFIRM, null);
                //更新出单中心状态
                this.updateOrderTransmissionStatus(payments.get(0), OrderTransmissionStatus.Enum.REFUND_FAILED, operator);
            }
        }

    }

    private Boolean refund(List<Payment> payments) {
        for (UnifiedRefundHandler r : refundPayChannelHandel) {
            if (r.support(payments.get(0))) {
                logger.debug("全额退款，退款channel->{},api->{},updateTime->{}",payments.get(0).getChannel().getDescription(),r.getClass().getName(),payments.get(0).getUpdateTime());
                Map<Long, Boolean> resultMap = r.refund(payments);
                logger.debug("全额退款,渠道返回:{}",resultMap);
                if (resultMap.containsValue(false)) {
                    return false;
                }
            }
        }
        return true;

    }


    private void updateOrderTransmissionStatus(Payment payment, OrderTransmissionStatus newStatus, InternalUser operator) {
        PurchaseOrderAmend purchaseOrderAmend = payment.getPurchaseOrderAmend();
        OrderOperationInfo orderOperationInfo = purchaseOrderAmend.getOrderOperationInfo();

        //更新amend状态
        purchaseOrderAmend.setPurchaseOrderAmendStatus(PurchaseOrderAmendStatus.Enum.FINISHED);
        purchaseOrderAmend.setUpdateTime(new Date());
        purchaseOrderAmendRepository.save(purchaseOrderAmend);

        //更新出单中心状态
        orderOperationInfo.setOriginalStatus(orderOperationInfo.getCurrentStatus());
        orderOperationInfo.setCurrentStatus(newStatus);
        orderOperationInfo.setUpdateTime(new Date());
        orderOperationInfoRepository.save(orderOperationInfo);

        //记录订单历史状态
        orderProcessHistoryService.saveChangeStatusHistory(operator, orderOperationInfo.getPurchaseOrder(),
            OrderTransmissionStatus.Enum.APPLY_FOR_REFUND, orderOperationInfo.getCurrentStatus());
    }


    /**
     * @param purchaseOrder
     * @param newStatus
     * @param operator      如果不设置则传null
     */
    public void refreshStatus(PurchaseOrder purchaseOrder, TelMarketingCenterStatus newStatus, InternalUser operator) {
        //更新电销表状态,并清空过期时间
        TelMarketingCenter telMarketingCenter = telMarketingCenterService.findByUser(purchaseOrder.getApplicant());
        if (telMarketingCenter != null) {
            if (!telMarketingCenter.getSource().getId().equals(TelMarketingCenterSource.Enum.ORDERS_REFUND.getId())) {
                return;
            }
            telMarketingCenter.setStatus(newStatus);
            telMarketingCenter.setProcessedNumber(telMarketingCenter.getProcessedNumber() + 1L);
            telMarketingCenter.setExpireTime(null);
            telMarketingCenter.setTriggerTime(null);
            telMarketingCenter.setUpdateTime(new Date());
            telMarketingCenter.setDisplay(false);
            if (operator != null)
                telMarketingCenter.setOperator(operator);

            telMarketingCenterService.save(telMarketingCenter);
        }

    }

}
