package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.wallet.service.WalletTradeService;
import com.cheche365.cheche.web.service.order.ClientOrderService;
import com.cheche365.cheche.web.service.payment.MultiPaymentService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangfei on 2015/12/18.
 */
class AOrderTransmissionStatus {

    protected void handle(OrderOperationInfo orderOperationInfo) {
    }
    protected void handleExtra(OrderOperationInfo orderOperationInfo,Map extra){
    }
}


@Component
class Unpay extends AOrderTransmissionStatus {

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Autowired
    private OrderManageService orderManageService;

    @Transactional
    public void handle(OrderOperationInfo orderOperationInfo) {
        orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo, OrderTransmissionStatus.Enum.UNPAID);
        orderManageService.updateStatus(orderOperationInfo.getPurchaseOrder(), OrderStatus.Enum.PENDING_PAYMENT_1);
    }
}

@Component
class Unconfirmed extends AOrderTransmissionStatus {
    private Logger logger = LoggerFactory.getLogger(Unconfirmed.class);

    @Autowired
    private PurchaseOrderAmendService purchaseOrderAmendService;
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;
    @Autowired
    private MultiPaymentService multiPaymentService;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Transactional
    public void handleExtra(OrderOperationInfo orderOperationInfo,Map extra) {
        if (OrderTransmissionStatus.Enum.UNPAID.getId().equals(orderOperationInfo.getCurrentStatus().getId())) {
            logger.debug("orderCenter payOffline operationInfo id is => {}", orderOperationInfo.getId());
            PaymentChannel paymentChannel;
            InternalUser currentUser = internalUserManageService.getCurrentInternalUser();
            if(extra.get("clientType").toString().equals("alipay")){
                paymentChannel = PaymentChannel.Enum.ALIPAY_OFFLINE_PAY_50;
            }else{
                paymentChannel = PaymentChannel.Enum.ACCOUNT_OFFLINE_PAY_51;
            }
            orderOperationInfoService.offlinePay(orderOperationInfo, paymentChannel, extra.get("thirdpartyPaymentNo").toString(),currentUser);
        }
    }

    @Transactional
    public void handle(OrderOperationInfo orderOperationInfo) {
        //确认出单-未确认、申请取消-未确认     更新订单归属人为指定客服
        if (OrderTransmissionStatus.Enum.CONFIRM_TO_ORDER.getId().equals(orderOperationInfo.getCurrentStatus().getId())) {
            logger.debug("order status from {} to {}, update order owner from {} to assigner: {}", orderOperationInfo.getCurrentStatus().getStatus(),
                OrderTransmissionStatus.Enum.UNCONFIRMED.getStatus(), orderOperationInfo.getOwner().getName(), orderOperationInfo.getAssigner().getName());
            orderOperationInfo.setOwner(orderOperationInfo.getAssigner());
        }
        orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo, OrderTransmissionStatus.Enum.UNCONFIRMED);
        //撤销退款申请
        if (OrderTransmissionStatus.Enum.APPLY_FOR_REFUND.getId().equals(orderOperationInfo.getOriginalStatus().getId())) {
            //调接口取消payment
            PurchaseOrderAmend purchaseOrderAmend = purchaseOrderAmendService.findByOrderOperationInfo(orderOperationInfo);
            if (purchaseOrderAmend != null)
                multiPaymentService.cancelFullRefundAmend(purchaseOrderAmend.getPurchaseOrder());

            //更新amend状态为取消
            purchaseOrderAmendService.cancelPrevAmend(orderOperationInfo.getPurchaseOrder());
            //更新电销状态退款取消
            InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
            telMarketingCenterService.refreshStatus(orderOperationInfo.getPurchaseOrder(), TelMarketingCenterStatus.Enum.REFUND_CANCEL, internalUser);
        }
    }
}

@Component
class ConfirmToOrder extends AOrderTransmissionStatus {

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Transactional
    public void handle(OrderOperationInfo orderOperationInfo) {
        orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo, OrderTransmissionStatus.Enum.CONFIRM_TO_ORDER);
    }
}

@Component
class PaidAndFinishOrder extends AOrderTransmissionStatus {
    private Logger logger = LoggerFactory.getLogger(PaidAndFinishOrder.class);


    @Autowired
    private OrderManageService orderManageService;
    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Transactional
    public void handle(OrderOperationInfo orderOperationInfo) {
        if (OrderTransmissionStatus.Enum.ORDER_INPUTED.getId().equals(orderOperationInfo.getCurrentStatus().getId())) {
            //录单完成-已付款，出单完成，更新原始订单状态
            if (logger.isDebugEnabled()) {
                logger.debug("update purchase_order status to: {}", OrderStatus.Enum.PAID_3.getStatus());
            }
            orderManageService.updateStatus(orderOperationInfo.getPurchaseOrder(), OrderStatus.Enum.PAID_3);
        }
        orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo, OrderTransmissionStatus.Enum.PAID_AND_FINISH_ORDER);
    }
}

