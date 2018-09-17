package com.cheche365.cheche.ordercenter.service.quote;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.manage.common.model.PurchaseOrderExtend;
import com.cheche365.cheche.manage.common.service.gift.OrderCenterGiftService;
import com.cheche365.cheche.ordercenter.service.order.OrderTransmissionStatusHandler;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.web.service.payment.MultiPaymentService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cheche365.cheche.core.exception.BusinessException.Code.OPERATION_NOT_ALLOWED;

/**
 * Created by xu.yelong on 2016/9/19.
 */
@Service
public class QuoteAmendService {

    @Autowired
    private DefaultQuoteService defaultQuoteService;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private PurchaseOrderAmendRepository purchaseOrderAmendRepository;

    @Autowired
    private MultiPaymentService multiPaymentService;

    @Autowired
    private PurchaseOrderAmendService purchaseOrderAmendService;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private OrderTransmissionStatusHandler orderTransmissionStatusHandler;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    List<UnifiedRefundHandler> refundPayChannelHandel;

    @Autowired
    private OrderCenterGiftService orderCenterGiftService;

    @Autowired
    private OrderProcessHistoryService orderProcessHistoryService;
    private Logger logger = LoggerFactory.getLogger(QuoteAmendService.class);

    /**
     * 修改订单
     * 出单中心订单在增补或车车承担的状态下需更改状态；
     * 目前下单支持单gift，后期支持多个
     *
     * @param originalPurchaseOrder 原来的订单
     * @param purchaseOrderExtend   表单数据
     */
    @Transactional
    public void modifyOrder(PurchaseOrder originalPurchaseOrder, PurchaseOrderExtend purchaseOrderExtend) {
        // 校验
        validateAmend(originalPurchaseOrder, purchaseOrderExtend);

        logger.debug("do modify order ,order no -->{}", originalPurchaseOrder.getOrderNo());
        QuoteRecord newQuoteRecord = quoteRecordService.getById(purchaseOrderExtend.getObjId());

        // # 取消旧的amend
        purchaseOrderAmendService.cancelPrevAmend(originalPurchaseOrder);

        // # 保存礼物
        generateGift(originalPurchaseOrder, purchaseOrderExtend, newQuoteRecord);

        // # 生成新的amend
        PurchaseOrderAmend purchaseOrderAmend = generateAmend(originalPurchaseOrder, purchaseOrderExtend, newQuoteRecord);

        // # 初始化增补
        OrderOperationInfo orderOperationInfo = purchaseOrderAmend.getOrderOperationInfo();
        multiPaymentService.initMultiPayment(purchaseOrderAmend);
        logger.info("finished call web service, orderNo:{}, status:{}, currentStatus:{}", purchaseOrderAmend.getPurchaseOrder().getOrderNo(),
                purchaseOrderAmend.getPurchaseOrder().getStatus().getDescription(), orderOperationInfo.getCurrentStatus().getDescription());

        // # 修改出单中心状态
        OrderTransmissionStatus newStatus = purchaseOrderExtend.getNewTransmissionStatus();
        if (newStatus != null && !orderOperationInfo.getCurrentStatus().getId().equals(newStatus.getId())) {
            orderTransmissionStatusHandler.request(orderOperationInfo, newStatus);
        }

        // # 保存备注历史纪录
        saveCommentToOrderProcessHistory(originalPurchaseOrder, purchaseOrderExtend);

        // # 部分退款
        if (purchaseOrderAmend.getPaymentType().getId().equals(PaymentType.Enum.PARTIALREFUND_3.getId())) {
            partialRefund(purchaseOrderAmend);
        }
    }

