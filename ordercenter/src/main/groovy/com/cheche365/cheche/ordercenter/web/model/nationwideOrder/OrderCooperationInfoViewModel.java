package com.cheche365.cheche.ordercenter.web.model.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.AutoType;
import com.cheche365.cheche.core.model.OrderCooperationInfo;
import com.cheche365.cheche.core.model.OrderCooperationStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.ordercenter.constants.ReasonEnum;
import com.cheche365.cheche.ordercenter.web.model.InsuranceCompanyData;
import com.cheche365.cheche.ordercenter.web.model.area.AreaViewData;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by sunhuazhong on 2015/11/14.
 */
public class OrderCooperationInfoViewModel {
    private Long id;
    private String appointTime;//指定出单机构时间
    private String reason;//订单异常原因
    private Integer reasonId;//订单异常原因ID
    private Boolean rebateStatus;//确认佣金到账状态
    private Boolean auditStatus;//审核状态
    private Boolean incomeStatus;//收益状态
    private Boolean matchStatus;//险种保额匹配状态
    private String createTime;//创建时间
    private String updateTime;//修改时间
    private String operatorName;//最后操作人
    private String assignerName;//指定操作人

    private AreaViewData area;//区域
    private InsuranceCompanyData insuranceCompany;//保险公司
    private AreaContactInfoViewModel areaContactInfo;//车车分站
    private InstitutionViewModel institution;//出单机构
    private OrderCooperationStatus cooperationStatus;//订单状态
    private OrderInsuranceViewModel orderInsurance;//订单保单信息
    private InstitutionQuoteRecordViewModel quoteRecord;//出单机构报价
    private PurchaseOrderRefundViewModel refund;//退款信息
    private DeliveryInfoViewModel deliveryInfo;//快递信息

    private Long purchaseOrderId;//订单
    private String orderNo;//订单号
    private String owner;//车主
    private String licensePlateNo;//车牌
    private Double paidAmount;//实付金额
    private Double payableAmount;//应付金额
    private String paymentStatus;//支付状态
    private String orderCreateTime;//订单创建时间
    private String orderUpdateTime;//订单修改时间
    private String vinNo;//车架号
    private String engineNo;//发动机号
    private String enrollDate;//初登日期
    private String modelName;//车型
    private String ownerName;//车主姓名
    private String ownerIdentityType;//车主证件类型
    private String ownerIdentity;//车主证件号码
    private String address;//送单地址
    private String receiver;//收件人
    private String receiverMobile;//收件人手机号
    private String userMobile;//用户手机号
    private String nickName;//微信昵称
    private String source;//来源
    private String platform;//平台
    private String insuredName;//被保险人姓名
    private String insuredIdentityType;//被保险人证件类型
    private String insuredIdentity;//被保险人证件号
    private String giftDetails;//礼品详情
    private Long channel;//渠道号
    private String applicantName;//投保人姓名
    private String applicantIdNo;//投保人身份证号
    private String applicantIdentityType;//投保人证件类型

