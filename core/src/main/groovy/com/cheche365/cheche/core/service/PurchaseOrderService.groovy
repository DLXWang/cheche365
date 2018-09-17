package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import static com.cheche365.cheche.core.model.Channel.Enum.*

/**
 * @author sunhuazhong
 */
@Service(value = "purchaseOrderService")
@Transactional
public class PurchaseOrderService {

    private Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private GiftService giftService;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Autowired
    private BusinessActivityRepository businessActivityRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private OrderAgentRepository orderAgentRepository;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    public PurchaseOrder findFirstByOrderNo(String orderNo) {
        return purchaseOrderRepository.findFirstByOrderNo(orderNo);
    }


    Page<PurchaseOrder> findEffectiveOrdersByStatusAndApplicant(List<OrderStatus> status, User user, Channel channel, Pageable pageable) {
        List<Channel> channels = Channel.findChannels(channel)
        return purchaseOrderRepository.findEffectiveOrdersByStatusAndApplicant(status, user, channels, pageable);
    }

    public PurchaseOrder findByQuoteRecordId(Long quoteRecordId){
        return this.purchaseOrderRepository.findByQuoteRecordId(quoteRecordId);
    }


    public PurchaseOrder findById(Long id) {
        return purchaseOrderRepository.findOne(id);
    }

    private Address getAddress(Address address) {
        return addressService.checkExist(address);
    }


    //以下四个方法，如果是2c的调用都用(orderNo, user)的形式，user从session中取，防止用户给任意订单号都能带出保单，造成数据丢失，内部调用可以用(order)参数形式
    public Insurance getInsuranceBillsByOrder(PurchaseOrder order) {
        return this.insuranceRepository.searchByPurchaseOrderNo(order.getOrderNo(), order.getApplicant().getId());
    }

    public Insurance getInsuranceBillsByOrder(String orderNo, User user) {
        return this.insuranceRepository.searchByPurchaseOrderNo(orderNo, user.getId());
    }

    public CompulsoryInsurance getCIBillByOrder(PurchaseOrder order) {
        return this.compulsoryInsuranceRepository.searchByPurchaseOrderNo(order.getOrderNo(), order.getApplicant().getId());
    }

    public CompulsoryInsurance getCIBillByOrder(String orderNo, User user) {
        return this.compulsoryInsuranceRepository.searchByPurchaseOrderNo(orderNo, user.getId());
    }

    public PurchaseOrder saveOrder(PurchaseOrder order) {
        return this.purchaseOrderRepository.save(order);
    }



    public PurchaseOrder getFirstPurchaseOrderByNo(String orderNo){
        return this.purchaseOrderRepository.findFirstByOrderNo(orderNo);
    }

    public QuoteRecord findQuoteRecord(PurchaseOrder order){
        return quoteRecordRepository.findOne(order.getObjId());

    }


    public long getTotalCount() {
        return purchaseOrderRepository.count();
    }

    public long getNewOrderTotalCount(Long internalUserId) {
        return purchaseOrderRepository.findNewOrderCount(internalUserId);
    }

    /**
     * 根据用户名和手机号获取获取新订单记录总数
     * @return
     */
    public long getNewOrderTotalCountByUser(String mobile, String name, Long internalUserId){
        return purchaseOrderRepository.findNewOrderCountByUser(mobile, name, internalUserId);
    }


    /**
     * 累计保单总金额（含线上、线下，优惠后金额）
     * @param type
     * @return
     */
    public Double getSumPaidAmount(int type) {
        return purchaseOrderRepository.getSumPaidAmount(type);
    }

    /**
     * 累计到账总金额（线上，优惠后金额）
     * @param type
     * @return
     */
    public Double getOnLineSumPaidAmount(int type) {
        return purchaseOrderRepository.getOnLineSumPaidAmount(type);
    }

    /**
     * 累计保单数量（支付成功，含订单完成）
     * @param type
     * @return
     */
    public Long getSumPaidOrderCount(int type) {
        return purchaseOrderRepository.getSumPaidOrderCount(type);
    }