    private PurchaseOrderAmend generateAmend(PurchaseOrder originalPurchaseOrder, PurchaseOrderExtend purchaseOrderExtend, QuoteRecord newQuoteRecord) {
        OrderOperationInfo orderOperationInfo = orderOperationInfoService.getByPurchaseOrder(originalPurchaseOrder);
        QuoteRecord originalQuoteRecord = getOriginalQuoteRecord(originalPurchaseOrder);
        PurchaseOrderAmendStatus amendStatus = PurchaseOrderAmendStatus.Enum.CREATE;
        //原订单的实付金额
        Double paidAmount = DoubleUtils.displayDoubleValue(purchaseOrderAmendService.getPaidAmountByOrderId(originalPurchaseOrder.getId()));
        //增补后的实付金额
        Double payableAmount = DoubleUtils.displayDoubleValue(purchaseOrderExtend.getPaidAmount());
        logger.debug("modify order ,original paidAmount -->{},current paidAmount -->{}", paidAmount, payableAmount);

        PaymentType paymentType;
        if (purchaseOrderExtend.getIsCheChePay() == 1 && (originalPurchaseOrder.getStatus().equals(OrderStatus.Enum.PAID_3) || orderOperationInfo.getCurrentStatus().getId().equals(OrderTransmissionStatus.Enum.ADDITION_PAID.getId()))) {
            paymentType = PaymentType.Enum.CHECHEPAY_6;
            amendStatus = PurchaseOrderAmendStatus.Enum.FINISHED;
            purchaseOrderExtend.setNewTransmissionStatus(OrderTransmissionStatus.Enum.UNCONFIRMED);
        } else if (paidAmount > payableAmount) {
            paymentType = PaymentType.Enum.PARTIALREFUND_3;
        } else {
            paymentType = PaymentType.Enum.ADDITIONALPAYMENT_2;
            purchaseOrderExtend.setNewTransmissionStatus(OrderTransmissionStatus.Enum.ADDITION_PAID);
            if (originalPurchaseOrder.getStatus().equals(OrderStatus.Enum.PENDING_PAYMENT_1) && orderOperationInfo.getCurrentStatus().getId().equals(OrderTransmissionStatus.Enum.UNPAID.getId())) {
                paymentType = PaymentType.Enum.INITIALPAYMENT_1;
                purchaseOrderExtend.setNewTransmissionStatus(OrderTransmissionStatus.Enum.UNPAID);
            } else if (DoubleUtils.equalsDouble(paidAmount, payableAmount)) {
                purchaseOrderExtend.setNewTransmissionStatus(OrderTransmissionStatus.Enum.UNCONFIRMED);
            }
            orderOperationInfo.setOperator(orderCenterInternalUserManageService.getCurrentInternalUser());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("modify order ,order no -->{},payment type -->{},gift id -->{}", originalPurchaseOrder.getOrderNo(), paymentType.getName(), originalPurchaseOrder.getGiftId());
        }
        return purchaseOrderAmendService.addAmend(originalQuoteRecord, newQuoteRecord, originalPurchaseOrder, paymentType, amendStatus);
    }

    private void generateGift(PurchaseOrder originalPurchaseOrder, PurchaseOrderExtend purchaseOrder, QuoteRecord newQuoteRecord) {
        //生成直接优惠产生的礼物
        setGift(originalPurchaseOrder, purchaseOrder, newQuoteRecord);
        //设置额外赠送礼品
        setResendGift(originalPurchaseOrder, purchaseOrder, newQuoteRecord);
    }


    private void saveCommentToOrderProcessHistory(PurchaseOrder originalPurchaseOrder, PurchaseOrderExtend purchaseOrderExtend) {
        if (!StringUtils.isBlank(purchaseOrderExtend.getComment())) {
            orderProcessHistoryService.saveChangeStatusHistory(orderCenterInternalUserManageService.getCurrentInternalUser(),
                    originalPurchaseOrder, null, null, purchaseOrderExtend.getComment());
        }
    }

    public List<PurchaseOrderAmend> getPrevAmend(PurchaseOrder purchaseOrder) {
        return purchaseOrderAmendRepository.findByPurchaseOrderAndPurchaseOrderAmendStatus(purchaseOrder, PurchaseOrderAmendStatus.Enum.CREATE);
    }

    /**
     * 判断上一条增补记录是否完成，如果没有完成，则取消掉，并将originalQuoteRecord保存至当前amend的originalQuoteRecord字段
     *
     * @param originalPurchaseOrder
     * @return
     */
    private QuoteRecord getOriginalQuoteRecord(PurchaseOrder originalPurchaseOrder) {
        QuoteRecord originalQuoteRecord = quoteRecordService.getById(originalPurchaseOrder.getObjId());
        PurchaseOrderAmend prevAmend = purchaseOrderAmendRepository.findByNewQuoteRecord(originalQuoteRecord);
        if (prevAmend != null && !prevAmend.getPurchaseOrderAmendStatus().getId().equals(PurchaseOrderAmendStatus.Enum.FINISHED.getId())) {
            originalQuoteRecord = prevAmend.getOriginalQuoteRecord();
        }
        return originalQuoteRecord;
    }