    private List<Map> supplementInfos;//车辆补充信息
    private String seats;//车座数
    private String code;//品牌型号
    private String channelIcon;//渠道LOGO

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(String appointTime) {
        this.appointTime = appointTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getReasonId() {
        return reasonId;
    }

    public void setReasonId(Integer reasonId) {
        this.reasonId = reasonId;
    }

    public Boolean getRebateStatus() {
        return rebateStatus;
    }

    public void setRebateStatus(Boolean rebateStatus) {
        this.rebateStatus = rebateStatus;
    }

    public Boolean getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Boolean auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Boolean getIncomeStatus() {
        return incomeStatus;
    }

    public void setIncomeStatus(Boolean incomeStatus) {
        this.incomeStatus = incomeStatus;
    }

    public Boolean getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(Boolean matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getAssignerName() {
        return assignerName;
    }

    public void setAssignerName(String assignerName) {
        this.assignerName = assignerName;
    }

    public AreaViewData getArea() {
        return area;
    }

    public void setArea(AreaViewData area) {
        this.area = area;
    }

    public InsuranceCompanyData getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompanyData insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public AreaContactInfoViewModel getAreaContactInfo() {
        return areaContactInfo;
    }

    public void setAreaContactInfo(AreaContactInfoViewModel areaContactInfo) {
        this.areaContactInfo = areaContactInfo;
    }

    public InstitutionViewModel getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionViewModel institution) {
        this.institution = institution;
    }

    public OrderCooperationStatus getCooperationStatus() {
        return cooperationStatus;
    }

    public void setCooperationStatus(OrderCooperationStatus cooperationStatus) {
        this.cooperationStatus = cooperationStatus;
    }

    public OrderInsuranceViewModel getOrderInsurance() {
        return orderInsurance;
    }

    public void setOrderInsurance(OrderInsuranceViewModel orderInsurance) {
        this.orderInsurance = orderInsurance;
    }

    public InstitutionQuoteRecordViewModel getQuoteRecord() {
        return quoteRecord;
    }

    public void setQuoteRecord(InstitutionQuoteRecordViewModel quoteRecord) {
        this.quoteRecord = quoteRecord;
    }

    public PurchaseOrderRefundViewModel getRefund() {
        return refund;
    }

    public void setRefund(PurchaseOrderRefundViewModel refund) {
        this.refund = refund;
    }

    public DeliveryInfoViewModel getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfoViewModel deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public String getOrderUpdateTime() {
        return orderUpdateTime;
    }

    public void setOrderUpdateTime(String orderUpdateTime) {
        this.orderUpdateTime = orderUpdateTime;
    }

    public String getVinNo() {
        return vinNo;
    }

    public void setVinNo(String vinNo) {
        this.vinNo = vinNo;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerIdentityType() {
        return ownerIdentityType;
    }

    public void setOwnerIdentityType(String ownerIdentityType) {
        this.ownerIdentityType = ownerIdentityType;
    }

    public String getOwnerIdentity() {
        return ownerIdentity;
    }

    public void setOwnerIdentity(String ownerIdentity) {
        this.ownerIdentity = ownerIdentity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    public String getInsuredIdentityType() {
        return insuredIdentityType;
    }

    public void setInsuredIdentityType(String insuredIdentityType) {
        this.insuredIdentityType = insuredIdentityType;
    }

    public String getInsuredIdentity() {
        return insuredIdentity;
    }

    public void setInsuredIdentity(String insuredIdentity) {
        this.insuredIdentity = insuredIdentity;
    }

    public String getGiftDetails() {
        return giftDetails;
    }

    public void setGiftDetails(String giftDetails) {
        this.giftDetails = giftDetails;
    }

    public Long getChannel() {
        return channel;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantIdNo() {
        return applicantIdNo;
    }

    public void setApplicantIdNo(String applicantIdNo) {
        this.applicantIdNo = applicantIdNo;
    }

    public String getApplicantIdentityType() {
        return applicantIdentityType;
    }

    public void setApplicantIdentityType(String applicantIdentityType) {
        this.applicantIdentityType = applicantIdentityType;
    }

    public List<Map> getSupplementInfos() {
        return supplementInfos;
    }

    public void setSupplementInfos(List<Map> supplementInfos) {
        this.supplementInfos = supplementInfos;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getChannelIcon() {
        return channelIcon;
    }

    public void setChannelIcon(String channelIcon) {
        this.channelIcon = channelIcon;
    }

    public static OrderCooperationInfoViewModel createViewModel(OrderCooperationInfo orderCooperationInfo, ResourceService resourceService) {
        OrderCooperationInfoViewModel viewModel = new OrderCooperationInfoViewModel();
        viewModel.setId(orderCooperationInfo.getId());
        viewModel.setAppointTime(orderCooperationInfo.getAppointTime() != null ?
            DateUtils.getDateString(orderCooperationInfo.getAppointTime(), DateUtils.DATE_LONGTIME24_PATTERN) : null);
        viewModel.setReason(orderCooperationInfo.getReason());
        ReasonEnum reasonEnum = ReasonEnum.formatByReason(orderCooperationInfo.getReason());
        viewModel.setReasonId(reasonEnum == null ? null : reasonEnum.getIndex());
        viewModel.setRebateStatus(orderCooperationInfo.getRebateStatus());
        viewModel.setAuditStatus(orderCooperationInfo.getAuditStatus());
        viewModel.setIncomeStatus(orderCooperationInfo.getIncomeStatus());
        viewModel.setMatchStatus(orderCooperationInfo.getMatchStatus());
        viewModel.setCreateTime(DateUtils.getDateString(orderCooperationInfo.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(orderCooperationInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));

        viewModel.setPurchaseOrderId(orderCooperationInfo.getPurchaseOrder().getId());
        viewModel.setOrderNo(orderCooperationInfo.getPurchaseOrder().getOrderNo());
        viewModel.setOwner(orderCooperationInfo.getPurchaseOrder().getAuto().getOwner());
        viewModel.setLicensePlateNo(orderCooperationInfo.getPurchaseOrder().getAuto().getLicensePlateNo());
        viewModel.setPaidAmount(orderCooperationInfo.getPurchaseOrder().getPaidAmount());
        viewModel.setPayableAmount(orderCooperationInfo.getPurchaseOrder().getPayableAmount());
        viewModel.setOrderCreateTime(DateUtils.getDateString(orderCooperationInfo.getPurchaseOrder().getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOrderUpdateTime(DateUtils.getDateString(orderCooperationInfo.getPurchaseOrder().getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        if(orderCooperationInfo.getPurchaseOrder().getSourceChannel()!=null){
            viewModel.setChannel(orderCooperationInfo.getPurchaseOrder().getSourceChannel().getId());
            if(!StringUtils.isEmpty(orderCooperationInfo.getPurchaseOrder().getSourceChannel().getIcon())){
                viewModel.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()),
                    orderCooperationInfo.getPurchaseOrder().getSourceChannel().getIcon()));
            }
        }
        viewModel.setAssignerName(orderCooperationInfo.getAssigner() != null ?
            orderCooperationInfo.getAssigner().getName() : "");
        viewModel.setOperatorName(orderCooperationInfo.getOperator() != null ?
            orderCooperationInfo.getOperator().getName() : "");

        viewModel.setCooperationStatus(orderCooperationInfo.getStatus());
        viewModel.setArea(AreaViewData.createViewModel(orderCooperationInfo.getArea()));
        viewModel.setInsuranceCompany(InsuranceCompanyData.createViewModel(orderCooperationInfo.getInsuranceCompany()));
        viewModel.setAreaContactInfo(AreaContactInfoViewModel.createViewModel(orderCooperationInfo.getAreaContactInfo()));
        viewModel.setInstitution(InstitutionViewModel.createViewModel(orderCooperationInfo.getInstitution()));
        return viewModel;
    }

    public static OrderCooperationInfoViewModel createDetailViewModel(OrderCooperationInfo orderCooperationInfo) {
        OrderCooperationInfoViewModel viewModel = new OrderCooperationInfoViewModel();
        /* 出单信息 */
        viewModel.setId(orderCooperationInfo.getId());
        viewModel.setAppointTime(orderCooperationInfo.getAppointTime() != null ?
            DateUtils.getDateString(orderCooperationInfo.getAppointTime(), DateUtils.DATE_LONGTIME24_PATTERN) : null);
        viewModel.setReason(orderCooperationInfo.getReason());
        ReasonEnum reasonEnum = ReasonEnum.formatByReason(orderCooperationInfo.getReason());
        viewModel.setReasonId(reasonEnum == null ? null : reasonEnum.getIndex());
        viewModel.setRebateStatus(orderCooperationInfo.getRebateStatus());
        viewModel.setAuditStatus(orderCooperationInfo.getAuditStatus());
        viewModel.setIncomeStatus(orderCooperationInfo.getIncomeStatus());
        viewModel.setMatchStatus(orderCooperationInfo.getMatchStatus());
        viewModel.setCreateTime(DateUtils.getDateString(orderCooperationInfo.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(orderCooperationInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setAssignerName(orderCooperationInfo.getAssigner() != null ?
            orderCooperationInfo.getAssigner().getName() : "");
        viewModel.setOperatorName(orderCooperationInfo.getOperator() != null ?
            orderCooperationInfo.getOperator().getName() : "");
        /* 订单信息 */
        PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
        viewModel.setPurchaseOrderId(purchaseOrder.getId());
        viewModel.setOrderNo(purchaseOrder.getOrderNo());
        viewModel.setPaidAmount(purchaseOrder.getPaidAmount());
        viewModel.setPayableAmount(purchaseOrder.getPayableAmount());
        viewModel.setOrderCreateTime(DateUtils.getDateString(purchaseOrder.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOrderUpdateTime(DateUtils.getDateString(purchaseOrder.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        /* 车辆信息 */
        AutoType autoType = purchaseOrder.getAuto().getAutoType();
        viewModel.setLicensePlateNo(purchaseOrder.getAuto().getLicensePlateNo());
        viewModel.setOwner(purchaseOrder.getAuto().getOwner());
        viewModel.setVinNo(purchaseOrder.getAuto().getVinNo());
        viewModel.setEngineNo(purchaseOrder.getAuto().getEngineNo());
        viewModel.setEnrollDate(DateUtils.getDateString(purchaseOrder.getAuto().getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        viewModel.setModelName(purchaseOrder.getAuto().getAutoType() == null ? "" : purchaseOrder.getAuto().getAutoType().getModel());
        viewModel.setSeats(autoType == null ? "" :
            (null == purchaseOrder.getAuto().getAutoType().getSeats() ? "" : purchaseOrder.getAuto().getAutoType().getSeats().toString()));
        viewModel.setCode(autoType == null ? "" : autoType.getCode());
        /* 车主信息 */
        viewModel.setOwnerName(purchaseOrder.getAuto().getOwner());
        viewModel.setOwnerIdentityType(purchaseOrder.getAuto().getIdentityType().getName());
        viewModel.setOwnerIdentity(purchaseOrder.getAuto().getIdentity());
        /* 配送信息 */
        viewModel.setReceiver(purchaseOrder.getDeliveryAddress().getName());
        viewModel.setReceiverMobile(purchaseOrder.getDeliveryAddress().getMobile());
        /* 用户信息 */
        viewModel.setUserMobile(purchaseOrder.getApplicant() == null ? "" : purchaseOrder.getApplicant().getMobile());
        viewModel.setPlatform(purchaseOrder.getSourceChannel() == null ? "" : purchaseOrder.getSourceChannel().getDescription());

        viewModel.setCooperationStatus(orderCooperationInfo.getStatus());
        viewModel.setArea(AreaViewData.createViewModel(orderCooperationInfo.getArea()));
        viewModel.setInsuranceCompany(InsuranceCompanyData.createViewModel(orderCooperationInfo.getInsuranceCompany()));
        viewModel.setInstitution(InstitutionViewModel.createViewModel(orderCooperationInfo.getInstitution()));

        return viewModel;
    }
}
