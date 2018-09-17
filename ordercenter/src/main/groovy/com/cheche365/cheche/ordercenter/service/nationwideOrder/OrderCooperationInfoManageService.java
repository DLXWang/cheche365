package com.cheche365.cheche.ordercenter.service.nationwideOrder;

import com.cheche365.cheche.core.model.Institution;
import com.cheche365.cheche.core.model.InstitutionQuoteRecord;
import com.cheche365.cheche.core.model.OrderCooperationInfo;
import com.cheche365.cheche.core.model.OrderCooperationStatus;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.PurchaseOrderRefund;
import com.cheche365.cheche.core.repository.InstitutionQuoteRecordRepository;
import com.cheche365.cheche.core.repository.OrderCooperationInfoRepository;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRefundRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.ordercenter.constants.PaymentStatusEnum;
import com.cheche365.cheche.ordercenter.constants.ReasonEnum;
import com.cheche365.cheche.ordercenter.constants.RefundTypeEnum;
import com.cheche365.cheche.ordercenter.model.NationwideOrderQuery;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.web.service.order.ClientOrderService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/14.
 */
@Service
@Transactional
public class OrderCooperationInfoManageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderCooperationInfoRepository orderCooperationInfoRepository;

    @Autowired
    private InstitutionQuoteRecordRepository institutionQuoteRecordRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderRefundRepository purchaseOrderRefundRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private OrderCooperationStatusHandler orderCooperationStatusHandler;

    @Autowired
    private InstitutionManageService institutionManageService;

    @Autowired
    private ClientOrderService webPurchaseOrderService;

    /**
     * 返回全部订单列表
     * @param currentPage
     * @param pageSize
     * @param query
     * @return
     */
    public Page<OrderCooperationInfo> listTotalOrder(Integer currentPage, Integer pageSize, NationwideOrderQuery query) {
        try {
            Page<OrderCooperationInfo> orderCooperationInfoPage = this.findBySpecAndPaginate(
                null, query,
                this.buildPageable(currentPage, pageSize, "createTime"));
            return orderCooperationInfoPage;
        } catch (Exception e) {
            logger.error("find total order by page has error", e);
        }
        return null;
    }

    /**
     * 返回异常订单列表
     * @param currentPage
     * @param pageSize
     * @param query
     * @return
     */
    public Page<OrderCooperationInfo> listAbnormityOrder(Integer currentPage, Integer pageSize, NationwideOrderQuery query) {
        try {
            Page<OrderCooperationInfo> orderCooperationInfoPage = this.findBySpecAndPaginate(
                OrderCooperationStatus.Enum.ABNORMITY, query,
                this.buildPageable(currentPage, pageSize, "updateTime"));
            return orderCooperationInfoPage;
        } catch (Exception e) {
            logger.error("find abnotmity order by page has error", e);
        }
        return null;
    }

    /**
     * 返回新建订单列表
     * @param currentPage
     * @param pageSize
     * @param query
     * @return
     */
    public Page<OrderCooperationInfo> listNewOrder(Integer currentPage, Integer pageSize, NationwideOrderQuery query) {
        try {
            // this.refreshCreateStatus();
            Page<OrderCooperationInfo> orderCooperationInfoPage = this.findBySpecAndPaginate(
                OrderCooperationStatus.Enum.CREATED, query,
                this.buildPageable(currentPage, pageSize,"createTime"));
            return orderCooperationInfoPage;
        } catch (Exception e) {
            logger.error("find new order by page has error", e);
        }
        return null;
    }

    /**
     * 返回完成订单列表
     * @param currentPage
     * @param pageSize
     * @param query
     * @return
     */
    public Page<OrderCooperationInfo> listFinishedOrder(Integer currentPage, Integer pageSize, NationwideOrderQuery query) {
        try {
            this.refreshCreateStatus();
            Page<OrderCooperationInfo> orderCooperationInfoPage = this.findBySpecAndPaginate(
                OrderCooperationStatus.Enum.FINISHED, query,
                this.buildPageable(currentPage, pageSize,"updateTime"));
            return orderCooperationInfoPage;
        } catch (Exception e) {
            logger.error("find new order by page has error", e);
        }
        return null;
    }

    /**
     * 返回退款订单列表
     * @param currentPage
     * @param pageSize
     * @param query
     * @return
     */
    public Page<OrderCooperationInfo> listRefundOrder(Integer currentPage, Integer pageSize, NationwideOrderQuery query) {
        try {
            Page<OrderCooperationInfo> orderCooperationInfoPage = this.findBySpecAndPaginate(
                OrderCooperationStatus.Enum.REFUND, query,
                this.buildPageable(currentPage, pageSize, "updateTime"));
            return orderCooperationInfoPage;
        } catch (Exception e) {
            logger.error("find refund order by page has error", e);
        }
        return null;
    }

    /**
     * 返回已出单订单列表
     * @param currentPage
     * @param pageSize
     * @param query
     * @return
     */
    public Page<OrderCooperationInfo> listDoneOrder(Integer currentPage, Integer pageSize, NationwideOrderQuery query) {
        try {
            Page<OrderCooperationInfo> orderCooperationInfoPage = this.findBySpecAndPaginate(
                OrderCooperationStatus.Enum.INSURANCE, query,
                this.buildPageable(currentPage, pageSize, "updateTime"));
            return orderCooperationInfoPage;
        } catch (Exception e) {
            logger.error("find done order by page has error", e);
        }
        return null;
    }

    /**
     * 分页查询
     *
     * @param status  状态
     * @param orderQuery 查询条件
     * @param pageable 分页信息
     * @return Page<OrderCooperationInfo>
     */
    private Page<OrderCooperationInfo> findBySpecAndPaginate(OrderCooperationStatus status, NationwideOrderQuery orderQuery, Pageable pageable) throws Exception {
        return orderCooperationInfoRepository.findAll(new Specification<OrderCooperationInfo>() {
            @Override
            public Predicate toPredicate(Root<OrderCooperationInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<OrderCooperationInfo> criteriaQuery = cb.createQuery(OrderCooperationInfo.class);
                Path<Long> statusPath = root.get("status").get("id");

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();

                Root<InstitutionQuoteRecord> institutionQuoteRecordRoot=criteriaQuery.from(InstitutionQuoteRecord.class);
                institutionQuoteRecordRoot.join("purchaseOrder");

                // 订单新建
                if(status != null && OrderCooperationStatus.Enum.CREATED.getId().equals(status.getId())) {
                    predicateList.add(cb.equal(statusPath, OrderCooperationStatus.Enum.CREATED.getId()));
                }
                // 已报价待审核
                else if(status != null && OrderCooperationStatus.Enum.QUOTE_NO_AUDIT.getId().equals(status.getId())) {
                    predicateList.add(cb.equal(statusPath, OrderCooperationStatus.Enum.QUOTE_NO_AUDIT.getId()));
                }
                // 通过审核待结款
                else if(status != null && OrderCooperationStatus.Enum.AUDIT_NO_PAYMENT.getId().equals(status.getId())) {
                    predicateList.add(cb.equal(statusPath, OrderCooperationStatus.Enum.AUDIT_NO_PAYMENT.getId()));
                }
                // 结款完成待出单
                else if(status != null && OrderCooperationStatus.Enum.PAYMENT_NO_INSURANCE.getId().equals(status.getId())) {
                    predicateList.add(cb.equal(statusPath, OrderCooperationStatus.Enum.PAYMENT_NO_INSURANCE.getId()));
                }
                // 已出单
                else if(status != null && OrderCooperationStatus.Enum.INSURANCE.getId().equals(status.getId())) {
                    predicateList.add(cb.equal(statusPath, OrderCooperationStatus.Enum.INSURANCE.getId()));
                }
                // 订单完成
                else if(status != null && OrderCooperationStatus.Enum.FINISHED.getId().equals(status.getId())) {
                    predicateList.add(cb.equal(statusPath, OrderCooperationStatus.Enum.FINISHED.getId()));
                }
                // 订单异常
                else if(status != null && OrderCooperationStatus.Enum.ABNORMITY.getId().equals(status.getId())) {
                    predicateList.add(cb.equal(statusPath, OrderCooperationStatus.Enum.ABNORMITY.getId()));
                }
                // 订单退款
                else if(status != null && OrderCooperationStatus.Enum.REFUND.getId().equals(status.getId())) {
                    predicateList.add(cb.equal(statusPath, OrderCooperationStatus.Enum.REFUND.getId()));
                }
                // 订单号
                if (StringUtils.isNotBlank(orderQuery.getOrderNo())) {
                    Path<String> orderNoPath = root.get("purchaseOrder").get("orderNo");
                    predicateList.add(cb.like(orderNoPath, orderQuery.getOrderNo() + "%"));
                }
                // 车主
                if (StringUtils.isNotBlank(orderQuery.getOwner())) {
                    Path<String> ownerPath = root.get("purchaseOrder").get("auto").get("owner");
                    predicateList.add(cb.like(ownerPath, orderQuery.getOwner() + "%"));
                }
                // 车牌号
                if (StringUtils.isNotBlank(orderQuery.getLicensePlateNo())) {
                    Path<String> licenseNoPath = root.get("purchaseOrder").get("auto").get("licensePlateNo");
                    predicateList.add(cb.like(licenseNoPath, orderQuery.getLicensePlateNo() + "%"));
                }
                // 区域
                if (orderQuery.getAreaId() != null && orderQuery.getAreaId() != 0) {
                    Path<Long> areaPath = root.get("area").get("id");
                    predicateList.add(cb.equal(areaPath, orderQuery.getAreaId()));
                }
                // 状态
                if (orderQuery.getPaymentStatusId() != null && orderQuery.getPaymentStatusId() != PaymentStatusEnum.ALL.ordinal()) {
                    Path<Long> orderStatusPath = root.get("purchaseOrder").get("status").get("id");
                    // 已支付
                    if (orderQuery.getPaymentStatusId() == PaymentStatusEnum.PAID.ordinal()) {
                        CriteriaBuilder.In<Long> statusIdIn = cb.in(statusPath);
                        List<Long> statusIdList = OrderCooperationStatus.Enum.allAvailableIds();
                        statusIdList.forEach(statusId -> statusIdIn.value(statusId));
                        predicateList.add(statusIdIn);
                    }
                    // 未支付
                    else if (orderQuery.getPaymentStatusId() == PaymentStatusEnum.NO_PAY.ordinal()) {
                        predicateList.add(cb.and(cb.isNull(statusPath)));
                        CriteriaBuilder.In<Long> orderStatusIdIn = cb.in(orderStatusPath);
                        orderStatusIdIn.value(OrderStatus.Enum.PENDING_PAYMENT_1.getId());//创建
                        orderStatusIdIn.value(OrderStatus.Enum.INSURE_FAILURE_7.getId());//投保失败
                        orderStatusIdIn.value(OrderStatus.Enum.HANDLING_2.getId());//处理中
                        predicateList.add(orderStatusIdIn);
                    }
                    // 放弃支付
                    else if (orderQuery.getPaymentStatusId() == PaymentStatusEnum.GIVE_UP_PAY.ordinal()) {
                        predicateList.add(cb.and(cb.isNull(statusPath)));
                        CriteriaBuilder.In<Long> orderStatusIdIn = cb.in(orderStatusPath);
                        orderStatusIdIn.value(OrderStatus.Enum.CANCELED_6.getId());//订单取消
                        orderStatusIdIn.value(OrderStatus.Enum.EXPIRED_8.getId());//订单过期
                        predicateList.add(orderStatusIdIn);
                    }
                }
                // 异常原因
                if (orderQuery.getWarningReasonId() != null && orderQuery.getWarningReasonId() != 0) {
                    Path<String> reasonPath = root.get("reason");
                    predicateList.add(cb.equal(reasonPath, ReasonEnum.format(orderQuery.getWarningReasonId()).getContent()));
                }
                //出单机构
                if (StringUtils.isNotBlank(orderQuery.getInstitutionName())) {
                    Path<String> institutionNamePath=root.get("institution").get("name");
                    predicateList.add(cb.like(institutionNamePath, orderQuery.getInstitutionName() + "%"));
                }
                //20分钟未报价
                if(Boolean.TRUE.equals(orderQuery.getQuoteTime())){
                    Path<Boolean> quoteStatusPath=root.get("quoteStatus");
                    Path<Date> appointTimePath=root.get("appointTime");
                    predicateList.add(cb.and(cb.isNull(quoteStatusPath)));
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 20);
                    predicateList.add(cb.lessThanOrEqualTo(appointTimePath, calendar.getTime()));
                }

                //审核状态
                if(orderQuery.getAuditStatus() != null){
                    Path<Boolean> auditStatusPath=root.get("auditStatus");
                    if(orderQuery.getAuditStatus() == -1){
                        predicateList.add(cb.isNull(auditStatusPath));
                    }else{
                        predicateList.add(cb.equal(auditStatusPath, orderQuery.getAuditStatus()));
                    }
                }
                //快递单号
                if (StringUtils.isNotBlank(orderQuery.getTrackingNo())) {
                    Path<String> trackingNoPath = root.get("purchaseOrder").get("deliveryInfo").get("trackingNo");
                    predicateList.add(cb.like(trackingNoPath, orderQuery.getTrackingNo() + "%"));
                }
                // 保单号
                if (StringUtils.isNotBlank(orderQuery.getPolicyNo())) {
                    Path<Long> orderIdPath = root.get("purchaseOrder").get("id");
                    CriteriaBuilder.In<Long> orderIdIn = cb.in(orderIdPath);
                    List<BigInteger> orderIdList = institutionQuoteRecordRepository.listOrderByPolicyNo(orderQuery.getPolicyNo() + "%");
                    orderIdList.forEach(orderId -> orderIdIn.value(orderId.longValue()));
                    predicateList.add(orderIdIn);
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    /**
     * 构建分页信息
     *
     * @param currentPage 当前页面
     * @param pageSize    每页显示数
     * @param sortProperties    排序属性
     * @return Pageable
     */
    private Pageable buildPageable(int currentPage, int pageSize, String sortProperties) {
        Sort sort = new Sort(Sort.Direction.DESC, sortProperties);
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    /**
     * 返回支付状态
     * @param purchaseOrder
     * @return
     */
    public PaymentStatus getPaymentStatus(PurchaseOrder purchaseOrder) {
        List<PaymentChannel> paymentChannels = new ArrayList<>();
        paymentChannels.add(PaymentChannel.Enum.ALIPAY_1);
        paymentChannels.add(PaymentChannel.Enum.UNIONPAY_3);
        paymentChannels.add(PaymentChannel.Enum.WECHAT_4);
        Payment payment = paymentRepository.findFirstByChannelInAndPurchaseOrderOrderByIdDesc(paymentChannels, purchaseOrder);
        return payment == null? null : payment.getStatus();
    }

    /**
     * 返回指定id的订单合作信息
     * @param id
     * @return
     */
    public OrderCooperationInfo findById(Long id) {
        return orderCooperationInfoRepository.findOne(id);
    }

    /**
     * 放弃支付
     * @param id
     * @return
     */
    public OrderCooperationInfo giveUpPay(Long id) {
        OrderCooperationInfo orderCooperationInfo = findById(id);
        orderCooperationInfo.setUpdateTime(Calendar.getInstance().getTime());
        orderCooperationInfo.setOperator(internalUserManageService.getCurrentInternalUser());
        orderCooperationInfoRepository.save(orderCooperationInfo);
        PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
        webPurchaseOrderService.cancel(purchaseOrder);
        return orderCooperationInfo;
    }

    /**
     * 订单退款
     * @param id
     * @return
     */
    public boolean refund(Long id, String refundTo) {
        try {
            // 修改合作出单状态
            OrderCooperationInfo orderCooperationInfo = findById(id);
            orderCooperationInfo.setReason(null);
            orderCooperationStatusHandler.request(orderCooperationInfo, OrderCooperationStatus.Enum.REFUND, null);
            // 设置退款属性
            setRefundInfo(orderCooperationInfo, refundTo);
            return true;
        } catch (Exception ex) {
            logger.error("refund order error.", ex);
            return false;
        }
    }

    public void setRefundInfo(OrderCooperationInfo orderCooperationInfo, String refundTo) {
        PurchaseOrderRefund purchaseOrderRefund = purchaseOrderRefundRepository
            .findFirstByPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
        if(purchaseOrderRefund == null) {
            purchaseOrderRefund = new PurchaseOrderRefund();
            purchaseOrderRefund.setPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
            purchaseOrderRefund.setCreateTime(Calendar.getInstance().getTime());
        }
        purchaseOrderRefund.setUserCheck(refundTo.contains("1"));
        purchaseOrderRefund.setChecheCheck(refundTo.contains("2"));
        purchaseOrderRefund.setRebateCheck(refundTo.contains("3"));
        purchaseOrderRefund.setUserStatus(refundTo.contains("1")? false : null);
        purchaseOrderRefund.setChecheStatus(refundTo.contains("2")? false : null);
        purchaseOrderRefund.setUpdateTime(Calendar.getInstance().getTime());
        purchaseOrderRefund.setOperator(internalUserManageService.getCurrentInternalUser());
        purchaseOrderRefundRepository.save(purchaseOrderRefund);
    }

    /**
     * 订单重发
     * @param id
     * @return
     */
    public boolean resend(Long id) {
        try {
            // 修改合作出单状态
            OrderCooperationInfo orderCooperationInfo = findById(id);
            orderCooperationInfo.setReason(null);
            if(orderCooperationInfo.getAppointTime() != null) {
                orderCooperationInfo.setAppointTime(Calendar.getInstance().getTime());
            }
            orderCooperationStatusHandler.request(orderCooperationInfo, OrderCooperationStatus.Enum.CREATED, null);
            return true;
        } catch (Exception ex) {
            logger.error("resend order error.", ex);
            return false;
        }
    }


    /**
     * 更新新建状态
     * */
    public void refreshCreateStatus(){
        orderCooperationInfoRepository.updateByPaymentStatus();
    }

    /**
     * 指派出单机构
     * */
    public boolean appointInstitution(Long id,Long institutionId,Double commercialRebate,Double compulsoryRebate){
        try{
            OrderCooperationInfo orderCooperationInfo=orderCooperationInfoRepository.findOne(id);
            Institution institution=institutionManageService.findById(institutionId);
            if(orderCooperationInfo==null||institution==null){
                return false;
            }
            if(orderCooperationInfo.getInstitution()!=null&&(institution.getId().equals(orderCooperationInfo.getInstitution().getId()))){
                //如果选择的是当前出单机构，直接返回
                return true;
            }
            orderCooperationInfo.setInstitution(institution);
            orderCooperationInfo.setAppointTime(new Date());
            orderCooperationInfo.setUpdateTime(new Date());
            orderCooperationInfo.setOperator(internalUserManageService.getCurrentInternalUser());
            orderCooperationInfoRepository.save(orderCooperationInfo);
            //更改出单机构后，重置报价信息
            InstitutionQuoteRecord institutionQuoteRecord=institutionQuoteRecordRepository.findFirstByPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
            if(institutionQuoteRecord!=null){
                institutionQuoteRecord.clearAllData();
            }else{
                institutionQuoteRecord=new InstitutionQuoteRecord();
                institutionQuoteRecord.setPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
            }
            institutionQuoteRecord.setCompulsoryRebate(compulsoryRebate);
            institutionQuoteRecord.setCommercialRebate(commercialRebate);
            institutionQuoteRecord.setInstitution(institution);
            institutionQuoteRecordRepository.save(institutionQuoteRecord);
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    /**
     * 更新状态
     * */
    public void save(OrderCooperationInfo orderCooperationInfo){
        orderCooperationInfoRepository.save(orderCooperationInfo);
    }

    /**
     * 查询出单信息
     * 根据订单号
     * */
    public OrderCooperationInfo findByPurchaseOrder(PurchaseOrder purchaseOrder){
        return orderCooperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
    }

    public PurchaseOrderRefund getPurchaseOrderRefund(PurchaseOrder purchaseOrder) {
        return purchaseOrderRefundRepository.findFirstByPurchaseOrder(purchaseOrder);
    }

    public OrderCooperationInfo refundTo(Long id, RefundTypeEnum refundTypeEnum) {
        OrderCooperationInfo orderCooperationInfo = findById(id);
        PurchaseOrderRefund purchaseOrderRefund = getPurchaseOrderRefund(orderCooperationInfo.getPurchaseOrder());
        if(purchaseOrderRefund == null) {
            purchaseOrderRefund = new PurchaseOrderRefund();
            purchaseOrderRefund.setPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
            purchaseOrderRefund.setCreateTime(Calendar.getInstance().getTime());
        }
        if(refundTypeEnum.ordinal() == RefundTypeEnum.TO_USER.ordinal()) {
            purchaseOrderRefund.setUserCheck(true);
            purchaseOrderRefund.setUserStatus(true);
        } else {
            purchaseOrderRefund.setChecheCheck(true);
            purchaseOrderRefund.setChecheStatus(true);
        }
        purchaseOrderRefund.setUpdateTime(Calendar.getInstance().getTime());
        purchaseOrderRefund.setOperator(internalUserManageService.getCurrentInternalUser());
        purchaseOrderRefundRepository.save(purchaseOrderRefund);
        return orderCooperationInfo;
    }
}
