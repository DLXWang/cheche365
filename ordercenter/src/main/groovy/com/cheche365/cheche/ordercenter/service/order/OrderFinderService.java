package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.agent.ChannelAgentPurchaseOrderRebate;
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.repository.agent.ChannelAgentOrderRebateRepository;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.QuoteFlowConfigService;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.model.PurchaseOrderModifyView;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.ordercenter.web.model.order.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.*;

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000;

/**
 * Created by wangfei on 2015/5/4.
 */
@Service(value = "orderFinderService")
public class OrderFinderService extends BaseService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;

    @Autowired
    private DailyInsuranceRepository dailyInsuranceRepository;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private WechatUserInfoRepository wechatUserInfoRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private MoApplicationLogRepository applicationLogMongoRepository;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private QuoteSupplementInfoService quoteSupplementInfoService;

    @Autowired
    private InsurancePurchaseOrderRebateRepository orderRebateRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    PurchaseOrderImageStatusRepository purchaseOrderImageStatusRepository;

    @Autowired
    private PurchaseOrderHistoryRepository purchaseOrderHistoryRepository;

    @Autowired
    private PurchaseOrderAmendService purchaseOrderAmendService;

    @Autowired
    private DailyRestartInsuranceRepository dailyRestartInsuranceRepository;

    @Autowired
    private ThirdServiceFailRepository thirdServiceFailRepository;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Autowired
    private DoubleDBService doubleDBService;

    @Autowired
    private QuoteFlowConfigService quoteFlowConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChannelAgentOrderRebateRepository channelAgentOrderRebateRepository;
    @Autowired
    private QuoteConfigService quoteConfigService;


    /**
     * 构建页面数据
     *
     * @param orderOperationInfo OrderOperationInfo
     * @return OrderViewData
     */
    public OrderViewData getOrderViewData(OrderOperationInfo orderOperationInfo) {
        OrderViewData viewData = new OrderViewData();
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        QuoteRecord quoteRecord = quoteRecordService.getById(orderOperationInfo.getPurchaseOrder().getObjId());
        viewData.setId(purchaseOrder.getId());
        viewData.setOrderOperationId(orderOperationInfo.getId());
        viewData.setPurchaseOrderId(purchaseOrder.getId());
        viewData.setOrderNo(purchaseOrder.getOrderNo());
        viewData.setOwner(purchaseOrder.getAuto().getOwner());
        viewData.setLicenseNo(purchaseOrder.getAuto().getLicensePlateNo());
        viewData.setModelName(purchaseOrder.getAuto().getAutoType() == null ? "" : purchaseOrder.getAuto().getAutoType().getModel());
        viewData.setInsuranceCompanyCode(quoteRecord.getInsuranceCompany().getCode());
        viewData.setInsuranceCompany(quoteRecord.getInsuranceCompany().getName());
        viewData.setSumPremium(purchaseOrder.getPaidAmount());
        viewData.setPayableAmount(purchaseOrder.getPayableAmount());//原始金额
        viewData.setChannel(purchaseOrder.getSourceChannel() != null ? purchaseOrder.getSourceChannel().getId() : 0);
        // 代理人
        if (purchaseOrder.getApplicant() != null && purchaseOrder.getApplicant().getUserType() != null
                && purchaseOrder.getApplicant().getUserType().getId() == UserType.Enum.Agent.getId()) {
            Agent agent = agentRepository.findFirstByUser(purchaseOrder.getApplicant());
            if (agent != null) {
                viewData.setRebate(agent.getRebate());
            }
        }
        viewData.setCreateTime(DateUtils.getDateString(purchaseOrder.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewData.setAssignerName(orderOperationInfo.getAssigner() == null ? "" : orderOperationInfo.getAssigner().getName());
        viewData.setOperatorName(orderOperationInfo.getOperator() == null ? "" : orderOperationInfo.getOperator().getName());
        viewData.setUpdateTime(orderOperationInfo.getUpdateTime() == null ? "" : DateUtils.getDateString(
                orderOperationInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewData.setCurrentStatus(orderOperationInfo.getCurrentStatus().getStatus());
        viewData.setPayTime(DateUtils.getDateString(
                purchaseOrder.getSendDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        viewData.setStatusId(orderOperationInfo.getCurrentStatus().getId());
        viewData.setQuoteArea(quoteRecord.getArea() == null ? "" : quoteRecord.getArea().getName());
        viewData.setUserSource(purchaseOrderService.getUserSource(purchaseOrder));

        viewData.setPayStatus(purchaseOrderService.getPaymentStatus(purchaseOrder));

        //备注
        viewData.setRemark(orderOperationInfo.getComment());
        if (orderOperationInfo.getPurchaseOrder().getSourceChannel() != null) {
            if (!StringUtils.isEmpty(orderOperationInfo.getPurchaseOrder().getSourceChannel().getIcon())) {
                viewData.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()),
                        orderOperationInfo.getPurchaseOrder().getSourceChannel().getIcon()));
            }
        }
        //邀请人
        User user = userService.findInviterByPurchaseOrderId(purchaseOrder.getId());
        if (user != null && user.getName() != null) {
            viewData.setInviter(user.getName());
        }
        //间接邀请人
        User UserInfo = userService.findTopInviterByByPurchaseOrderId(purchaseOrder.getId());
        if (UserInfo != null && UserInfo.getName() != null) {
            viewData.setIndirectionInviter(UserInfo.getName());
        }

        return viewData;
    }

    /**
     * 订单详情
     *
     * @param purchaseOrderId
     * @return OrderDetailViewData
     * @throws Exception
     */
    public OrderDetailViewData getOrderDetail(Long purchaseOrderId) {
        OrderDetailViewData orderDetailViewData = new OrderDetailViewData();
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(purchaseOrderId);
        this.getPurchaseOrderDetail(orderDetailViewData, purchaseOrder);

        this.getPaymentInfoDetail(orderDetailViewData, purchaseOrderId);
        this.getOderOperationInfoDetail(orderDetailViewData, purchaseOrder);
        this.getInsuranceDetail(orderDetailViewData, purchaseOrder);
        this.isRealQuote(purchaseOrder, orderDetailViewData);
        orderDetailViewData.setThirdPart(purchaseOrder.getSourceChannel().isThirdPartnerChannel());
        orderDetailViewData.setDailyInsurance(enableDailyInsuranceBtn(purchaseOrder));
        this.getAgentRebateInfo(orderDetailViewData, purchaseOrder);
        return orderDetailViewData;
    }

    /**
     * 获取订单跟踪信息
     *
     * @return OrderDetailViewData
     * @throws Exception
     */
    public Map<String, String> getOrderFollowInfo(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(purchaseOrderId);
        OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
        TelMarketingCenter telMarketingCenter = telMarketingCenterRepository.findFirstByUser(purchaseOrder.getApplicant());
        Map<String, String> orderFollowInfo = new HashMap<>();
        if (orderOperationInfo.getAssigner() != null) {
            orderFollowInfo.put("followPerson", orderOperationInfo.getAssigner().getName());
        }
        if (orderOperationInfo.getOwner() != null) {
            orderFollowInfo.put("inputPerson", orderOperationInfo.getOwner().getName());
        }
        if (telMarketingCenter != null && telMarketingCenter.getOperator() != null) {
            orderFollowInfo.put("telFollowPerson", telMarketingCenter.getOperator().getName());
        }
        return orderFollowInfo;
    }


    /**
     * 获取险种（有保单取保单数据，没有取订单数据）
     *
     * @param orderDetailViewData
     * @param purchaseOrder
     * @throws Exception
     */
    public void getInsuranceDetail(OrderDetailViewData orderDetailViewData, PurchaseOrder purchaseOrder) {
        /* 报价记录 */
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        /* 商业险保单 */
        Insurance insurance = insuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        /* 交强险保单 */
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        orderDetailViewData.setOwnerMobile(quoteRecord.getOwnerMobile());
        /* 有保单取保单数据，没有取订单数据 */
        if (insurance != null) {
            orderDetailViewData.setCommercialPolicyNo(insurance.getPolicyNo());
            orderDetailViewData.setCommercialPolicyEffectiveDate(DateUtils.getDateString(
                    insurance.getEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            orderDetailViewData.setCommercialPolicyExpireDate(DateUtils.getDateString(
                    insurance.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            orderDetailViewData.setCommercialPremium(insurance.getPremium());
            orderDetailViewData.setThirdPartyAmount(insurance.getThirdPartyAmount());
            orderDetailViewData.setThirdPartyPremium(insurance.getThirdPartyPremium());
            orderDetailViewData.setScratchAmount(insurance.getScratchAmount());
            orderDetailViewData.setScratchPremium(insurance.getScratchPremium());
            orderDetailViewData.setDamageAmount(insurance.getDamageAmount());
            orderDetailViewData.setDamagePremium(insurance.getDamagePremium());
            orderDetailViewData.setTheftAmount(insurance.getTheftAmount());
            orderDetailViewData.setTheftPremium(insurance.getTheftPremium());
            orderDetailViewData.setDriverAmount(insurance.getDriverAmount());
            orderDetailViewData.setDriverPremium(insurance.getDriverPremium());
            orderDetailViewData.setPassengerAmount(insurance.getPassengerAmount());
            orderDetailViewData.setPassengerPremium(insurance.getPassengerPremium());
            orderDetailViewData.setSpontaneousLossAmount(insurance.getSpontaneousLossAmount());
            orderDetailViewData.setSpontaneousLossPremium(insurance.getSpontaneousLossPremium());
            orderDetailViewData.setEnginePremium(insurance.getEnginePremium());
            orderDetailViewData.setGlassPremium(insurance.getGlassPremium());
            orderDetailViewData.setGlassType(insurance.getInsurancePackage().getGlassType() == null ? "" :
                    insurance.getInsurancePackage().getGlassType().getName());
            orderDetailViewData.setUnableFindThirdPartyPremium(insurance.getUnableFindThirdPartyPremium());
            orderDetailViewData.setIop(insurance.getIopTotal());
            orderDetailViewData.setDesignatedRepairShopPremium(insurance.getDesignatedRepairShopPremium());
            orderDetailViewData.setInsuranceImage(getResourceAbsoluteUrl(insurance.getInsuranceImage()));
            orderDetailViewData.setApplicantIdentityTypeId(insurance.getApplicantIdentityType() != null ? insurance.getApplicantIdentityType().getId() : 1);
            orderDetailViewData.setInsuredIdentityTypeId(insurance.getInsuredIdentityType() != null ? insurance.getInsuredIdentityType().getId() : 1);
        } else {
            orderDetailViewData.setCommercialPolicyNo("");
            orderDetailViewData.setCommercialPolicyEffectiveDate("");
            orderDetailViewData.setCommercialPolicyExpireDate("");
            orderDetailViewData.setCommercialPremium(quoteRecord.getPremium());
            orderDetailViewData.setCompulsoryPremium(quoteRecord.getCompulsoryPremium());
            orderDetailViewData.setAutoTax(quoteRecord.getAutoTax());
            orderDetailViewData.setThirdPartyAmount(quoteRecord.getThirdPartyAmount());
            orderDetailViewData.setThirdPartyPremium(quoteRecord.getThirdPartyPremium());
            orderDetailViewData.setScratchAmount(quoteRecord.getScratchAmount());
            orderDetailViewData.setScratchPremium(quoteRecord.getScratchPremium());
            orderDetailViewData.setDamageAmount(quoteRecord.getDamageAmount());
            orderDetailViewData.setDamagePremium(quoteRecord.getDamagePremium());
            orderDetailViewData.setTheftAmount(quoteRecord.getTheftAmount());
            orderDetailViewData.setTheftPremium(quoteRecord.getTheftPremium());
            orderDetailViewData.setDriverAmount(quoteRecord.getDriverAmount());
            orderDetailViewData.setDriverPremium(quoteRecord.getDriverPremium());
            orderDetailViewData.setPassengerAmount(quoteRecord.getPassengerAmount());
            orderDetailViewData.setPassengerPremium(quoteRecord.getPassengerPremium());
            orderDetailViewData.setSpontaneousLossAmount(quoteRecord.getSpontaneousLossAmount());
            orderDetailViewData.setSpontaneousLossPremium(quoteRecord.getSpontaneousLossPremium());
            orderDetailViewData.setEnginePremium(quoteRecord.getEnginePremium());
            orderDetailViewData.setGlassPremium(quoteRecord.getGlassPremium());
            orderDetailViewData.setGlassType(quoteRecord.getInsurancePackage().getGlassType() == null ? "" :
                    quoteRecord.getInsurancePackage().getGlassType().getName());
            orderDetailViewData.setUnableFindThirdPartyPremium(quoteRecord.getUnableFindThirdPartyPremium());
            orderDetailViewData.setIop(quoteRecord.getIopTotal());
            orderDetailViewData.setDesignatedRepairShopPremium(quoteRecord.getDesignatedRepairShopPremium());
        }

        if (compulsoryInsurance != null) {
            orderDetailViewData.setCompulsoryPolicyNo(compulsoryInsurance.getPolicyNo());
            orderDetailViewData.setCompulsoryPolicyEffectiveDate(DateUtils.getDateString(
                    compulsoryInsurance.getEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            orderDetailViewData.setCompulsoryPolicyExpireDate(DateUtils.getDateString(
                    compulsoryInsurance.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            orderDetailViewData.setCompulsoryPremium(compulsoryInsurance.getCompulsoryPremium());
            orderDetailViewData.setAutoTax(compulsoryInsurance.getAutoTax());
            orderDetailViewData.setCompulsoryInsuranceImage(getResourceAbsoluteUrl(compulsoryInsurance.getInsuranceImage()));
            orderDetailViewData.setCompulsoryStamp(getResourceAbsoluteUrl(compulsoryInsurance.getStamp()));
            orderDetailViewData.setApplicantIdentityTypeId(compulsoryInsurance.getApplicantIdentityType() != null ? compulsoryInsurance.getApplicantIdentityType().getId() : 1);
            orderDetailViewData.setInsuredIdentityTypeId(compulsoryInsurance.getInsuredIdentityType() != null ? compulsoryInsurance.getInsuredIdentityType().getId() : 1);
        } else {
            orderDetailViewData.setCompulsoryPolicyNo("");
            orderDetailViewData.setCompulsoryPolicyEffectiveDate("");
            orderDetailViewData.setCompulsoryPolicyExpireDate("");
            orderDetailViewData.setCompulsoryPremium(quoteRecord.getCompulsoryPremium());
            orderDetailViewData.setAutoTax(quoteRecord.getAutoTax());
        }
        if (quoteRecord.getInsuranceCompany().equals(InsuranceCompany.Enum.ZHONGAN_50000) ||
                quoteRecord.getInsuranceCompany().equals(InsuranceCompany.Enum.SINOSAFE_205000)) {
            orderDetailViewData.setSupportAmend(false);
        }

        //设置投保人和被保人信息
        setInsuredData(orderDetailViewData, purchaseOrder, insurance, compulsoryInsurance);
    }


    /**
     * 订单信息（不包括险种信息）
     *
     * @param orderDetailViewData
     * @param purchaseOrder
     */
    public void getPurchaseOrderDetail(OrderDetailViewData orderDetailViewData, PurchaseOrder purchaseOrder) {
        AutoType autoType = purchaseOrder.getAuto().getAutoType();

        //前端是否展示泛华出单机构
        if (purchaseOrder.getOrderSourceType() != null && purchaseOrder.getOrderSourceType().getId().equals(OrderSourceType.Enum.PLANTFORM_BX_5.getId())) {
            orderDetailViewData.setSpecialRemarks("fanhua");
        }

        /* 车辆信息 */
        orderDetailViewData.setLicenseNo(purchaseOrder.getAuto().getLicensePlateNo());
        orderDetailViewData.setVinNo(purchaseOrder.getAuto().getVinNo());
        orderDetailViewData.setEngineNo(purchaseOrder.getAuto().getEngineNo());
        orderDetailViewData.setEnrollDate(DateUtils.getDateString(
                purchaseOrder.getAuto().getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        orderDetailViewData.setModelName(autoType == null ? "" : autoType.getModel());
        orderDetailViewData.setSeats(autoType == null ? "" :
                (null == purchaseOrder.getAuto().getAutoType().getSeats() ? "" : purchaseOrder.getAuto().getAutoType().getSeats().toString()));
        orderDetailViewData.setCode(autoType == null ? "" : autoType.getCode());

        /* 报价补充信息 */
        orderDetailViewData.setSupplementInfos(LackOfSupplementInfoHandler.toOrderCenterFormat(quoteSupplementInfoService.getSupplementInfosByPurchaseOrder(purchaseOrder)));

        /* 车主信息 */
        orderDetailViewData.setOwnerName(purchaseOrder.getAuto().getOwner());
        orderDetailViewData.setOwnerIdentityType(purchaseOrder.getAuto().getIdentityType().getName());
        orderDetailViewData.setOwnerIdentity(purchaseOrder.getAuto().getIdentity());

        /* 订单信息 */
        orderDetailViewData.setOrderNo(purchaseOrder.getOrderNo());
        orderDetailViewData.setOrderStatus(null == purchaseOrder.getStatus() ? "" : purchaseOrder.getStatus().getStatus());
        orderDetailViewData.setPayableAmount(purchaseOrder.getPayableAmount());
        Double paidAmount = purchaseOrderAmendService.getPaidAmountByOrderId(purchaseOrder.getId());
        orderDetailViewData.setPaidAmount(paidAmount);

        /* 支付信息 */
        List<PaymentChannel> paymentChannels = new ArrayList<PaymentChannel>();
        orderDetailViewData.setPaymentChannel("online");
        if (PaymentChannel.Enum.ALIPAY_OFFLINE_PAY_50.getId().equals(purchaseOrder.getChannel().getId()) || PaymentChannel.Enum.ACCOUNT_OFFLINE_PAY_51.getId().equals(purchaseOrder.getChannel().getId())) {
            orderDetailViewData.setPaymentChannel("offline");
        }
        paymentChannels.add(PaymentChannel.Enum.ALIPAY_1);
        paymentChannels.add(PaymentChannel.Enum.UNIONPAY_3);
        paymentChannels.add(PaymentChannel.Enum.WECHAT_4);
        Payment payment = paymentRepository.findFirstByChannelInAndPurchaseOrderOrderByIdDesc(paymentChannels, purchaseOrder);
        if (payment != null && payment.getStatus().getId().equals(PaymentStatus.Enum.PAYMENTSUCCESS_2.getId())) {
            orderDetailViewData.setPayStatus("paid");
        } else {
            orderDetailViewData.setPayStatus("notPaid");
        }

        /* 配送信息 */
        orderDetailViewData.setAddressInfo(purchaseOrder.getDeliveryAddress());
        orderDetailViewData.setReceiver(purchaseOrder.getDeliveryAddress().getName());
//        orderDetailViewData.setOwnerMobile(purchaseOrder.getDeliveryAddress().getMobile());
        orderDetailViewData.setSendDate(purchaseOrder.getSendDate() == null ? "" : DateUtils.getDateString(
                purchaseOrder.getSendDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        orderDetailViewData.setTimePeriod(purchaseOrder.getTimePeriod() == null ? "" : purchaseOrder.getTimePeriod());
        orderDetailViewData.setAddress(addressService.getAddress(purchaseOrder));
        orderDetailViewData.setReceiverMobile(purchaseOrder.getDeliveryAddress().getMobile());

        /* 保险公司信息 */
        InsuranceCompany insuranceCompany = quoteRecordService.getById(purchaseOrder.getObjId()).getInsuranceCompany();
        orderDetailViewData.setInsuranceCompany(insuranceCompany.getName());
        orderDetailViewData.setInsuranceCompanyId(insuranceCompany.getId());


        /* 微信用户信息 */
        WechatUserInfo wechatUserInfo = wechatUserInfoRepository.findFirstByUser(purchaseOrder.getApplicant());
        if (wechatUserInfo != null && StringUtils.isNotBlank(wechatUserInfo.getNickname())) {
            orderDetailViewData.setNickName(wechatUserInfo.getNickname());
        } else {
            orderDetailViewData.setNickName("");
        }
        if (purchaseOrder.getApplicant() == null || purchaseOrder.getApplicant().getMobile() == null) {
            orderDetailViewData.setUserMobile("");
        } else {
            orderDetailViewData.setUserMobile(purchaseOrder.getApplicant().getMobile());
        }
        orderDetailViewData.setSource(purchaseOrderService.getUserSource(purchaseOrder));
        orderDetailViewData.setPlatform(purchaseOrder.getSourceChannel() == null ? "" : purchaseOrder.getSourceChannel().getDescription());

        /* 礼品信息 */
        orderDetailViewData.setGiftDetails(purchaseOrderGiftService.getGiftInfo(purchaseOrder.getId(), purchaseOrder));

        /* 快递信息 */
        orderDetailViewData.setTrackingNo(purchaseOrder.getTrackingNo());
        orderDetailViewData.setExpressCompany(purchaseOrder.getDeliveryInfo() == null ?
                "" : purchaseOrder.getDeliveryInfo().getExpressCompany());

        orderDetailViewData.setSourceId(purchaseOrder.getSourceChannel() == null ? null : purchaseOrder.getSourceChannel().getId());
        orderDetailViewData.setAreaId(purchaseOrder.getArea().getId());

        //费率佣金信息
        InsurancePurchaseOrderRebate purchaseOrderRebate = orderRebateRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (null != purchaseOrderRebate) {
            Agent agent = (null != purchaseOrderRebate.getUpChannelId()) ?
                    agentRepository.findOne(purchaseOrderRebate.getUpChannelId()) : null;
            Institution institution = (null != purchaseOrderRebate.getDownChannelId()) ?
                    institutionRepository.findOne(purchaseOrderRebate.getDownChannelId()) : null;
            orderDetailViewData.setRebates(OrderDetailViewData.organizeRebateList(purchaseOrderRebate, agent, institution));
        }

        //照片状态信息
        PurchaseOrderImageStatus purchaseOrderImageStatus = purchaseOrderImageStatusRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (purchaseOrderImageStatus == null) {
            orderDetailViewData.setOrderImageStatus(PurchaseOrderImage.STATUS.STATUS_MAP.get(PurchaseOrderImage.STATUS.UPLOAD));
        } else {
            orderDetailViewData.setOrderImageStatus(PurchaseOrderImage.STATUS.STATUS_MAP.get(purchaseOrderImageStatus.getStatus()));
        }
        orderDetailViewData.setStatusDisplay(purchaseOrder.getStatusDisplay());

        //邀请人信息
        User inviterInfo = userService.findInviterByPurchaseOrderId(purchaseOrder.getId());
        if (inviterInfo != null && inviterInfo.getName() != null) {
            orderDetailViewData.setInviter(inviterInfo.getName());
        }

        //间接邀请人信息
        User indirectInviterInfo = userService.findTopInviterByByPurchaseOrderId(purchaseOrder.getId());
        if (indirectInviterInfo != null && indirectInviterInfo.getName() != null) {
            orderDetailViewData.setIndirectInviter(indirectInviterInfo.getName());
        }

        //邀请人奖励金额
        ChannelAgentPurchaseOrderRebate capo = channelAgentOrderRebateRepository.findInviterAward(purchaseOrder.getId());
        Double inviterAward = 0.00;
        if (capo != null) {
            if (capo.getCommercialAmount() != null) {
                inviterAward += capo.getCommercialAmount();
            }
            if (capo.getCompulsoryAmount() != null) {
                inviterAward += capo.getCompulsoryAmount();
            }
        }
        orderDetailViewData.setInviterAward(inviterAward);

        //间接邀请人奖励金额
        ChannelAgentPurchaseOrderRebate capoo = channelAgentOrderRebateRepository.findIndirectionAward(purchaseOrder.getId());
        Double indirectInviterAward = 0.00;
        if (capoo != null) {
            if (capoo.getCompulsoryAmount() != null) {
                indirectInviterAward += capoo.getCompulsoryAmount();
            }
            if (capoo.getCommercialAmount() != null) {
                indirectInviterAward += capoo.getCommercialAmount();
            }
        }
        orderDetailViewData.setIndirectInviterAward(indirectInviterAward);
    }

    private void setInsuredData(OrderDetailViewData orderDetailViewData, PurchaseOrder purchaseOrder, Insurance insurance, CompulsoryInsurance compulsoryInsurance) {
        Auto auto = purchaseOrder.getAuto();
        orderDetailViewData.setInsuredName(auto.getOwner());
        orderDetailViewData.setInsuredIdentity(auto.getIdentity());
        if (auto.getIdentityType() == null) {
            orderDetailViewData.setInsuredIdentityType(IdentityType.Enum.OTHER_IDENTIFICATION.getName());
            //orderDetailViewData.setInsuredIdentityTypeId(IdentityType.Enum.OTHER_IDENTIFICATION.getId());
        } else {
            orderDetailViewData.setInsuredIdentityType(auto.getIdentityType().getName());
            //orderDetailViewData.setInsuredIdentityTypeId(auto.getIdentityType().getId());
        }
        if (insurance != null) {
            orderDetailViewData.setInsuredName(StringUtils.isBlank(insurance.getInsuredName()) ? auto.getOwner() : insurance.getInsuredName());
            orderDetailViewData.setInsuredIdentity(StringUtils.isBlank(insurance.getInsuredIdNo()) ? auto.getIdentity() : insurance.getInsuredIdNo());
            orderDetailViewData.setApplicantName(StringUtils.isBlank(insurance.getApplicantName()) ? auto.getOwner() : insurance.getApplicantName());
            orderDetailViewData.setApplicantIdNo(StringUtils.isBlank(insurance.getApplicantIdNo()) ? auto.getIdentity() : insurance.getApplicantIdNo());
            orderDetailViewData.setInsuredIdentityType(insurance.getInsuredIdentityType() == null ? IdentityType.Enum.OTHER_IDENTIFICATION.getName() : insurance.getInsuredIdentityType().getName());
            orderDetailViewData.setApplicantIdentityType(insurance.getApplicantIdentityType() == null ? IdentityType.Enum.OTHER_IDENTIFICATION.getName() : insurance.getApplicantIdentityType().getName());
        } else if (compulsoryInsurance != null) {
            orderDetailViewData.setInsuredName(StringUtils.isBlank(compulsoryInsurance.getInsuredName()) ? auto.getOwner() : compulsoryInsurance.getInsuredName());
            orderDetailViewData.setInsuredIdentity(StringUtils.isBlank(compulsoryInsurance.getInsuredIdNo()) ? auto.getIdentity() : compulsoryInsurance.getInsuredIdNo());
            orderDetailViewData.setApplicantName(StringUtils.isBlank(compulsoryInsurance.getApplicantName()) ? auto.getOwner() : compulsoryInsurance.getApplicantName());
            orderDetailViewData.setApplicantIdNo(StringUtils.isBlank(compulsoryInsurance.getApplicantIdNo()) ? auto.getIdentity() : compulsoryInsurance.getApplicantIdNo());
            orderDetailViewData.setInsuredIdentityType(compulsoryInsurance.getInsuredIdentityType() == null ? IdentityType.Enum.OTHER_IDENTIFICATION.getName() : compulsoryInsurance.getInsuredIdentityType().getName());
            orderDetailViewData.setApplicantIdentityType(compulsoryInsurance.getApplicantIdentityType() == null ? IdentityType.Enum.OTHER_IDENTIFICATION.getName() : compulsoryInsurance.getApplicantIdentityType().getName());
        }
    }

    public ModelAndViewResult setOrderPaymentStatus(String selectedVals) {
        ModelAndViewResult result = new ModelAndViewResult();
        String[] id = selectedVals.substring(0, selectedVals.length() - 1).split(",");
        try {
            for (int i = 0; i < id.length; i++) {
                //修改purchase_order表
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(Long.valueOf(id[i]));
                purchaseOrder.setStatus(OrderStatus.Enum.PAID_3);
                purchaseOrder.setUpdateTime(new Date());
                purchaseOrder.setOperator(internalUserManageService.getCurrentInternalUser());
                purchaseOrderRepository.save(purchaseOrder);
                //修改payment表
                Payment payment = paymentRepository.findFirstByPurchaseOrder(purchaseOrder);
                payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
                payment.setUpdateTime(new Date());
                payment.setOperator(internalUserManageService.getCurrentInternalUser());
                paymentRepository.save(payment);
                //修改order_operation_info表
                OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
                orderOperationInfo.setOriginalStatus(orderOperationInfo.getCurrentStatus());//把目前状态替换到原有状态
                //orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.INPUTINSURANCE);//新的目前状态：录入保单
                orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.PAID_AND_FINISH_ORDER);//新的目前状态：录入保单
                orderOperationInfo.setUpdateTime(new Date());
                orderOperationInfo.setOperator(internalUserManageService.getCurrentInternalUser());
                orderOperationInfoRepository.save(orderOperationInfo);
            }
            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
            result.setCode(ModelAndViewResult.RESULT_SUCCESS_COMMON_CODE);
        } catch (Exception e) {
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setCode(ModelAndViewResult.RESULT_FAIL_ONE_KEY_PROCESS_CODE);
            result.setMessage(ModelAndViewResult.RESULT_FAIL_ONE_KEY_PROCESS_MESSAGE);
        }
        return result;
    }

    public ModelAndViewResult changeOrderPaymentChannel(Long orderId) {
        ModelAndViewResult result = new ModelAndViewResult();
        try {
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(orderId);
            List<PaymentChannel> paymentChannelList = new ArrayList<PaymentChannel>();
            Payment payment = paymentRepository.findFirstByChannelInAndPurchaseOrderOrderByIdDesc(paymentChannelList, purchaseOrder);
            OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);

            purchaseOrder.setUpdateTime(new Date());
            purchaseOrder.setOperator(internalUserManageService.getCurrentInternalUser());
            purchaseOrder.appendDescription("支付方式由线下支付改为线上支付");
            purchaseOrder.setChannel(PaymentChannel.Enum.WECHAT_4);// 修改支付方式,默认为微信支付

            payment.setUpdateTime(new Date());
            payment.setOperator(internalUserManageService.getCurrentInternalUser());
            payment.setComments(payment.getComments() + ",支付方式由线下支付改为线上支付");
            payment.setChannel(PaymentChannel.Enum.WECHAT_4);// 修改支付方式,默认为微信支付

            orderOperationInfo.setUpdateTime(new Date());
            orderOperationInfo.setOperator(internalUserManageService.getCurrentInternalUser());

            purchaseOrderRepository.save(purchaseOrder);
            paymentRepository.save(payment);
            orderOperationInfoRepository.save(orderOperationInfo);

            // 保存操作日志
            saveChangeOrderPaymentChannelApplicationLog(purchaseOrder.getId(), payment.getId());

            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
        } catch (Exception e) {
            logger.error("更新线上订单的支付渠道失败");
            result.setResult(ModelAndViewResult.RESULT_FAIL);
        }

        return result;
    }

    public void getPaymentInfoDetail(OrderDetailViewData orderDetailViewData, Long orderId) {
        List<Long> excludeType = Arrays.asList(PaymentType.Enum.DISCOUNT_5.getId(), PaymentType.Enum.CHECHEPAY_6.getId(), PaymentType.Enum.DAILY_RESTART_PAY_7.getId());
        List<Payment> payments = paymentRepository.findPaymentInfoByOrderId(orderId, excludeType);
        if (CollectionUtils.isNotEmpty(payments))
            orderDetailViewData.setPaymentInfos(PaymentInfoViewModel.changeObjArrToPaymentInfoViewModel(payments));
    }

    //是否显示停复驶按钮
    private boolean enableDailyInsuranceBtn(PurchaseOrder purchaseOrder) {
        return purchaseOrder.getStatus().getId().equals(OrderStatus.Enum.FINISHED_5.getId())
                && quoteRecordService.getById(purchaseOrder.getObjId()).getInsuranceCompany().getId().equals(ANSWERN_65000.getId())
                && dailyInsuranceRepository.countByPurchaseOrder(purchaseOrder) > 0;
    }


    private void isRealQuote(PurchaseOrder purchaseOrder, OrderDetailViewData viewData) {
        QuoteRecord quoteRecord = quoteRecordService.getById(purchaseOrder.getObjId());
        viewData.setQuoteType(quoteFlowConfigService.quoteType(quoteRecord.getType()));
        viewData.setSupportChangeStatus(quoteConfigService.isSupportManualQuote(purchaseOrder.getSourceChannel(),purchaseOrder.getArea(),quoteRecord.getInsuranceCompany()));
    }

    public OrderDetailViewData findQuoteRecordByPurchaseOrderHistoryId(Long orderHistoryId) {
        OrderDetailViewData orderDetailViewData = new OrderDetailViewData();
        PurchaseOrderHistory history = purchaseOrderHistoryRepository.findOne(orderHistoryId);
        QuoteRecord quoteRecord = quoteRecordService.getById(history.getObjId());
        String insuranceCompanyName = quoteRecord.getInsuranceCompany().getName();
        OrderDetailViewData.createByPurchaseOrder(orderDetailViewData, history, insuranceCompanyName);
        OrderDetailViewData.getRecordInsuranceDetail(orderDetailViewData, quoteRecord);
        orderDetailViewData.setGiftDetails(purchaseOrderGiftService.getHistoryGiftInfo(history));
        return orderDetailViewData;
    }

    public List<AmendQuoteRecordViewModel> findAmendRecordsByOrderId(Long orderId) {
        List<Object[]> objects = quoteRecordRepository.findAmendRecordsByOrderId(orderId);
        List<AmendQuoteRecordViewModel> list = new ArrayList<AmendQuoteRecordViewModel>();
        if (CollectionUtils.isNotEmpty(objects))
            list = AmendQuoteRecordViewModel.changeObjArrToAmendQuoteRecordViewModel(objects);
        return list;
    }

    public DailyInsuranceViewModel findTotalByPurchaseOrderId(Long orderId) {
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(orderId);
        DailyInsuranceViewModel dailyInsuranceViewModel = new DailyInsuranceViewModel();
        List<Long> status = Arrays.asList(DailyInsuranceStatus.Enum.STOP_APPLY.getId(), DailyInsuranceStatus.Enum.STOPPED.getId(), DailyInsuranceStatus.Enum.RESTARTED.getId());
        List<DailyInsurance> dailyInsuranceList = dailyInsuranceRepository.findByPurchaseOrderAndStatusByIdDesc(purchaseOrder, status);
        Double totalRefundAmount = 0.0;
        Double totalPaidAmount = 0.0;
        Long totalStopDays = 0l;
        Long totalRestartDays = 0l;
        for (DailyInsurance dailyInsurance : dailyInsuranceList) {
            totalRefundAmount += dailyInsurance.getTotalRefundAmount();
            List<DailyRestartInsurance> dailyRestartInsuranceList = dailyRestartInsuranceRepository.findAllByDailyInsurance(dailyInsurance);
            for (DailyRestartInsurance dailyRestartInsurance : dailyRestartInsuranceList) {
                totalPaidAmount += dailyRestartInsurance.getPaidAmount();
                totalRestartDays += DateUtils.getDaysBetween(dailyRestartInsurance.getEndDate(), dailyRestartInsurance.getBeginDate()) + 1;
            }
            totalStopDays += DateUtils.getDaysBetween(dailyInsurance.getEndDate(), dailyInsurance.getBeginDate()) + 1;
            dailyInsuranceViewModel.setStatus(DailyInsuranceStatus.mapStatus(dailyInsuranceList.get(0).getStatus()).getDescription());
        }
        dailyInsuranceViewModel.setTotalStopDays(totalStopDays - totalRestartDays);
        dailyInsuranceViewModel.setTotalRestartDays(totalRestartDays);
        dailyInsuranceViewModel.setTotalPaidAmount(totalPaidAmount);
        dailyInsuranceViewModel.setTotalRefundAmount(totalRefundAmount);
        dailyInsuranceViewModel.setPremium(purchaseOrder.getPaidAmount() + totalPaidAmount - totalRefundAmount);
        return dailyInsuranceViewModel;
    }

    public Page<DailyInsurance> getDailyInsuranceOrdersByPage(PublicQuery query, Long orderId) {
        Pageable pageable = super.buildPageable(query.getCurrentPage(), query.getPageSize(), Sort.Direction.DESC, "endDate");
        return this.findDailyInsuranceBySpecAndPaginate(pageable, orderId);
    }

    private Page<DailyInsurance> findDailyInsuranceBySpecAndPaginate(Pageable pageable, Long orderId) {
        return dailyInsuranceRepository.findAll((Specification<DailyInsurance>) (root, query, cb) -> {
            CriteriaQuery<DailyInsurance> criteriaQuery = cb.createQuery(DailyInsurance.class);
            List<Predicate> predicateList = new ArrayList<Predicate>();
            Path<Long> purchaseOrderPath = root.get("purchaseOrder").get("id");
            CriteriaBuilder.In<Long> statusIdIn = cb.in(root.get("status").get("id"));
            statusIdIn.value(DailyInsuranceStatus.Enum.STOP_APPLY.getId());
            statusIdIn.value(DailyInsuranceStatus.Enum.STOPPED.getId());
            statusIdIn.value(DailyInsuranceStatus.Enum.RESTARTED.getId());
            predicateList.add(statusIdIn);
            predicateList.add(cb.equal(purchaseOrderPath, orderId));
            Predicate[] predicates = new Predicate[predicateList.size()];
            predicates = predicateList.toArray(predicates);
            return criteriaQuery.where(predicates).getRestriction();
        }, pageable);
    }

    public Map<Long, List<DailyRestartInsurance>> getDailyRestartInsurance(List<DailyInsurance> dailyInsuranceList) {
        Map dailyRestartMap = new HashMap();
        for (DailyInsurance dailyInsurance : dailyInsuranceList) {
            List<DailyRestartInsurance> dailyRestartInsuranceList = dailyRestartInsuranceRepository.findAllByDailyInsurance(dailyInsurance);
            dailyRestartMap.put(dailyInsurance.getId(), dailyRestartInsuranceList);
        }
        return dailyRestartMap;
    }

    public void getOderOperationInfoDetail(OrderDetailViewData orderDetailViewData, PurchaseOrder purchaseOrder) {
        OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
        orderDetailViewData.setCurrentStatus(orderOperationInfo.getCurrentStatus().getId());
        orderDetailViewData.setSupportAmend(orderOperationInfo.suppleAmend());
    }

    /**
     * 根据原配送地址进行查询，
     * 如果有关联其他订单，则新增地址，并绑定到要修改的订单上
     * 如果没有关联其他订单，则直接修改地址即可
     *
     * @param purchaseOrder
     */
    public Address updatePurchaseOrder(PurchaseOrderModifyView purchaseOrder) {
        //查询原送单地址对应的订单数
        int orderCount = purchaseOrderRepository.countPurchaseOrderByAddressId(purchaseOrder.getOriginalAddressId());
        //如果该地址关联了其他订单，则根据判断新增地址是否已存在，如果存在，则将存在的地址ID赋给订单，不存在，则创建地址并赋给订单ID
        PurchaseOrder order = purchaseOrderRepository.findOne(purchaseOrder.getOrderId());
        if (orderCount > 1) {
            Address address = addressService.save(purchaseOrder.getNewAddress());
            order.setDeliveryAddress(address);
            purchaseOrderService.saveOrder(order);
            return address;
        } else {
            //直接进行地址的修改
            Address newAddress = purchaseOrder.getNewAddress();
            Address originalAddress = order.getDeliveryAddress();
            String[] properties = new String[]{"province", "city", "district", "mobile", "name", "street"};
            BeanUtil.copyPropertiesContain(newAddress, originalAddress, properties);
            return addressService.save(originalAddress);
        }
    }

    /**
     * 保存状态变更工作日志
     *
     * @param orderId
     */
    private void saveChangeOrderPaymentChannelApplicationLog(Long orderId, Long paymentId) {
        MoApplicationLog orderApplicationLog = new MoApplicationLog();
        orderApplicationLog.setLogLevel(2);//日志的级别 1debug  2info 3warn 4error
        orderApplicationLog.setLogMessage("订单支付方式由线下支付变成线上支付");//日志信息
        orderApplicationLog.setLogType(LogType.Enum.CHANGE_ORDER_PAY_CHANNEL_20);//订单支付方式变更
        orderApplicationLog.setObjId(orderId + "");//对象id
        orderApplicationLog.setObjTable("purchase_order");//对象表名
        orderApplicationLog.setOpeartor(internalUserManageService.getCurrentInternalUser().getId());//操作人
        orderApplicationLog.setCreateTime(Calendar.getInstance().getTime());//创建时间
        doubleDBService.saveApplicationLog(orderApplicationLog);

        MoApplicationLog paymentApplicationLog = new MoApplicationLog();
        paymentApplicationLog.setLogLevel(2);//日志的级别 1debug  2info 3warn 4error
        paymentApplicationLog.setLogMessage("订单支付方式由线下支付变成线上支付");//日志信息
        paymentApplicationLog.setLogType(LogType.Enum.CHANGE_ORDER_PAY_CHANNEL_20);//订单支付方式变更
        paymentApplicationLog.setObjId(paymentId + "");//对象id
        paymentApplicationLog.setObjTable("payment");//对象表名
        paymentApplicationLog.setOpeartor(internalUserManageService.getCurrentInternalUser().getId());//操作人
        paymentApplicationLog.setCreateTime(Calendar.getInstance().getTime());//创建时间
        doubleDBService.saveApplicationLog(orderApplicationLog);
    }

    private String getResourceAbsoluteUrl(String insuranceImageUrl) {
        if (StringUtils.isBlank(insuranceImageUrl)) {
            return null;
        }
        String insurancePath = resourceService.getProperties().getInsurancePath();
        int index;
        if ((index = insuranceImageUrl.indexOf(insurancePath)) > -1) {
            return resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(insurancePath), insuranceImageUrl.substring(index + insurancePath.length(), insuranceImageUrl.length()));
        }
        return null;
    }

    public List<String> getDetailMessage(Long purchaseOrderId) {
        OrderTransmissionStatus status = orderOperationInfoService.getByPurchaseOrderId(purchaseOrderId).getCurrentStatus();

        if (status.getId().equals(OrderTransmissionStatus.Enum.UNCONFIRMED.getId())) {
            List<ThirdServiceFail> thirdServiceFailList = thirdServiceFailRepository.findByOrderIdOrderByCreateTimeDesc(purchaseOrderId);
            List<String> messageList = new ArrayList<>();
            thirdServiceFailList.forEach(thirdServiceFail -> {
                messageList.add(thirdServiceFail.getMessage());
            });
            return messageList;
        } else if (status.getId().equals(OrderTransmissionStatus.Enum.UNDERWRITING_FAILED.getId())) {
            List<MoApplicationLog> logList = applicationLogMongoRepository.findByObjIdAndLogTypeAndObjTableOrderByCreateTime(purchaseOrderId + "", LogType.Enum.INSURE_FAILURE_1.getId(), "purchase_order", new Sort(Sort.Direction.DESC, "createTime"));
            List<String> messageList = new ArrayList<>();
            logList.forEach(applicationLog -> {
                messageList.add(applicationLog.getLogMessage().toString());
            });
            return messageList;
        }
        return null;
    }

    @Autowired
    private AgentTmpRepository agentTmpRepository;

    @Autowired
    private OrderAgentRepository orderAgentRepository;

    private void getAgentRebateInfo(OrderDetailViewData orderDetailViewData, PurchaseOrder purchaseOrder) {


        //代理人费率
        List<Object[]> list = null;
        try {
            list = agentTmpRepository.findFanHuaAutoAgentByOrder(purchaseOrder.getId());
            if (CollectionUtils.isEmpty(list)) {
                list = agentTmpRepository.findFanHuaBaoChengAgentByOrder(purchaseOrder.getId());
            }
        } catch (Exception e) {
            logger.info("not in production environment");
            return;
        }

        String agentName = "";
        String cardNum = "";
        Double commercialAmount = 0.0;
        Double compulsoryAmount = 0.0;
        if (list.size() > 0) {
            Object[] obj = list.get(0);
            agentName = obj[0] == null ? "" : String.valueOf(obj[0]);
            commercialAmount = obj[1] == null ? commercialAmount : Double.valueOf(obj[1].toString());
            cardNum = obj[2] == null ? "" : String.valueOf(obj[2]);
        }

        if (list.size() > 1) {
            Object[] obj = list.get(1);
            agentName = obj[0] == null ? agentName : String.valueOf(obj[0]);
            compulsoryAmount = obj[1] == null ? compulsoryAmount : Double.valueOf(obj[1].toString());
            cardNum = obj[2] == null ? cardNum : String.valueOf(obj[2]);
        }
        if (StringUtils.isNotEmpty(agentName)) {
            orderDetailViewData.setAgentName(agentName);
            orderDetailViewData.setCardNum(cardNum);
            if (orderDetailViewData.getCommercialPremium() > 0 && commercialAmount > 0) {
                orderDetailViewData.setCommercialRebate(DoubleUtils.div(commercialAmount, orderDetailViewData.getCommercialPremium(), 3) * 100);
            }
            if (orderDetailViewData.getCompulsoryPremium() > 0 && compulsoryAmount > 0) {
                orderDetailViewData.setCompulsoryRebate(DoubleUtils.div(compulsoryAmount, orderDetailViewData.getCompulsoryPremium(), 3) * 100);
            }
            return;
        }

        if (StringUtils.isEmpty(orderDetailViewData.getAgentName()) && purchaseOrder.getApplicant() != null
                && purchaseOrder.getApplicant().getUserType() != null
                && purchaseOrder.getApplicant().getUserType().getId() == UserType.Enum.Agent.getId()) {
            OrderAgent orderAgent = orderAgentRepository.findByPurchaseOrder(purchaseOrder);
            if (orderAgent != null && orderAgent.getAgent() != null) {
                orderDetailViewData.setAgentName(orderAgent.getAgent().getName());
            }
        }
    }
}
