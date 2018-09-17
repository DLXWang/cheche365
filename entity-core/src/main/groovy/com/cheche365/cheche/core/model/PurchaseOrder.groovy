package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.model.agent.Customer
import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.cheche365.cheche.core.service.listener.EntityChangeListener
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.springframework.beans.BeanUtils
import org.springframework.data.annotation.Version

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType
import javax.persistence.ForeignKey
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.PrePersist
import javax.persistence.Transient
import java.beans.PropertyDescriptor

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSAFE_205000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ZHONGAN_50000
import static com.cheche365.cheche.core.model.OrderStatus.Enum.CANCELED_6
import static com.cheche365.cheche.core.model.OrderStatus.Enum.DELIVERED_4
import static com.cheche365.cheche.core.model.OrderStatus.Enum.EXPIRED_8
import static com.cheche365.cheche.core.model.OrderStatus.Enum.FINISHED_5
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.OrderStatus.Enum.REFUNDED_9
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1
import static com.cheche365.cheche.core.model.OrderStatus.Enum.REFUNDING_10
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_SOURCES
import static java.util.Calendar.DAY_OF_MONTH
import static org.apache.commons.lang3.time.DateUtils.truncatedCompareTo

@Entity
@EntityListeners(EntityChangeListener.class)
@JsonIgnoreProperties(ignoreUnknown = true, value = ["audit,subStatus"])
class PurchaseOrder extends DescribableEntity {
    private static final long serialVersionUID = 1L

    public static final int ORDER_NO_LENGTH = 19;//新的流水号去前19位

    private User applicant;
    private Auto auto;
    private OrderType type;//订单类型
    private Long objId;//对象Id：对车险的order，就是quote_record_id,对理赔的order，就是claim_id
    private OrderStatus status;//订单状态
    private OrderSubStatus subStatus;//订单状态
    private Double payableAmount;//应付金额
    private Double paidAmount; //实付金额

    private PaymentChannel channel;//付款渠道
    private Channel sourceChannel; //订单来源渠道，如微信，IOS_4，第三方
    private Address deliveryAddress;//快递地址

    private InternalUser operator;//操作人员
    private String invoiceHeader; //发票抬头

    private Date sendDate;//配送日期
    private String timePeriod;//配送时间段
    private Date expireTime //订单支付过期时间

    private String orderNo;//订单号

    private int wechatPaymentCalledTimes; //微信支付调用次数
    private String wechatPaymentSuccessOrderNo; //微信支付成功微信端的订单号

    private int version;

    private Area area;

    private String trackingNo;  //快递单号


    //transient fields
    @Transient
    @JsonIgnore
    private PurchaseOrder previous

    @Transient
    private Object giftId;//下单选择的代金券,老版本接口为Long，为支持出单中心再送礼品需求，改为List<Long>

    @Transient
    private List<Gift> realGifts;//根据订单生成的实物代金券id

    @Transient
    private String insuredName; //被保险人

    @Transient
    private String insuredIdNo; //被保险人证件号

    @Transient
    private IdentityType insuredIdentityType //被保险人证件类型

    @Transient
    private String applicantName;//投保人姓名    String 否  投保人姓名（汉字）

    @Transient
    private String applicantIdNo;//投保人证件号码 String 否  身份证号

    @Transient
    private IdentityType applicantIdentityType //投保人证件类型

    @Transient
    private Boolean skipInsure;//是否需要核保 true(需要核保) false(跳过核保步骤)

    @Transient
    private String ownerMobile;//代理人填写的车主手机号

    @Transient
    private Map<String, Object> additionalParameters;

    @Transient
    private Customer customer

    private int audit;


    private OrderSourceType orderSourceType;//订单来源类型，区分CPS渠道，大客户，滴滴专车增补保险
    private String orderSourceId;//订单来源对象id

    private DeliveryInfo deliveryInfo;//快递信息

    private String comment;//备注信息

