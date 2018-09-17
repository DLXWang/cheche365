package com.cheche365.cheche.ordercenter.service.nationwideOrder;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.OrderCooperationInfoService;
import com.cheche365.cheche.core.service.PurchaseOrderRefundService;
import com.cheche365.cheche.ordercenter.service.newyearpack.NewYearPackManagerService;
import com.cheche365.cheche.ordercenter.service.order.OrderManageService;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.web.service.order.ClientOrderService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

/**
 * Created by wangfei on 2015/11/17.
 */
@Component
abstract class AOrderCooperationStatus {
    protected void handle(OrderCooperationInfo orderCooperationInfo){}
}

@Component
class OrderCreate extends AOrderCooperationStatus {
    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;
    @Override
    protected void handle(OrderCooperationInfo orderCooperationInfo) {
        orderCooperationInfoService.updateOrderCooperationStatus(orderCooperationInfo, OrderCooperationStatus.Enum.CREATED);
    }
}

@Component
class QuoteNoAudit extends AOrderCooperationStatus {
    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;
    @Override
    protected void handle(OrderCooperationInfo orderCooperationInfo) {
        orderCooperationInfoService.updateOrderCooperationStatus(orderCooperationInfo, OrderCooperationStatus.Enum.QUOTE_NO_AUDIT);
    }
}

@Component
class AuditNoPayment extends AOrderCooperationStatus {
    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;
    @Override
    protected void handle(OrderCooperationInfo orderCooperationInfo) {
        orderCooperationInfoService.updateOrderCooperationStatus(orderCooperationInfo, OrderCooperationStatus.Enum.AUDIT_NO_PAYMENT);
    }
}

@Component
class PaymentNoInsurance extends AOrderCooperationStatus {
    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;
    @Override
    protected void handle(OrderCooperationInfo orderCooperationInfo) {
        orderCooperationInfoService.updateOrderCooperationStatus(orderCooperationInfo, OrderCooperationStatus.Enum.PAYMENT_NO_INSURANCE);
    }
}

@Component
class Insurance extends AOrderCooperationStatus {
    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;
    @Override
    protected void handle(OrderCooperationInfo orderCooperationInfo) {
        orderCooperationInfoService.updateOrderCooperationStatus(orderCooperationInfo, OrderCooperationStatus.Enum.INSURANCE);
    }
}

@Component
class Finished extends AOrderCooperationStatus {
    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;
    @Autowired
    private OrderManageService orderManageService;
    @Override
    protected void handle(OrderCooperationInfo orderCooperationInfo) {
        orderCooperationInfoService.updateOrderCooperationStatus(orderCooperationInfo, OrderCooperationStatus.Enum.FINISHED);
        orderManageService.updateStatus(orderCooperationInfo.getPurchaseOrder(), OrderStatus.Enum.FINISHED_5);
        //保存新年礼包信息：发送200元加油卡短信
//        PurchaseOrder purchaseOrder=orderCooperationInfo.getPurchaseOrder();
//        orderManageService.saveNewYearPackInfo(purchaseOrder);
//        //发送百度出单完成订单100元加油卡短信
//        orderManageService.sendBaiduCouponFuelCardMessage(purchaseOrder);
    }
}

@Component
class Abnormity extends AOrderCooperationStatus {
    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;

    @Override
    protected void handle(OrderCooperationInfo orderCooperationInfo) {
        orderCooperationInfoService.updateOrderCooperationStatus(orderCooperationInfo, OrderCooperationStatus.Enum.ABNORMITY);
    }
}

@Component
class Refund extends AOrderCooperationStatus {
    private Logger logger = LoggerFactory.getLogger(Refund.class);

    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;

    @Autowired
    private OrderManageService orderManageService;

    @Autowired
    private OrderCooperationInfoManageService orderCooperationInfoManageService;

    @Autowired
    private NewYearPackManagerService newYearPackManagerService;

    @Autowired
    private ClientOrderService webPurchaseOrderService;

    @Override
    protected void handle(OrderCooperationInfo orderCooperationInfo) {
        PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
        webPurchaseOrderService.refund(purchaseOrder);
        orderManageService.updateStatus(orderCooperationInfo.getPurchaseOrder(), OrderStatus.Enum.REFUNDED_9);
        orderCooperationInfoService.updateOrderCooperationStatus(orderCooperationInfo, OrderCooperationStatus.Enum.REFUND);
        if (StringUtils.isNotBlank(orderCooperationInfo.getRefundObject())) {
            orderCooperationInfoManageService.setRefundInfo(orderCooperationInfo, orderCooperationInfo.getRefundObject());
        }
    }
}

@Component
public class OrderCooperationStatusHandler {
    private Logger logger = LoggerFactory.getLogger(OrderCooperationStatusHandler.class);

    @Autowired
    private OrderCreate orderCreate;