    private void partialRefund(PurchaseOrderAmend purchaseOrderAmend) {
        List<Payment> payments = paymentRepository.findByPurchaseOrderAmend(purchaseOrderAmend);
        if (CollectionUtils.isEmpty(payments)) {
            return;
        }
        for (Payment payment : payments) {
            logger.debug("do partial refund,amend id:-->{},payment: -->{},order no: -->{}", purchaseOrderAmend.getId(), payment.getId(), purchaseOrderAmend.getPurchaseOrder().getOrderNo());
        }
        for (UnifiedRefundHandler r : refundPayChannelHandel) {
            logger.debug("部分退款支持渠道:{}", r);
            if (r.support(payments.get(0))) {
                logger.debug("增补部分退款，退款channel->{},api->{}", payments.get(0).getChannel().getDescription(), r.getClass().getName());
                Map<Long, Boolean> resultMap = r.refund(payments);
                if (resultMap.values().contains(false)) {
                    throw new BusinessException(OPERATION_NOT_ALLOWED, "部分退款失败!");
                }
            }
        }
    }

    private void setGift(PurchaseOrder originalPurchaseOrder, PurchaseOrderExtend purchaseOrder, QuoteRecord newQuoteRecord) {
        Gift telGift = orderCenterGiftService.createGift(purchaseOrder, newQuoteRecord.getId());
        //目前只生效一个gift，优先电销gift，后期支持一个订单多个gift
        //数据结构修改了，giftId为Object类型，与接口协调的是传入的List类型
        //返回giftAmount也没啥用，还有可能导致空指针异常，干掉了
        if (telGift != null) {
            originalPurchaseOrder.setGiftId(Arrays.asList(telGift.getId()));
        } else if (purchaseOrder.getGiftId() != null) {
            originalPurchaseOrder.setGiftId(purchaseOrder.getGiftId());
        }
    }

    private void setResendGift(PurchaseOrder originalPurchaseOrder, PurchaseOrderExtend purchaseOrder, QuoteRecord newQuoteRecord) {
        List<Gift> giftList = orderCenterGiftService.createResendGift(purchaseOrder, newQuoteRecord.getId());
        if (CollectionUtils.isNotEmpty(giftList)) {
            /*
              实体类的结构发生变化，giftId修改为Obejct类型，额外赠送礼品存放在giftId中；
              因为PurchaseOrder已设置过电销的礼品信息，所以先要把电销礼品Id取出，放到giftList这个集合中，再赋值给giftI属性
              */
            List<Long> list = purchaseOrder.getGiftId() != null ? new ArrayList<Long>((List<Long>) originalPurchaseOrder.getGiftId()) : new ArrayList<Long>();
            list.addAll(giftList.stream().map(Gift::getId).collect(Collectors.toList()));
            originalPurchaseOrder.setGiftId(list);
        }
    }

    private void validateAmend(PurchaseOrder originalPurchaseOrder, PurchaseOrderExtend purchaseOrder) {
        if (!OrderStatus.Enum.allowModiftStatus().contains(originalPurchaseOrder.getStatus().getId())) {
            logger.debug("订单当前状态不允许增补操作,订单号：->{},状态:->{}", originalPurchaseOrder.getOrderNo(), originalPurchaseOrder.getStatus().getDescription());
            throw new BusinessException(OPERATION_NOT_ALLOWED, "订单当前状态不允许增补操作,状态:->{" + originalPurchaseOrder.getStatus().getDescription() + "}");
        }
        QuoteRecord quoteRecord = quoteRecordService.getById(purchaseOrder.getObjId());
        if (purchaseOrderAmendRepository.findByNewQuoteRecord(quoteRecord) != null) {
            logger.debug("当前报价已经有增补记录,订单号：->{},状态:->{},报价ID->{}", originalPurchaseOrder.getOrderNo(), originalPurchaseOrder.getStatus().getDescription(), quoteRecord.getId());
            throw new BusinessException(OPERATION_NOT_ALLOWED, "当前报价已经有增补记录!");
        }
    }

}
