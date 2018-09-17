package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.cheche365.cheche.core.service.listener.EntityChangeListener
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.data.annotation.Version

import javax.persistence.*

/**
 * Created by mahong on 2016/9/22.
 */
@Entity
@EntityListeners(EntityChangeListener.class)
@JsonIgnoreProperties(ignoreUnknown = true, value = ["lastOrderHistory"])
class PurchaseOrderHistory extends DescribableEntity {
    private static final long serialVersionUID = 1L

    private OperationType operationType;
    private Date historyCreateTime;//历史表创建时间
    private PurchaseOrder purchaseOrder;
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
    private String orderNo;//订单号
    private int wechatPaymentCalledTimes; //微信支付调用次数
    private String wechatPaymentSuccessOrderNo; //微信支付成功微信端的订单号
    private int version;
    private Area area;
    private String trackingNo;  //快递单号
    private int audit;
    private OrderSourceType orderSourceType;//订单来源类型，区分CPS渠道，大客户，滴滴专车增补保险
    private String orderSourceId;//订单来源对象id
    private DeliveryInfo deliveryInfo;//快递信息
    private String comment;//备注信息

    @ManyToOne
    @JoinColumn(name = "operationType", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_OPERATION_TYPE", foreignKeyDefinition = "FOREIGN KEY (operation_type) REFERENCES operation_type(id)"))
    public OperationType getOperationType() {
        return operationType
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType
    }

    @ManyToOne
    @JoinColumn(name = "purchaseOrder", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_PURCHASE_ORDER", foreignKeyDefinition = "FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder
    }

    @Column(columnDefinition = "DATETIME")
    public Date getHistoryCreateTime() {
        return historyCreateTime
    }

    public void setHistoryCreateTime(Date historyCreateTime) {
        this.historyCreateTime = historyCreateTime
    }

    @ManyToOne
    @JoinColumn(name = "applicant", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_USER", foreignKeyDefinition = "FOREIGN KEY (applicant) REFERENCES user(id)"))
    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    @ManyToOne
    @JoinColumn(name = "auto", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_AUTO", foreignKeyDefinition = "FOREIGN KEY (auto) REFERENCES auto(id)"))
    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    @ManyToOne
    @JoinColumn(name = "type", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_ORDER_TYPE", foreignKeyDefinition = "FOREIGN KEY (type) REFERENCES order_type(id)"))
    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    @Column
    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_ORDER_STATUS", foreignKeyDefinition = "FOREIGN KEY (status) REFERENCES order_status(id)"))
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    @Column
    public int getWechatPaymentCalledTimes() {
        return wechatPaymentCalledTimes;
    }

    public void setWechatPaymentCalledTimes(int wechatPaymentCalledTimes) {
        this.wechatPaymentCalledTimes = wechatPaymentCalledTimes;
    }

    @Column(columnDefinition = "VARCHAR(27)")
    public String getWechatPaymentSuccessOrderNo() {
        return wechatPaymentSuccessOrderNo;
    }

    public void setWechatPaymentSuccessOrderNo(String wechatPaymentSuccessOrderNo) {
        this.wechatPaymentSuccessOrderNo = wechatPaymentSuccessOrderNo;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_PAYMENT_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES payment_channel(id)"))
    public PaymentChannel getChannel() {
        return channel;
    }

    public void setChannel(PaymentChannel channel) {
        this.channel = channel;
    }

    @ManyToOne
    @JoinColumn(name = "source_channel", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (source_channel) REFERENCES channel(id)"))
    public Channel getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(Channel sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    @ManyToOne
    @JoinColumn(name = "deliveryAddress", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_ADDRESS", foreignKeyDefinition = "FOREIGN KEY (delivery_address) REFERENCES address(id)"))
    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "VARCHAR(300)")
    public String getInvoiceHeader() {
        return invoiceHeader;
    }

    public void setInvoiceHeader(String invoiceHeader) {
        this.invoiceHeader = invoiceHeader;
    }

    @Column(columnDefinition = "DATE")
    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    @Column(columnDefinition = "VARCHAR(15)")
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Version
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Column(columnDefinition = "int(1)")
    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (`area`) REFERENCES `area` (`id`)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @ManyToOne
    @JoinColumn(name = "orderSourceType", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_ORDER_SOURCE_TYPE", foreignKeyDefinition = "FOREIGN KEY (`order_source_type`) REFERENCES `order_source_type` (`id`)"))
    public OrderSourceType getOrderSourceType() {
        return orderSourceType;
    }

    public void setOrderSourceType(OrderSourceType orderSourceType) {
        this.orderSourceType = orderSourceType;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getOrderSourceId() {
        return orderSourceId;
    }

    public void setOrderSourceId(String orderSourceId) {
        this.orderSourceId = orderSourceId;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    @ManyToOne
    @JoinColumn(name = "deliveryInfo", foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_HIS_REF_DELIVERY_INFO", foreignKeyDefinition = "FOREIGN KEY (`delivery_info`) REFERENCES `delivery_info` (`id`)"))
    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    @Column(columnDefinition = "VARCHAR(1000)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
