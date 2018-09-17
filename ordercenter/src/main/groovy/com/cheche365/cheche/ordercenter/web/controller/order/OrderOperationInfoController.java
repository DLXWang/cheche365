package com.cheche365.cheche.ordercenter.web.controller.order;

import com.cheche365.cheche.baoxian.model.RefundInfo;
import com.cheche365.cheche.baoxian.service.BaoXianRefundService;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.web.model.InsurancePurchaseOrderRebateViewModel;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InsurancePurchaseOrderRebateManageService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderProcessHistoryExpandService;
import com.cheche365.cheche.ordercenter.service.order.OrderCenterPurchaseOrderImageService;
import com.cheche365.cheche.ordercenter.service.order.OrderManageService;
import com.cheche365.cheche.ordercenter.service.order.OrderOperationInfoExpandService;
import com.cheche365.cheche.ordercenter.service.order.OrderTransmissionStatusHandler;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.ordercenter.web.model.InsuranceCompanyData;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.InstitutionRebateViewModel;
import com.cheche365.cheche.ordercenter.web.model.order.OrderOperationInfoViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by wangfei on 2015/12/10.
 */
@RestController
@RequestMapping(value = "/orderCenter/orderOperationInfos")
public class OrderOperationInfoController {
    private Logger logger = LoggerFactory.getLogger(OrderOperationInfoController.class);

    @Autowired
    private OrderOperationInfoExpandService orderOperationInfoExpandService;
    @Autowired
    private OrderManageService orderManageService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private OrderTransmissionStatusHandler orderTransmissionStatusHandler;
    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;
    @Autowired
    private InsuranceService insuranceService;
    @Autowired
    private CompulsoryInsuranceService compulsoryInsuranceService;
    @Autowired
    private QuoteRecordRepository quoteRecordRepository;
    @Autowired
    private InstitutionRebateHistoryService institutionRebateHistoryService;
    @Autowired
    private InsurancePurchaseOrderRebateManageService insurancePurchaseOrderRebateService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private OrderCenterPurchaseOrderImageService orderCenterPurchaseOrderImageService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private PurchaseOrderAmendService purchaseOrderAmendService;
    @Autowired
    private OrderOperationInfoService orderOperationInfoService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private IInternalUserManageService internalUserManageService;
    @Autowired
    private OrderProcessHistoryExpandService orderProcessHistoryExpandService;
    @Autowired
    private QuoteConfigService quoteConfigService;
    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository;
    @Autowired
    private BaoXianRefundService baoXianRefundService;


    @RequestMapping(value = "",method = RequestMethod.GET)
    @VisitorPermission("or0101")
    public DataTablePageViewModel getOrders(PublicQuery query) {
        Page<OrderOperationInfo> page = orderOperationInfoExpandService.getOrdersByPage(query);
        List<OrderOperationInfoViewModel> modelList = new ArrayList<>();
        page.getContent().forEach(orderOperationInfo -> modelList.add(createViewModel(orderOperationInfo)));
        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablePageViewModel<>(pageInfo.getTotalElements() ,pageInfo.getTotalElements(),query.getDraw(),modelList);
    }

