package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.core.model.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangfei on 2016/5/19.
 */
public class PurchaseOrderExtend {


    private User applicant;
    private Auto auto;
    private OrderType type;//订单类型
    private Long objId;//对象Id：对车险的order，就是quote_record_id,对理赔的order，就是claim_id
    private OrderStatus status;//订单状态

    private Double payableAmount;//应付金额
    private Double paidAmount; //实付金额

    private PaymentChannel channel;//付款渠道
    private Channel sourceChannel; //订单来源渠道，如微信，IOS_4，第三方
    private Address deliveryAddress;//快递地址

    private InternalUser operator;//操作人员
    private String invoiceHeader; //发票抬头

    private Date sendDate;//配送日期
    private String timePeriod;//配送时间段
    private Date expireTime; //订单支付过期时间

    private String orderNo;//订单号

    private int wechatPaymentCalledTimes; //微信支付调用次数
    private String wechatPaymentSuccessOrderNo; //微信支付成功微信端的订单号

    private int version;

    private Area area;

    private String trackingNo;  //快递单号

    private Object giftId;//下单选择的代金券,老版本接口为Long，为支持出单中心再送礼品需求，改为List<Long>

    private List<Gift> realGifts;//根据订单生成的实物代金券id

    private String insuredName; //被保险人

    private String insuredIdNo; //被保险人证件号

    private IdentityType insuredIdentityType; //被保险人证件类型

    private String applicantName;//投保人姓名    String 否  投保人姓名（汉字）

    private String applicantIdNo;//投保人证件号码 String 否  身份证号

    private IdentityType applicantIdentityType; //投保人证件类型

    private Boolean skipInsure;//是否需要核保 true(需要核保) false(跳过核保步骤)

    private String ownerMobile;//代理人填写的车主手机号

    private Map<String, Object> additionalParameters;

    private int audit;


    private OrderSourceType orderSourceType;//订单来源类型，区分CPS渠道，大客户，滴滴专车增补保险
    private String orderSourceId;//订单来源对象id

    private DeliveryInfo deliveryInfo;//快递信息

    private String comment;//备注信息

    private String flow;//flow="reInsure"  为重复报价

    private Double compulsoryPercent;//交强优惠幅度
    private Double commercialPercent;//商业优惠幅度
    private Integer isCheChePay;//车车付款
    private Integer premiumType;//优惠类型
    private Double giftAmount;//代金券金额
    private List<Map<String, String>> resendGiftList;//额外赠送礼品

    private OrderTransmissionStatus newTransmissionStatus;

    public OrderTransmissionStatus getNewTransmissionStatus() {
        return newTransmissionStatus;
    }

    public void setNewTransmissionStatus(OrderTransmissionStatus newTransmissionStatus) {
        this.newTransmissionStatus = newTransmissionStatus;
    }

    public List<Map<String, String>> getResendGiftList() {
        return resendGiftList;
    }

    public void setResendGiftList(List<Map<String, String>> resendGiftList) {
        this.resendGiftList = resendGiftList;
    }

    public Double getCompulsoryPercent() {
        return compulsoryPercent;
    }

    public void setCompulsoryPercent(Double compulsoryPercent) {
        this.compulsoryPercent = compulsoryPercent;
    }

    public Double getCommercialPercent() {
        return commercialPercent;
    }

    public void setCommercialPercent(Double commercialPercent) {
        this.commercialPercent = commercialPercent;
    }

    public Integer getIsCheChePay() {
        return isCheChePay;
    }

    public void setIsCheChePay(Integer isCheChePay) {
        this.isCheChePay = isCheChePay;
    }

    public Integer getPremiumType() {
        return premiumType;
    }

    public void setPremiumType(Integer premiumType) {
        this.premiumType = premiumType;
    }

    public Double getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(Double giftAmount) {
        this.giftAmount = giftAmount;
    }


    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public PaymentChannel getChannel() {
        return channel;
    }

    public void setChannel(PaymentChannel channel) {
        this.channel = channel;
    }

    public Channel getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(Channel sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    public String getInvoiceHeader() {
        return invoiceHeader;
    }

    public void setInvoiceHeader(String invoiceHeader) {
        this.invoiceHeader = invoiceHeader;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getWechatPaymentCalledTimes() {
        return wechatPaymentCalledTimes;
    }

    public void setWechatPaymentCalledTimes(int wechatPaymentCalledTimes) {
        this.wechatPaymentCalledTimes = wechatPaymentCalledTimes;
    }

    public String getWechatPaymentSuccessOrderNo() {
        return wechatPaymentSuccessOrderNo;
    }

    public void setWechatPaymentSuccessOrderNo(String wechatPaymentSuccessOrderNo) {
        this.wechatPaymentSuccessOrderNo = wechatPaymentSuccessOrderNo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public Object getGiftId() {
        return giftId instanceof List ? giftId : ("" == giftId ? null : giftId);
    }

    public void setGiftId(Object giftId) {
        this.giftId = giftId;
    }

    public List<Gift> getRealGifts() {
        return realGifts;
    }

    public void setRealGifts(List<Gift> realGifts) {
        this.realGifts = realGifts;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    public String getInsuredIdNo() {
        return insuredIdNo;
    }

    public void setInsuredIdNo(String insuredIdNo) {
        this.insuredIdNo = insuredIdNo;
    }

    public IdentityType getInsuredIdentityType() {
        return insuredIdentityType;
    }

    public void setInsuredIdentityType(IdentityType insuredIdentityType) {
        this.insuredIdentityType = insuredIdentityType;
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

    public IdentityType getApplicantIdentityType() {
        return applicantIdentityType;
    }

    public void setApplicantIdentityType(IdentityType applicantIdentityType) {
        this.applicantIdentityType = applicantIdentityType;
    }

    public Boolean getSkipInsure() {
        return skipInsure;
    }

    public void setSkipInsure(Boolean skipInsure) {
        this.skipInsure = skipInsure;
    }

    public String getOwnerMobile() {
        return ownerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile;
    }

    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(Map<String, Object> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    public OrderSourceType getOrderSourceType() {
        return orderSourceType;
    }

    public void setOrderSourceType(OrderSourceType orderSourceType) {
        this.orderSourceType = orderSourceType;
    }

    public String getOrderSourceId() {
        return orderSourceId;
    }

    public void setOrderSourceId(String orderSourceId) {
        this.orderSourceId = orderSourceId;
    }

    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public static class PremiumTypeEnum {
        public static Integer PERCENT = 0;
        public static Integer LIMIT = 1;
    }
}