@Component
class OrderInputed extends AOrderTransmissionStatus {

    private Logger logger = LoggerFactory.getLogger(OrderInputed.class);


    @Autowired
    private OrderManageService orderManageService;
    @Autowired
    private OrderOperationInfoService orderOperationInfoService;
    @Transactional
    public void handle(OrderOperationInfo orderOperationInfo) {
        orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo, OrderTransmissionStatus.Enum.ORDER_INPUTED);
        orderManageService.updateStatus(orderOperationInfo.getPurchaseOrder(), OrderStatus.Enum.FINISHED_5);
    }
}

@Component
class ApplyForRefund extends AOrderTransmissionStatus {
    private Logger logger = LoggerFactory.getLogger(ApplyForRefund.class);
    @Autowired
    private PurchaseOrderAmendService purchaseOrderAmendService;
    @Autowired
    private MultiPaymentService multiPaymentService;
    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Transactional
    public void handle(OrderOperationInfo orderOperationInfo) {
        //取消上次amend
        purchaseOrderAmendService.cancelPrevAmend(orderOperationInfo.getPurchaseOrder());
        //创建新amend
        PurchaseOrderAmend purchaseOrderAmend = purchaseOrderAmendService.addFullRefundAmend(orderOperationInfo);
        //更新出单中心状态
        orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo, OrderTransmissionStatus.Enum.APPLY_FOR_REFUND);
        //调用接口创建payment
        multiPaymentService.initMultiPayment(purchaseOrderAmend);
    }
}

@Component
class Refunded extends AOrderTransmissionStatus {
    private Logger logger = LoggerFactory.getLogger(Refunded.class);

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Transactional
    public void handle(OrderOperationInfo orderOperationInfo) {
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
//        webPurchaseOrderService.refund(purchaseOrder);
        orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo, OrderTransmissionStatus.Enum.REFUNDED);
//        orderManageService.updateStatus(purchaseOrder, OrderStatus.Enum.REFUNDED_9);
//        newYearPackManagerService.releaseNewYearPurchaseOrder(purchaseOrder);
    }
}

@Component
class Canceled extends AOrderTransmissionStatus {
    @Autowired
    private OrderManageService orderManageService;
    @Autowired
    private ClientOrderService webPurchaseOrderService;

    @Transactional
    public void handle(OrderOperationInfo orderOperationInfo) {
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        webPurchaseOrderService.cancel(purchaseOrder, false);
        orderManageService.updateStatus(purchaseOrder, OrderStatus.Enum.CANCELED_6);
    }
}

@Component
class RefundFailed extends AOrderTransmissionStatus {
    private Logger logger = LoggerFactory.getLogger(RefundFailed.class);

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Transactional
    public void handle(OrderOperationInfo orderOperationInfo) {
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        //telMarketingCenterService.refreshStatus(orderOperationInfo.getPurchaseOrder(), TelMarketingCenterStatus.Enum.REFUND_CANCEL, internalUser);
        orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo, OrderTransmissionStatus.Enum.REFUND_FAILED);
        // purchaseOrderAmendService.cancelPrevAmend(orderOperationInfo.getPurchaseOrder());
    }
}

@Component
class AdditionPaid extends AOrderTransmissionStatus {
    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Transactional
    public void handle(OrderOperationInfo orderOperationInfo) {
        orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo, OrderTransmissionStatus.Enum.ADDITION_PAID);
    }
}

@Component
public class OrderTransmissionStatusHandler {
    private Logger logger = LoggerFactory.getLogger(OrderTransmissionStatusHandler.class);

    @Autowired
    private Unpay unpay;

    @Autowired
    private Unconfirmed unconfirmed;

    @Autowired
    private ConfirmToOrder confirmToOrder;

    @Autowired
    private PaidAndFinishOrder paidAndFinishOrder;

    @Autowired
    private OrderInputed orderInputed;

    @Autowired
    private ApplyForRefund applyForRefund;

    @Autowired
    private Refunded refunded;

    @Autowired
    private Canceled canceled;

    @Autowired
    private RefundFailed refundFailed;

    @Autowired
    private AdditionPaid addition_paid;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private IInternalUserService internalUserService;

    @Autowired
    private OrderProcessHistoryService orderProcessHistoryService;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private WalletTradeService walletTradeService;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    private Map strategyMap;