    private OrderOperationInfoViewModel createViewModel(OrderOperationInfo orderOperationInfo) {
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        Payment payment = purchaseOrderService.getPaymentByPurchaseOrder(purchaseOrder);
        if (payment != null && payment.getStatus() != null) {
            payment.toDisplayText();
        }
        QuoteRecord quoteRecord = orderManageService.getQuoteRecordByPurchaseOrder(purchaseOrder);
        OrderOperationInfoViewModel viewModel = OrderOperationInfoViewModel.createViewModel(orderOperationInfo);
        viewModel.setInsuranceCompany(InsuranceCompanyData.createViewModel(quoteRecord.getInsuranceCompany()));

        viewModel.setAgentChannel(false);

        viewModel.setPaymentChannel(purchaseOrder.getChannel());
        viewModel.setPaymentStatus(null != payment ? payment.getStatus() : null);
        viewModel.setGift(purchaseOrderGiftService.getGiftDetail(purchaseOrder));
        OrderProcessHistory orderProcessHistory = orderProcessHistoryExpandService.getLatestHistoryByPurchaseOrder(purchaseOrder);
        viewModel.setLatestComment(null == orderProcessHistory ? "" : orderProcessHistory.getComment());

        //设置照片状态
        viewModel.setOrderImageStatus(orderCenterPurchaseOrderImageService.getImageStatus(purchaseOrder));

        //中华联合分地区
        if (BeanUtil.equalsID(InsuranceCompany.Enum.CIC_45000, quoteRecord.getInsuranceCompany())) {
            viewModel.setQuoteSource(quoteRecord.getType());
            viewModel.setInnerPay(quoteConfigService.isInnerPay(quoteRecord,purchaseOrder));
        }
        if (orderOperationInfo.getPurchaseOrder().getSourceChannel() != null) {
            if (!StringUtils.isEmpty(orderOperationInfo.getPurchaseOrder().getSourceChannel().getIcon())) {
                viewModel.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()),
                    orderOperationInfo.getPurchaseOrder().getSourceChannel().getIcon()));
            }
        }
        if (orderOperationInfo.getCurrentStatus().getId().equals(OrderTransmissionStatus.Enum.ADDITION_PAID.getId())) {
            Double paidPrice = purchaseOrderAmendService.getPaidAmountByOrderId(orderOperationInfo.getPurchaseOrder().getId());
            viewModel.setPaid(paidPrice.equals(new Double(0)) ? false : true);
        }
        Boolean isThirdPart = (orderOperationInfo.getPurchaseOrder().getSourceChannel() == null) ?
            false : orderOperationInfo.getPurchaseOrder().getSourceChannel().isThirdPartnerChannel();
        viewModel.setThirdPart(isThirdPart);
        viewModel.setFanhua(orderOperationInfo.isFanhua());
        viewModel.setSupportChangeStatus(quoteConfigService.isSupportManualQuote(purchaseOrder.getSourceChannel(),purchaseOrder.getArea(),quoteRecord.getInsuranceCompany()));
        return viewModel;
    }

    @RequestMapping(value = "/{orderOperationInfoId}", method = RequestMethod.GET)
    public OrderOperationInfoViewModel getOrderOperationInfo(@PathVariable Long orderOperationInfoId) {
        if (logger.isDebugEnabled()) {
            logger.debug("get orderOperationInfo by orderOperationInfoId -> {}", orderOperationInfoId);
        }
        OrderOperationInfo orderOperationInfo = orderOperationInfoService.getById(orderOperationInfoId);
        AssertUtil.notNull(orderOperationInfo, "can not find orderOperationInfo by id: " + orderOperationInfoId);
        return createViewModel(orderOperationInfo);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public List<OrderTransmissionStatus> getAllOrderStatus() {
        return OrderTransmissionStatus.Enum.getNewStatusList();
    }

    @RequestMapping(value = "/{orderOperationInfoId}/status/enable", method = RequestMethod.GET)
    public Map<String, Object> getAllOrderStatus(@PathVariable Long orderOperationInfoId) {
        if (logger.isDebugEnabled()) {
            logger.debug("get all enable status by orderOperationInfoId -> {}", orderOperationInfoId);
        }
        OrderOperationInfo orderOperationInfo = orderOperationInfoService.getById(orderOperationInfoId);
        AssertUtil.notNull(orderOperationInfo, "can not find orderOperationInfo by id -> " + orderOperationInfoId);
        Payment payment = purchaseOrderService.getPaymentByPurchaseOrder(orderOperationInfo.getPurchaseOrder());
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("oriStatus", orderOperationInfo.getCurrentStatus().getId());
        statusMap.put("switchStatus", orderOperationInfo.getCurrentStatus().next());
        return statusMap;
    }

    @RequestMapping(value = "/{orderOperationInfoId}/status", method = RequestMethod.PUT)
    public ResultModel updateStatus(@PathVariable Long orderOperationInfoId,
                                    @RequestParam(value = "newStatus", required = true) Long newStatus,
                                    @RequestParam(value = "owner", required = false) Long owner,
                                    @RequestParam(value = "institutionId", required = false) Long institutionId,
                                    @RequestParam(value = "commercialRebate", required = false) Double commercialRebate,
                                    @RequestParam(value = "compulsoryRebate", required = false) Double compulsoryRebate,
                                    @RequestParam(value = "clientType", required = false) String clientType,
                                    @RequestParam(value = "number", required = false) String number) {
        logger.debug("update order status by orderOperationInfoId -> {}, newStatus -> {}, owner -> {}",
            orderOperationInfoId, newStatus, owner);
        OrderTransmissionStatus transmissionStatus = OrderTransmissionStatus.Enum.format(newStatus);
        AssertUtil.notNull(transmissionStatus, "illegal argument newStatus: " + newStatus);
        OrderOperationInfo orderOperationInfo = orderOperationInfoService.getById(orderOperationInfoId);
        AssertUtil.notNull(orderOperationInfo, "can not find orderOperationInfo by id -> " + orderOperationInfoId);
        if (!validOrderInputed(orderOperationInfo)) {
            return new ResultModel(false, "更改状态失败，请先录入保单");
        }
        if (orderOperationInfo.getCurrentStatus().getId().equals(OrderTransmissionStatus.Enum.APPLY_FOR_REFUND.getId()) && transmissionStatus.getId().equals(OrderTransmissionStatus.Enum.UNCONFIRMED.getId())) {
            Boolean isMember = stringRedisTemplate.opsForSet().isMember("syn.refunding.order.id", orderOperationInfo.getPurchaseOrder().getOrderNo());
            if (isMember) {
                return new ResultModel(false, "正在退款中,不能取消!");
            }
        }
        if(!StringUtil.isNull(clientType)){
            Map extra = new HashMap();extra.put("clientType",clientType);extra.put("thirdpartyPaymentNo",number);
            orderOperationInfo.getCurrentStatus().getId().equals(OrderTransmissionStatus.Enum.APPLY_FOR_REFUND.getId());
            try{
                orderTransmissionStatusHandler.request(orderOperationInfo, transmissionStatus, null, owner, extra);
                return new ResultModel();
            }catch(RuntimeException e){
                return new ResultModel(false,"失败");
            }
        }
        orderTransmissionStatusHandler.request(orderOperationInfo, transmissionStatus, owner);
        if (OrderTransmissionStatus.Enum.CONFIRM_TO_ORDER.getId().equals(newStatus)) {
            createInsurancePurchaseOrderRebate(orderOperationInfo, institutionId, commercialRebate, compulsoryRebate);
        }
        return new ResultModel();
    }

    /**
     * 泛华"出单中"订单，申请退款
     */
    @Transactional
    @RequestMapping(value = "/{orderOperationInfoId}/fanHua/refund", method = RequestMethod.PUT)
    public ResultModel fanHuaRefund(@PathVariable Long orderOperationInfoId) {
        logger.debug("fanHua refund by orderOperationInfoId -> {}", orderOperationInfoId);
        OrderOperationInfo orderOperationInfo = orderOperationInfoService.getById(orderOperationInfoId);
        OrderTransmissionStatus transmissionNewStatus = OrderTransmissionStatus.Enum.REFUNDED;
        OrderTransmissionStatus transmissionOriStatus = orderOperationInfo.getCurrentStatus();
        PurchaseOrder purchaseOrder=orderOperationInfo.getPurchaseOrder();
        QuoteRecord quoteRecord=quoteRecordRepository.findOne(purchaseOrder.getObjId());
        if(!orderOperationInfo.isFanhua()){
            return new ResultModel(false,"此订单不支持退款，请线下操作");
        }
        Payment payment=createPayment(purchaseOrder);
        RefundInfo refundInfo=createRefundInfo(purchaseOrder.getOrderSourceId(),quoteRecord.getInsuranceCompany(),quoteRecord.getArea(),new HashMap());
        try{
            logger.debug("泛华退款-->{}", CacheUtil.doJacksonSerialize(refundInfo));
            baoXianRefundService.refund(refundInfo);
        }catch(Exception e){
            logger.error("出单中心泛华订单全额退款失败,订单号:->{}",purchaseOrder.getOrderNo(),e);
            return new ResultModel(false,"线上退款失败，请联系管理员");
        }finally {
            paymentResult(payment,false);
        }
        paymentResult(payment,true);
        orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo, transmissionNewStatus);
        orderTransmissionStatusHandler.afterHandle(orderOperationInfo, transmissionOriStatus,"泛华退款");
        purchaseOrder.setStatus(OrderStatus.Enum.REFUNDED_9);
        purchaseOrder.setUpdateTime(new Date());
        purchaseOrder.setStatusDisplay(null);
        purchaseOrder.setOperator(internalUserManageService.getCurrentInternalUser());
        purchaseOrderService.saveOrder(purchaseOrder);
        logger.debug("出单中心泛华订单全额退款成功,订单号:->{}",purchaseOrder.getOrderNo());
        return new ResultModel();
    }


    /**
     * 根据车牌所在城市和保险公司查询出单机构
     *
     * @return
     */
    @RequestMapping(value = "/rebate/{orderOperationInfoId}", method = RequestMethod.GET)
    public List<InstitutionRebateViewModel> listInstitutionRebateByOrderOperationInfoId(@PathVariable Long orderOperationInfoId) {
        OrderOperationInfo orderOperationInfo = orderOperationInfoService.getById(orderOperationInfoId);
        Date confirmOrderDate = orderOperationInfo.getConfirmOrderDate();
        if (confirmOrderDate == null) {
            confirmOrderDate = new Date();
        }
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        List<InstitutionRebateHistory> institutionRebateHistoryList = institutionRebateHistoryService.ListByAreaAndInsuranceCompanyAndDateTime(quoteRecord.getArea(), quoteRecord.getInsuranceCompany().getId(), confirmOrderDate);
        return createInstitutionRebateViewModelByHistory(institutionRebateHistoryList);
    }

    private boolean validOrderInputed(OrderOperationInfo orderOperationInfo) {
        Long objId = orderOperationInfo.getPurchaseOrder().getObjId();
        if (insuranceService.findByQuoteRecordId(objId) != null || compulsoryInsuranceService.findByQuoteRecordId(objId) != null) {
            return true;
        }
        return false;
    }

    private void createInsurancePurchaseOrderRebate(OrderOperationInfo orderOperationInfo,Long institutionId,Double commercialRebate,Double compulsoryRebate){
        if(institutionId==null||commercialRebate==null||compulsoryRebate==null) {
            return;
        }
        InsurancePurchaseOrderRebateViewModel viewModel = new InsurancePurchaseOrderRebateViewModel();
        viewModel.setDownRebateChannel(RebateChannel.Enum.REBATE_CHANNEL_INSTITUTION);
        viewModel.setDownChannelId(institutionId);
        InsuranceCompany insuranceCompany=insuranceService.findByQuoteRecordId(orderOperationInfo.getPurchaseOrder().getObjId()).getInsuranceCompany();
        InstitutionRebateHistory rebate=institutionRebateHistoryService.findByInstitutionAndDateTimeAndAreAndCompany(institutionId,orderOperationInfo.getConfirmOrderDate(),orderOperationInfo.getPurchaseOrder().getArea().getId(),insuranceCompany.getId());
        viewModel.setDownCommercialRebate(rebate != null?rebate.getCommercialRebate():0);
        viewModel.setDownCompulsoryRebate(rebate != null?rebate.getCompulsoryRebate():0);
        viewModel.setPurchaseOrderId(orderOperationInfo.getPurchaseOrder().getId());
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(orderOperationInfo.getPurchaseOrder().getId());
        viewModel.setCommercialPremium(quoteRecord.getPremium());
        viewModel.setCompulsoryPremium(quoteRecord.getCompulsoryPremium());
        insurancePurchaseOrderRebateService.savePurchaseOrderRebate(viewModel);
        if (orderOperationInfo.getConfirmOrderDate() == null) {
            orderOperationInfo.setConfirmOrderDate(new Date());
            orderOperationInfoService.save(orderOperationInfo);
        }
    }

    private List<InstitutionRebateViewModel> createInstitutionRebateViewModelByHistory(List<InstitutionRebateHistory> institutionRebateHistoryListList) {
        List<InstitutionRebateViewModel> institutionRebateViewModelList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(institutionRebateHistoryListList)) {
            for (InstitutionRebateHistory institutionRebateHistory : institutionRebateHistoryListList) {
                InstitutionRebate institutionRebate = new InstitutionRebate();
                String[] properties = {"institution", "area", "insuranceCompany", "commercialRebate", "compulsoryRebate"};
                BeanUtil.copyPropertiesContain(institutionRebateHistory, institutionRebate, properties);
                institutionRebateViewModelList.add(InstitutionRebateViewModel.createViewModel(institutionRebate));
            }
        }
        return institutionRebateViewModelList;
    }

    private Payment createPayment(PurchaseOrder purchaseOrder) {
        Payment payment = new Payment();
        payment.setPurchaseOrder(purchaseOrder);
        payment.setChannel(PaymentChannel.Enum.BAOXIAN_PAY_16);
        payment.setClientType(Channel.Enum.ORDER_CENTER_11);
        payment.setStatus(PaymentStatus.Enum.NOTPAYMENT_1);
        payment.setPaymentType(PaymentType.Enum.FULLREFUND_4);
        payment.setCreateTime(purchaseOrder.getCreateTime());
        payment.setUser(purchaseOrder.getApplicant());
        payment.setAmount(purchaseOrder.getPaidAmount());
        payment.setOperator(internalUserManageService.getCurrentInternalUser());
        return paymentRepository.save(payment);
    }

    private void paymentResult(Payment payment,Boolean success){
        if(success){
            payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
            payment.setComments("出单中心泛华线上全额退款成功");
        }else{
            payment.setStatus(PaymentStatus.Enum.PAYMENTFAILED_3);
            payment.setComments("出单中心泛华线上全额退款失败");
        }
        payment.setUpdateTime(new Date());
        paymentRepository.save(payment);
    }

    private RefundInfo createRefundInfo(String taskId,InsuranceCompany insuranceCompany,Area area,Map additionalParameters){
        RefundInfo refundInfo=new RefundInfo();
        refundInfo.setTaskId(taskId);
        refundInfo.setInsuranceCompany(insuranceCompany);
        refundInfo.setArea(area);
        refundInfo.setAdditionalParameters(additionalParameters);
        return refundInfo;
    }
}