    @Autowired
    private QuoteNoAudit quoteNoAudit;

    @Autowired
    private AuditNoPayment auditNoPayment;

    @Autowired
    private PaymentNoInsurance paymentNoInsurance;

    @Autowired
    private Insurance insurance;

    @Autowired
    private Finished finished;

    @Autowired
    private Abnormity abnormity;

    @Autowired
    private Refund refund;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private OrderProcessHistoryExpandService orderProcessHistoryService;

    @Autowired
    private PurchaseOrderRefundService purchaseOrderRefundService;

    @Autowired
    private OrderManageService orderManageService;

    @Transactional
    public OrderCooperationInfo request(OrderCooperationInfo orderCooperationInfo, OrderCooperationStatus newStatus, String reason) {
        OrderCooperationStatus oriStatus = orderCooperationInfo.getStatus();
        if (null != oriStatus && oriStatus.getId().equals(newStatus.getId())) {
            if (OrderCooperationStatus.Enum.ABNORMITY.getId().equals(newStatus.getId())) {
                if (StringUtils.trimToEmpty(orderCooperationInfo.getReason()).equals(StringUtils.trimToEmpty(reason))) {
                    logger.warn("order status and exception reason has no change, stop to change status.");
                    return orderCooperationInfo;
                }
            } else if (OrderCooperationStatus.Enum.REFUND.getId().equals(newStatus.getId())) {
                if (StringUtils.isNotBlank(orderCooperationInfo.getRefundObject())) {
                    PurchaseOrderRefund purchaseOrderRefund = purchaseOrderRefundService.findByPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
                    if (PurchaseOrderRefund.convertCheckToString(purchaseOrderRefund).equals(orderCooperationInfo.getRefundObject())) {
                        logger.warn("order status and refund object has no change, stop to change status.");
                        return orderCooperationInfo;
                    }
                }
            } else {
                logger.warn("order status has no change, stop to change status.");
                return orderCooperationInfo;
            }
        }
        logger.info("change order status from {} to {}", (null == oriStatus ? "" : oriStatus.getStatus()), newStatus.getStatus());
        AOrderCooperationStatus status;
        if (OrderCooperationStatus.Enum.CREATED.getId().equals(newStatus.getId())) {
            status = orderCreate;
        } else if (OrderCooperationStatus.Enum.QUOTE_NO_AUDIT.getId().equals(newStatus.getId())) {
            status = quoteNoAudit;
        } else if (OrderCooperationStatus.Enum.AUDIT_NO_PAYMENT.getId().equals(newStatus.getId())) {
            status = auditNoPayment;
        } else if (OrderCooperationStatus.Enum.PAYMENT_NO_INSURANCE.getId().equals(newStatus.getId())) {
            status = paymentNoInsurance;
        } else if (OrderCooperationStatus.Enum.INSURANCE.getId().equals(newStatus.getId())) {
            status = insurance;
        } else if (OrderCooperationStatus.Enum.FINISHED.getId().equals(newStatus.getId())) {
            status = finished;
        } else if (OrderCooperationStatus.Enum.ABNORMITY.getId().equals(newStatus.getId())) {
            status = abnormity;
        } else if (OrderCooperationStatus.Enum.REFUND.getId().equals(newStatus.getId())) {
            status = refund;
        } else {
            throw new IllegalArgumentException("illegal newStatus -> " + newStatus.getId());
        }

        preHandle(orderCooperationInfo, newStatus, reason);

        status.handle(orderCooperationInfo);

        afterHandle(orderCooperationInfo, oriStatus);

        return orderCooperationInfo;
    }

    public void preHandle(OrderCooperationInfo orderCooperationInfo, OrderCooperationStatus newStatus, String reason) {
        orderCooperationInfo.setUpdateTime(Calendar.getInstance().getTime());
        orderCooperationInfo.setOperator(internalUserManageService.getCurrentInternalUser());
        if (newStatus.getId().equals(OrderCooperationStatus.Enum.ABNORMITY.getId())) {
            orderCooperationInfo.setReason(reason);
        } else {
            orderCooperationInfo.setReason(null);
        }
        if(orderCooperationInfo.getStatus().getId().equals(OrderCooperationStatus.Enum.FINISHED.getId())) {
            orderManageService.updateStatus(orderCooperationInfo.getPurchaseOrder(), OrderStatus.Enum.PAID_3);
        }
    }

    public void afterHandle(OrderCooperationInfo orderCooperationInfo, OrderCooperationStatus oriStatus) {
        logger.info("save log for change order status.");
        InternalUser operator = internalUserManageService.getCurrentInternalUser();
        orderProcessHistoryService.saveChangeStatusHistory(operator, orderCooperationInfo.getPurchaseOrder(),
            oriStatus, orderCooperationInfo.getStatus());
    }
}