    private void init(){
        strategyMap = new HashMap<Long,AOrderTransmissionStatus>(){{
            put(OrderTransmissionStatus.Enum.UNCONFIRMED.getId(), unconfirmed);
            put(OrderTransmissionStatus.Enum.CONFIRM_TO_ORDER.getId(), confirmToOrder);
            put(OrderTransmissionStatus.Enum.PAID_AND_FINISH_ORDER.getId(), paidAndFinishOrder);
            put(OrderTransmissionStatus.Enum.ORDER_INPUTED.getId(), orderInputed);
            put(OrderTransmissionStatus.Enum.APPLY_FOR_REFUND.getId(), applyForRefund);
            put(OrderTransmissionStatus.Enum.REFUNDED.getId(), refunded);
            put(OrderTransmissionStatus.Enum.UNPAID.getId(), unpay);
            put(OrderTransmissionStatus.Enum.CANCELED.getId(), canceled);
            put(OrderTransmissionStatus.Enum.REFUND_FAILED.getId(), refundFailed);
            put(OrderTransmissionStatus.Enum.ADDITION_PAID.getId(), addition_paid);
        }};
    }

    public AOrderTransmissionStatus creator(Long id){
        if(strategyMap == null || strategyMap.size() == 0){
            init();
        }
        if(!strategyMap.containsKey(id)){
            throw new IllegalArgumentException("illegal newStatus -> " + id);
        }
        return (AOrderTransmissionStatus)strategyMap.get(id);
    }
    public OrderOperationInfo request(OrderOperationInfo orderOperationInfo, OrderTransmissionStatus newStatus) {
        return request(orderOperationInfo,newStatus,null,null);
    }

    public OrderOperationInfo request(OrderOperationInfo orderOperationInfo, OrderTransmissionStatus newStatus, Long owner) {
        return request(orderOperationInfo,newStatus,null,owner);
    }
    public OrderOperationInfo request(OrderOperationInfo orderOperationInfo, OrderTransmissionStatus newStatus, String confirmNo, Long owner) {
        return request(orderOperationInfo, newStatus, confirmNo, owner,null);
    }
    public OrderOperationInfo request(OrderOperationInfo orderOperationInfo, OrderTransmissionStatus newStatus, String confirmNo, Long owner,Map extra) {
        OrderTransmissionStatus oriStatus = orderOperationInfo.getCurrentStatus();
        logger.info("orderNo {} change order status from {} to {}",
            orderOperationInfo.getPurchaseOrder().getOrderNo(),
            (null == oriStatus ? "" : oriStatus.getStatus()), newStatus.getStatus());
        try{
            AOrderTransmissionStatus status = creator(newStatus.getId());
            preHandle(orderOperationInfo, newStatus, confirmNo, owner);
            if(extra != null){
                status.handleExtra(orderOperationInfo,extra);
            }else{
                status.handle(orderOperationInfo);
            }
            afterHandle(orderOperationInfo, oriStatus);
            return orderOperationInfo;
        }catch(IllegalArgumentException exception){
            logger.error("no valid statusid ",exception);
        }
        return orderOperationInfo;
    }

    public void preHandle(OrderOperationInfo orderOperationInfo, OrderTransmissionStatus newStatus, String confirmNo, Long owner) {
        InternalUser operator;
        try {
            operator = internalUserManageService.getCurrentInternalUser();
        } catch (NullPointerException e) {
            operator = internalUserRepository.findFirstByName("system");
        }
        orderOperationInfo.setOperator(operator);
        if (null != owner) {
            orderOperationInfo.setOwner(internalUserService.getInternalUserById(owner));
        }
        if (StringUtils.isNotBlank(confirmNo)) {
            orderOperationInfo.setConfirmNo(confirmNo);
        }
    }

    public void afterHandle(OrderOperationInfo orderOperationInfo, OrderTransmissionStatus oriStatus) {
        afterHandle(orderOperationInfo,oriStatus,null);
    }


    @Transactional
    public void afterHandle(OrderOperationInfo orderOperationInfo, OrderTransmissionStatus oriStatus,String comment) {
        InternalUser operator = internalUserManageService.getCurrentInternalUserOrSystem();
        if (oriStatus.getId().equals(orderOperationInfo.getCurrentStatus().getId())) {
            return;
        }
        orderProcessHistoryService.saveChangeStatusHistory(operator, orderOperationInfo.getPurchaseOrder(),
            oriStatus, orderOperationInfo.getCurrentStatus(),comment);

        if (OrderStatus.Enum.FINISHED_5.equals(orderOperationInfo.getPurchaseOrder().getStatus()) &&
            Channel.rebateToWallets().contains(orderOperationInfo.getPurchaseOrder().getSourceChannel())) {
            walletTradeService.createAgentWalletTrade(quoteRecordRepository.findOne(orderOperationInfo.getPurchaseOrder().getObjId()), orderOperationInfo.getPurchaseOrder());
        }
    }
}