    /**
     * 昨日累计保单总金额（含线上、线下，优惠后金额）
     * @param type
     * @return
     */
    public Double getYesterdaySumPaidAmount(int type) {
        return purchaseOrderRepository.getYesterdaySumPaidAmount(type);
    }

    /**
     * 昨日累计到账总金额（线上，优惠后金额）
     * @param type
     * @return
     */
    public Double getYesterdayOnLineSumPaidAmount(int type) {
        return purchaseOrderRepository.getYesterdayOnLineSumPaidAmount(type);
    }

    /**
     * 昨日累计保单数量（支付成功，含订单完成）
     * @param type
     * @return
     */
    public Long getYesterdaySumPaidOrderCount(int type) {
        return purchaseOrderRepository.getYesterdaySumPaidOrderCount(type);
    }


    public void assambleOrderDescription(List<Payment> payments, PurchaseOrder order) {
        StringBuffer description = new StringBuffer();
        for (Payment payment : payments) {
            description.append(",");
            description.append(payment.getComments()).append(payment.getAmount()).append("元");
        }

        if (order.getRealGifts() != null && !order.getRealGifts().isEmpty() && !UserType.Enum.isAgent(order.getApplicant().getUserType())) {
            for (Gift gift : order.getRealGifts()) {
                description.append(",赠送");
                description.append(gift.getGiftType().getName()).append(gift.getQuantity()).append(gift.getUnit());
            }
        }
        order.appendDescription(description.length() > 0 ? description.substring(1) : "");
    }

