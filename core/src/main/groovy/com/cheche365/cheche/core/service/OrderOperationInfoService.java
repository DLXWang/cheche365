package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.CompulsoryInsurance;
import com.cheche365.cheche.core.model.Insurance;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.OrderOperationInfo;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.OrderTransmissionStatus;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.model.PaymentType;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository;
import com.cheche365.cheche.core.repository.InsuranceRepository;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000;

/**
 * Created by wangfei on 2015/7/7.
 */
@Service
@Transactional
@Primary
public class OrderOperationInfoService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;

    @Autowired
    private IInternalUserService internalUserService;

    @Autowired
    private OrderProcessHistoryService orderProcessHistoryService;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired(required = false)
    @Qualifier("walletTradeService")
    private IWalletTradeService walletTradeService;


    public OrderOperationInfo getById(Long orderOperationId) {
        return orderOperationInfoRepository.findOne(orderOperationId);
    }

    public OrderOperationInfo getByPurchaseOrderId(Long purchaseOrderId) {
        return orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrderRepository.findOne(purchaseOrderId));
    }

    public OrderOperationInfo getByPurchaseOrder(PurchaseOrder purchaseOrder) {
        return orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
    }

    public void saveOrderCenterInfo(PurchaseOrder purchaseOrder) {
        if (logger.isDebugEnabled()) {
            logger.debug("generate order center info by purchase order, orderNo:{}", purchaseOrder.getOrderNo());
        }
        if (orderCooperationInfoService.checkOrderMode(purchaseOrder)) {
            orderCooperationInfoService.saveOrderCooperationInfo(purchaseOrder);
        } else {
            saveOrderOperationInfo(purchaseOrder);
        }
    }

    public OrderOperationInfo saveOrderOperationInfo(PurchaseOrder purchaseOrder) {
        if (logger.isDebugEnabled()) {
            logger.debug("generate order operation info by purchase order, orderNo:{}", purchaseOrder.getOrderNo());
        }
        if (purchaseOrder == null) {
            throw new RuntimeException("save order operation info, purchase order can not be null");
        }
        OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (orderOperationInfo == null && StringUtils.isNotBlank(purchaseOrder.getComment())) {
            orderProcessHistoryService.saveChangeStatusHistory(InternalUser.ENUM.SYSTEM, purchaseOrder,
                null, null, purchaseOrder.getComment());
        }
        orderOperationInfo = orderOperationInfoRepository.save(this.createOperationInfo(purchaseOrder, orderOperationInfo));
        return orderOperationInfo;
    }


    public OrderOperationInfo createOperationInfo(PurchaseOrder purchaseOrder, OrderOperationInfo orderOperationInfo) {
        if (orderOperationInfo == null) {
            orderOperationInfo = new OrderOperationInfo();
            orderOperationInfo.setCreateTime(purchaseOrder.getCreateTime());
            Insurance insurance = insuranceRepository.findByQuoteRecordId(purchaseOrder.getObjId());
            Long companyId;
            if (insurance != null) {
                companyId = insurance.getInsuranceCompany().getId();
            } else {
                CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findByQuoteRecordId(purchaseOrder.getObjId());
                companyId = compulsoryInsurance.getInsuranceCompany().getId();
            }
            //安心保险指定跟进人为录单员
            if (companyId.equals(ANSWERN_65000.getId())) {
                orderOperationInfo.setAssigner(internalUserService.getRandomInputter());
            } else {

                orderOperationInfo.setAssigner(internalUserService.getRandomCustomer());
            }
            // 判断保险公司是否为众安保险，如果是众安保险，则设置持有人为随机客服，持有人为随机录单员
            if (isZhonganInsuranceCompany(purchaseOrder)) {
                orderOperationInfo.setOwner(internalUserService.getRandomInputter());
            } else {
                orderOperationInfo.setOwner(orderOperationInfo.getAssigner());
            }

            if (purchaseOrder.getSourceChannel().getId().equals(Channel.Enum.PARTNER_ANBANG_61.getId())) {
                InternalUser TOAassignUser = internalUserService.getRandomTOACustomer();
                orderOperationInfo.setAssigner(TOAassignUser);
                orderOperationInfo.setOwner(TOAassignUser);
            }
        }
        orderOperationInfo.setPurchaseOrder(purchaseOrder);
        if (purchaseOrder.getStatus() != null) {
            if (purchaseOrder.getStatus().getId().equals(OrderStatus.Enum.CANCELED_6.getId())) {
                orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.CANCELED);
            } else if (purchaseOrder.getStatus().getId().equals(OrderStatus.Enum.INSURE_FAILURE_7.getId())) {
                orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.UNDERWRITING_FAILED);
            } else if (purchaseOrder.getStatus().getId().equals(OrderStatus.Enum.PENDING_PAYMENT_1.getId())) {
                orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.UNPAID);
            }
        } else {
            orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.UNPAID);
        }
        orderOperationInfo.setUpdateTime(purchaseOrder.getCreateTime());
        return orderOperationInfo;
    }

    // 判断该订单保险公司是否为众安保险
    private boolean isZhonganInsuranceCompany(PurchaseOrder purchaseOrder) {
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        if (quoteRecord.getInsuranceCompany() != null
            && InsuranceCompany.Enum.ZHONGAN_50000.getId().equals(quoteRecord.getInsuranceCompany().getId())) {
            return true;
        }
        return false;
    }

    public void setExpiredStatus(PurchaseOrder purchaseOrder, Boolean setHistory) {
        OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (orderOperationInfo != null) {
            // 修改出单状态
            OrderTransmissionStatus originalStatus = orderOperationInfo.getCurrentStatus();
            orderOperationInfo.setUpdateTime(new Date());
            orderOperationInfo.setOriginalStatus(originalStatus);
            orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.CANCELED);
            orderOperationInfo.setOwner(internalUserService.getRandomAdmin());
            orderOperationInfoRepository.save(orderOperationInfo);
            // 添加修改状态历史记录
            InternalUser operator = internalUserRepository.findFirstByName("system");
            if (setHistory) {
                orderProcessHistoryService.saveChangeStatusHistory(operator, purchaseOrder, originalStatus, OrderTransmissionStatus.Enum.CANCELED);
            }
        }
    }

    public void setExpiredStatus(PurchaseOrder purchaseOrder) {
        this.setExpiredStatus(purchaseOrder, true);
    }

    public void updateOrderStatus(PurchaseOrder purchaseOrder, OrderStatus orderStatus) {
        purchaseOrder.setStatus(orderStatus);
        purchaseOrder.setUpdateTime(Calendar.getInstance().getTime());
        purchaseOrderRepository.save(purchaseOrder);
    }

    public OrderOperationInfo updateOrderTransmissionStatus(OrderOperationInfo orderOperationInfo, OrderTransmissionStatus newStatus) {
        //出单状态变更为录单完成时同步填写确认日期，否则保单录入时无法选择机构
        if (OrderTransmissionStatus.Enum.ORDER_INPUTED == newStatus && orderOperationInfo.getConfirmOrderDate() == null) {
            orderOperationInfo.setConfirmOrderDate(new Date());
        }
        orderOperationInfo.setOriginalStatus(orderOperationInfo.getCurrentStatus());
        orderOperationInfo.setCurrentStatus(newStatus);
        orderOperationInfo.setUpdateTime(new Date());
        return orderOperationInfoRepository.save(orderOperationInfo);
    }

    /**
     * 提供给泛华退款之后调用
     *
     * @param purchaseOrder
     * @param newStatus
     * @return
     */
    @Transactional
    public OrderOperationInfo updateOrderTransmissionStatusByFanhua(PurchaseOrder purchaseOrder, OrderTransmissionStatus newStatus) {
        OrderOperationInfo orderOperationInfo = getByPurchaseOrder(purchaseOrder);
        OrderTransmissionStatus oriStatus = orderOperationInfo.getCurrentStatus();
        OrderOperationInfo operationInfo = this.updateOrderTransmissionStatus(orderOperationInfo, newStatus);
        InternalUser operator = InternalUser.ENUM.SYSTEM;
        orderProcessHistoryService.saveChangeStatusHistory(operator, orderOperationInfo.getPurchaseOrder(),
            oriStatus, orderOperationInfo.getCurrentStatus());
        return operationInfo;
    }

    public OrderOperationInfo updateOrderTransmissionStatus(PurchaseOrder purchaseOrder, OrderTransmissionStatus newStatus) {
        return updateOrderTransmissionStatus(getByPurchaseOrder(purchaseOrder), newStatus);
    }

    public void updatePurchaseOrderStatusForServiceSuccess(PurchaseOrder purchaseOrder) {
        InsuranceCompany insuranceCompany = quoteRecordRepository.findOne(purchaseOrder.getObjId()).getInsuranceCompany();
        logger.debug("{}承保完成后修改订单和出单状态，并设置支付号，订单号:{}", insuranceCompany.getName(), purchaseOrder.getOrderNo());
        OrderOperationInfo orderOperationInfo = getByPurchaseOrder(purchaseOrder);
        orderOperationInfo.setConfirmOrderDate(new Date());
        // 修改订单状态为订单完成
        updateOrderStatus(purchaseOrder, OrderStatus.Enum.FINISHED_5);
        // 修改出单状态为录单完成
        updateOrderTransmissionStatus(orderOperationInfo, OrderTransmissionStatus.Enum.ORDER_INPUTED);
        // 修改出单信息的支付号
        updateConfirmNo(orderOperationInfo, insuranceCompany);
        // 记录状态变更记录
        InternalUser operator = internalUserRepository.findFirstByName("system");
        orderProcessHistoryService.saveChangeStatusHistory(operator, purchaseOrder,
            OrderTransmissionStatus.Enum.UNCONFIRMED, OrderTransmissionStatus.Enum.ORDER_INPUTED);

        //同步钱包数据
        if (Channel.rebateToWallets().contains(purchaseOrder.getSourceChannel())) {
            walletTradeService.createAgentWalletTrade(quoteRecordRepository.findOne(purchaseOrder.getObjId()), purchaseOrder);
        }
    }

    private void updateConfirmNo(OrderOperationInfo orderOperationInfo, InsuranceCompany insuranceCompany) {
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        if (purchaseOrder == null || purchaseOrder.getChannel() == null)
            return;
        Payment payment = paymentRepository.findFirstByChannelAndPurchaseOrderOrderByIdDesc(purchaseOrder.getChannel(), purchaseOrder);
        if (payment != null && StringUtils.isNotBlank(payment.getThirdpartyPaymentNo())) {
            logger.debug("{}承保完成后订单设置支付号，订单号:{}，支付号:{}", insuranceCompany.getName(), purchaseOrder.getOrderNo(), payment.getThirdpartyPaymentNo());
            orderOperationInfo.setConfirmNo(payment.getThirdpartyPaymentNo());
            orderOperationInfoRepository.save(orderOperationInfo);
        }
    }


    public OrderOperationInfo save(OrderOperationInfo orderOperationInfo) {
        return orderOperationInfoRepository.save(orderOperationInfo);
    }

    /**
     * 第三方合作未完全处理订单
     *
     * @return
     */
    public List<OrderOperationInfo> findCooperationOrderByOperateStatus(List statusList, List sourceList) {
        return orderOperationInfoRepository.findCooperationOrderByOperateStatus(statusList, sourceList);
    }

    public OrderOperationInfo findByOrderNo(String orderNo) {
        return orderOperationInfoRepository.findByOrderNo(orderNo);
    }

    @Transactional
    public void offlinePay(OrderOperationInfo orderOperationInfo, PaymentChannel channel, String thirdpartyPaymentNo, InternalUser user) {
        updateOrderTransmissionStatus(orderOperationInfo, OrderTransmissionStatus.Enum.UNCONFIRMED);
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        purchaseOrder.setStatus(OrderStatus.Enum.PAID_3);
        purchaseOrder.setUpdateTime(new Date());
        purchaseOrder.setOperator(user);
        purchaseOrder.setChannel(channel);
        purchaseOrderRepository.save(purchaseOrder);

        List<Long> excludeType = Arrays.asList(PaymentType.Enum.DISCOUNT_5.getId(), PaymentType.Enum.CHECHEPAY_6.getId(), PaymentType.Enum.DAILY_RESTART_PAY_7.getId());
        List<Payment> payments = paymentRepository.findPaymentInfoByOrderId(purchaseOrder.getId(), excludeType);
        Payment payment = payments.get(payments.size() - 1);//paymentRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (!payment.getStatus().getId().equals(PaymentStatus.Enum.NOTPAYMENT_1.getId())) {
            throw new RuntimeException("save purchaseOrder =>" + purchaseOrder.getId() + " offlinePay error, current payment is error");
        }
        payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
        payment.setChannel(channel);
        payment.setThirdpartyPaymentNo(thirdpartyPaymentNo);
        payment.setUpdateTime(new Date());
        payment.setOperator(user);
        paymentRepository.save(payment);
    }

}