    private String statusDisplay;//状态展示

    private String flow;//flow="reInsure"  为重复报价

    @JsonIgnore
    private OrderOperationInfo orderOperationInfo;

    public static PropertyDescriptor[] PO_DESCRIPTOR = BeanUtils.getPropertyDescriptors(PurchaseOrder.class)

    @Override
    void onCreate() {
        this.createTime ?: super.onCreate()
    }

    @Transient
    String getFlow() {
        return flow;
    }

    void setFlow(String flow) {
        this.flow = flow;
    }

    @Fetch(FetchMode.SELECT)
    @OneToOne(mappedBy = "purchaseOrder", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    OrderOperationInfo getOrderOperationInfo() {
        return orderOperationInfo;
    }

    void setOrderOperationInfo(OrderOperationInfo orderOperationInfo) {
        this.orderOperationInfo = orderOperationInfo;
    }

    @ManyToOne
    @JoinColumn(name = "applicant", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_USER", foreignKeyDefinition = "FOREIGN KEY (applicant) REFERENCES user(id)"))
    User getApplicant() {
        return applicant;
    }

    void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    @ManyToOne
    @JoinColumn(name = "auto", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_AUTO", foreignKeyDefinition = "FOREIGN KEY (auto) REFERENCES auto(id)"))
    Auto getAuto() {
        return auto;
    }

    void setAuto(Auto auto) {
        this.auto = auto;
    }

    @ManyToOne
    @JoinColumn(name = "type", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_ORDER_TYPE", foreignKeyDefinition = "FOREIGN KEY (type) REFERENCES order_type(id)"))
    OrderType getType() {
        return type;
    }

    void setType(OrderType type) {
        this.type = type;
    }

    @Column
    Long getObjId() {
        return objId;
    }

    void setObjId(Long objId) {
        this.objId = objId;
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_ORDER_STATUS", foreignKeyDefinition = "FOREIGN KEY (status) REFERENCES order_status(id)"))
    OrderStatus getStatus() {
        return status;
    }

    void setStatus(OrderStatus status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name = "sub_status", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_ORDER_SUB_STATUS", foreignKeyDefinition = "FOREIGN KEY (sub_status) REFERENCES order_sub_status(id)"))
    OrderSubStatus getOrderSubStatus() {
        return subStatus;
    }

    void setOrderSubStatus(OrderSubStatus subStatus) {
        this.subStatus = subStatus;
    }


    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getPayableAmount() {
        return payableAmount;
    }

    void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getPaidAmount() {
        return paidAmount;
    }

    void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    @Column
    int getWechatPaymentCalledTimes() {
        return wechatPaymentCalledTimes;
    }

    void setWechatPaymentCalledTimes(int wechatPaymentCalledTimes) {
        this.wechatPaymentCalledTimes = wechatPaymentCalledTimes;
    }

    @Column(columnDefinition = "VARCHAR(27)")
    String getWechatPaymentSuccessOrderNo() {
        return wechatPaymentSuccessOrderNo;
    }

    void setWechatPaymentSuccessOrderNo(String wechatPaymentSuccessOrderNo) {
        this.wechatPaymentSuccessOrderNo = wechatPaymentSuccessOrderNo;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_PAYMENT_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES payment_channel(id)"))
    PaymentChannel getChannel() {
        return channel;
    }

    void setChannel(PaymentChannel channel) {
        this.channel = channel;
    }

    @ManyToOne
    @JoinColumn(name = "source_channel", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (source_channel) REFERENCES channel(id)"))
    Channel getSourceChannel() {
        return sourceChannel;
    }

    void setSourceChannel(Channel sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    @ManyToOne
    @JoinColumn(name = "deliveryAddress", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_ADDRESS", foreignKeyDefinition = "FOREIGN KEY (delivery_address) REFERENCES address(id)"))
    Address getDeliveryAddress() {
        return deliveryAddress;
    }

    void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    InternalUser getOperator() {
        return operator;
    }

    void setOperator(InternalUser operator) {
        this.operator = operator;
    }


    @Column(columnDefinition = "VARCHAR(300)")
    String getInvoiceHeader() {
        return invoiceHeader;
    }

    void setInvoiceHeader(String invoiceHeader) {
        this.invoiceHeader = invoiceHeader;
    }

    @Column(columnDefinition = "DATE")
    Date getSendDate() {
        return sendDate;
    }

    void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getTimePeriod() {
        return timePeriod;
    }

    void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    @Column(columnDefinition = "VARCHAR(15)")
    String getOrderNo() {
        return orderNo;
    }

    void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Version
    int getVersion() {
        return version;
    }

    void setVersion(int version) {
        this.version = version;
    }

    @Transient
    PurchaseOrder getPreviousState() {
        return previous
    }

    void setPreviousState(PurchaseOrder previous) {
        this.previous = previous
    }

    @Transient
    Object getGiftId() {
        return giftId instanceof List ? giftId : ["" == giftId ? null : giftId];
    }

    void setGiftId(Object giftId) {
        this.giftId = giftId;
    }

    @Transient
    List<Gift> getRealGifts() {
        return realGifts;
    }

    void setRealGifts(List<Gift> realGifts) {
        this.realGifts = realGifts;
    }

    @Transient
    String getInsuredName() {
        return insuredName;
    }


    void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }



    @Transient
    String getInsuredIdNo() {
        return insuredIdNo;
    }

    void setInsuredIdNo(String insuredIdNo) {
        this.insuredIdNo = insuredIdNo;
    }

    @Transient
    IdentityType getInsuredIdentityType() {
        return insuredIdentityType
    }

    void setInsuredIdentityType(IdentityType insuredIdentityType) {
        this.insuredIdentityType = insuredIdentityType
    }

    @PrePersist
    void setDefaultAudit() {
        if (this.audit == 0) this.audit = 1;
    }

    @Column(columnDefinition = "int(1)")
    int getAudit() {
        return audit;
    }

    void setAudit(int audit) {
        this.audit = audit;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (`area`) REFERENCES `area` (`id`)"))
    Area getArea() {
        return area;
    }

    void setArea(Area area) {
        this.area = area;
    }


    String currentWechatOrderNo(String outTradeNo) {
        return outTradeNo + this.wechatPaymentCalledTimes;
    }


    static String toOrderNo(String orderNoFromWechat) {
        if (StringUtils.isBlank(orderNoFromWechat)) {
            return ""
        }

        return orderNoFromWechat.length() > ORDER_NO_LENGTH ? orderNoFromWechat.substring(0, ORDER_NO_LENGTH) : orderNoFromWechat;
    }

    boolean checkExpire() {
        return !this.expireTime || new Date().after(this.expireTime)
    }

    Date getExpireTime() {
        return this.expireTime
    }

    void setExpireTime(Date expireTime) {
        this.expireTime = expireTime
    }

    PurchaseOrder oneMoreWechatPaymentCall() {
        this.wechatPaymentCalledTimes++;
        return this;
    }

    @ManyToOne
    @JoinColumn(name = "orderSourceType", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_ORDER_SOURCE_TYPE", foreignKeyDefinition = "FOREIGN KEY (`order_source_type`) REFERENCES `order_source_type` (`id`)"))
    OrderSourceType getOrderSourceType() {
        return orderSourceType;
    }

    void setOrderSourceType(OrderSourceType orderSourceType) {
        this.orderSourceType = orderSourceType;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getOrderSourceId() {
        return orderSourceId;
    }

    void setOrderSourceId(String orderSourceId) {
        this.orderSourceId = orderSourceId;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    String getTrackingNo() {
        return trackingNo;
    }

    void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    @ManyToOne
    @JoinColumn(name = "deliveryInfo", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_REF_DELIVERY_INFO", foreignKeyDefinition = "FOREIGN KEY (`delivery_info`) REFERENCES `delivery_info` (`id`)"))
    DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    @Column(columnDefinition = "VARCHAR(1000)")
    String getComment() {
        return comment;
    }

    void setComment(String comment) {
        this.comment = comment;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    String getStatusDisplay() {
        return statusDisplay;
    }

    void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    @Transient
    String getApplicantName() {
        return applicantName;
    }

    void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    @Transient
    String getApplicantIdNo() {
        return applicantIdNo;
    }

    void setApplicantIdNo(String applicantIdNo) {
        this.applicantIdNo = applicantIdNo;
    }

    @Transient
    IdentityType getApplicantIdentityType() {
        return applicantIdentityType
    }

    void setApplicantIdentityType(IdentityType applicantIdentityType) {
        this.applicantIdentityType = applicantIdentityType
    }

    @Transient
    Boolean getSkipInsure() {
        return skipInsure;
    }

    void setSkipInsure(Boolean skipInsure) {
        this.skipInsure = skipInsure;
    }

    @Transient
    String getOwnerMobile() {
        return ownerMobile;
    }

    void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile;
    }

    @Transient
    Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }

    void setAdditionalParameters(Map<String, Object> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    @Transient
    Customer getCustomer() {
        return customer
    }

    void setCustomer(Customer customer) {
        this.customer = customer
    }

    Boolean answernFinished(QuoteRecord quoteRecord) {
        (quoteRecord.insuranceCompany == ANSWERN_65000 && statusFinished())
    }

    Boolean orderStatusAllowedTab(QuoteRecord quoteRecord) {
        ([PAID_3, DELIVERED_4] + (quoteRecord.type in PLATFORM_SOURCES ? [PENDING_PAYMENT_1,FINISHED_5] : []) + ((SINOSAFE_205000 == quoteRecord.insuranceCompany) ? [FINISHED_5] : [])).contains(this.status)
    }

    Boolean zaFinished(QuoteRecord quoteRecord){
        quoteRecord.insuranceCompany == ZHONGAN_50000 && statusFinished()
    }

    Boolean supportReQuote() {
        (IdentityType.Enum.IDENTITYCARD == this.auto?.identityType) && [CANCELED_6, EXPIRED_8].contains(this.status)
    }

    OrderStatus statusDisplay() {
        if (!this.statusDisplay) {
            return this.status
        }

        def statusDisplay = [REFUNDING_10, REFUNDED_9].contains(this.status) ? "${this.status.description}(${this.statusDisplay})" : this.statusDisplay
        return new OrderStatus(id: this.status.id, status: statusDisplay, description: statusDisplay)
    }

    Boolean statusFinished(){
        FINISHED_5 == this.status
    }

    Boolean dailyInsuranceOperationAllowed(){
        (
            (truncatedCompareTo(this.createTime, DateUtils.parseDate('2017-06-01', "yyyy-MM-dd"), DAY_OF_MONTH) < 0) ||
                (
                    truncatedCompareTo(this.createTime, DateUtils.parseDate('2017-07-13', "yyyy-MM-dd"), DAY_OF_MONTH) > 0 &&
                        truncatedCompareTo(this.createTime, DateUtils.parseDate('2018-09-05', "yyyy-MM-dd"), DAY_OF_MONTH) < 0
                )
        )
    }

    static class DiscountEnum {
        public static String PAYMENT_DISCOUNT = "paymentDiscount";
        public static String GIFT_DISCOUNT = "giftDiscount";
        public static String PAYMENT_DISCOUNT_TYPE = "paymentDiscountType";
        public static String PAYMENT_DISCOUNT_AMOUNT = "paymentDiscountAmount";
        public static String GIFT_DISCOUNT_TYPE = "giftDiscountType";
        public static String GIFT_DISCOUNT_AMOUNT = "giftDiscountAmount";
    }

}