    /**
     * Deprecated Reason:
     * 统一使用新的取消订单方法
     * PurchaseOrderService.cancel(String purchaseOrderNo,User user, StringBuilder debugMessage);
     */
    @Deprecated
    public void cancelOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null || purchaseOrder.getId() == null)
            return;

        //重置订单状态
        this.resetOrderStatus(purchaseOrder);

        //重置礼品状态
        giftService.resetOrderGift(purchaseOrder);
    }

    public void resetOrderStatus(PurchaseOrder purchaseOrder) {
        logger.info("reset order status by purchaseOrder -> id : {}", purchaseOrder.getId());
        purchaseOrder.setStatus(OrderStatus.Enum.CANCELED_6);
        purchaseOrder.setUpdateTime(new Date());
        purchaseOrder.appendDescription(" 订单被取消");
        purchaseOrderRepository.save(purchaseOrder);

        orderOperationInfoService.setExpiredStatus(purchaseOrder);
    }

    public List<PurchaseOrder> listExpiredOrder(Date expiredDate) {
        return purchaseOrderRepository.getExpiredOrderListByDate(expiredDate);
    }

    public List<PurchaseOrder> findCompletedOrderByWeek(int type) {
        return purchaseOrderRepository.findCompletedOrderByWeek(type);
    }

    public Map<String, List<Map<String, String>>> findOrderDetails(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "获取订单优惠信息的订单为空");
        }

        List<Payment> payments = paymentRepository.findByPurchaseOrder(purchaseOrder);
        if (payments == null || payments.isEmpty()) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "指定订单:" + purchaseOrder.getId() + "未查找到任何payment支付信息");
        }

        List<Map<String, String>> paymentResult = new ArrayList<>()
        payments.each { payment ->
            if (!payment.getChannel().isCustomerPay()) {
                Map<String, String> detail = new HashMap<>();
                detail.put(PurchaseOrder.DiscountEnum.PAYMENT_DISCOUNT_TYPE, payment.getComments());
                detail.put(PurchaseOrder.DiscountEnum.PAYMENT_DISCOUNT_AMOUNT, String.valueOf(payment.getAmount()));
                paymentResult.add(detail);
            }
        }

        List<Map<String, String>> giftResult = new ArrayList<>();
        List<Gift> gifts = purchaseOrderGiftService.findGiftByPurchaseOrder(purchaseOrder)
        gifts.each { gift ->
            if (!GiftTypeUseType.Enum.REDUCE_1.getId().equals(gift.getGiftType().getUseType().getId())) {
                Map<String, String> detail = new HashMap<>();
                detail.put(PurchaseOrder.DiscountEnum.GIFT_DISCOUNT_TYPE, gift.getGiftType().getName());
                detail.put(PurchaseOrder.DiscountEnum.GIFT_DISCOUNT_AMOUNT, gift.getGiftDisplay());
                giftResult.add(detail);
            }
        }

        Map<String, List<Map<String, String>>> result = new HashMap<>();
        result.put(PurchaseOrder.DiscountEnum.PAYMENT_DISCOUNT, paymentResult);
        result.put(PurchaseOrder.DiscountEnum.GIFT_DISCOUNT, giftResult);
        return result;
    }

    /**
     * 获取用户来源
     * 区分普通用户，大客户，代理人
     * CPS渠道暂时无法确认
     * @param purchaseOrder
     * @return
     */
    public String getUserSource(PurchaseOrder purchaseOrder) {

        // CPS渠道
        if(purchaseOrder.getOrderSourceType() != null
            && OrderSourceType.Enum.CPS_CHANNEL_1.getId().equals(purchaseOrder.getOrderSourceType().getId())) {
            if(purchaseOrder.getOrderSourceId() == null) {
                return "商务活动";
            } else {
                BusinessActivity businessActivity = businessActivityRepository.findOne(Long.valueOf(purchaseOrder.getOrderSourceId()));
                String source = businessActivity == null? "商务活动" : "(商务活动)" + businessActivity.getName();
                return source;
            }
        }

        // 代理人
        else if(purchaseOrder.getApplicant() != null
            && purchaseOrder.getApplicant().getUserType() != null
            && purchaseOrder.getApplicant().getUserType().getId() == UserType.Enum.Agent.getId()) {
            String source;
            OrderAgent orderAgent=orderAgentRepository.findByPurchaseOrder(purchaseOrder);
            if(orderAgent==null||orderAgent.getAgent()==null){
                source="个人";
            }else{
                source="(推荐)" +orderAgent.getAgent().getName();
            }
            return source;
        }

        // 个人
        else {
            return "个人";
        }
    }

    public Map assambleOrder(List<PurchaseOrder> orders) {
        if (orders == null) {
            return null;
        }

        Map map = new TreeMap<>().descendingMap();
        for (PurchaseOrder order : orders) {
            map.put(order.getOrderNo(), order.getObjId());
        }
        return map;
    }

    public PurchaseOrder updateOrderDeliveryInfo(PurchaseOrder purchaseOrder, DeliveryInfo deliveryInfo) {
        purchaseOrder.setDeliveryInfo(deliveryInfo);
        purchaseOrder.setUpdateTime(new Date());
        return purchaseOrderRepository.save(purchaseOrder);
    }


    public List<PurchaseOrder> findCompletedOrderByCreateTimeBetween(Date startDate,Date endDate){
        return purchaseOrderRepository.findCompletedOrderByCreateTimeBetween(startDate, endDate);
    }

    public Long countByOrderNo(String orderNo) {
        return purchaseOrderRepository.countByOrderNo(orderNo);
    }


    public List<PurchaseOrder>findUnPayOrderByCreateTimeBetween(Date startDate,Date endDate,int id){
        return purchaseOrderRepository.findUnPayOrderByCreateTimeBetween(startDate, endDate, id);
    }



    public Payment getPaymentByPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (null == purchaseOrder || null == purchaseOrder.getChannel()) return null;
        return paymentRepository.findFirstByChannelAndPurchaseOrderOrderByIdDesc(purchaseOrder.getChannel(), purchaseOrder);
    }

    public String getPaymentStatus(PurchaseOrder purchaseOrder) {
        Payment payment = getPaymentByPurchaseOrder(purchaseOrder);
        if(payment==null){
            return WebConstants.NO_PAID_TEXT;
        }
        return payment.getStatus().getStatus();
    }

    /**
     * 通过车车购买过车险的用户（排除代理人出单的用户）&符合投保人与保单收货人，姓名相同的用户
     * @return
     */
    public List<String> findMemberBirthdayWishedUserMobile(String currentDate) {
        return purchaseOrderRepository.findMemberBirthdayWishedUserMobile(currentDate);
    }

}
