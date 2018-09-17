package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.cheche365.cheche.core.service.listener.EntityChangeListener
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import javax.persistence.*

import static com.cheche365.cheche.core.model.PaymentStatus.Enum.*
import static com.cheche365.cheche.core.model.PaymentType.Enum.*

@Entity
@EntityListeners(EntityChangeListener.class)
@JsonIgnoreProperties( value = ["upstreamId,itpNo"])
public class Payment extends DescribableEntity {
    private static final long serialVersionUID = 1L

    private User user;
    private PurchaseOrder purchaseOrder;
    private Double amount;
    private PaymentChannel channel;//付款渠道
    private Channel clientType; //client端渠道, have to rename channel to clientType since channel column (PaymentChannel) already exists in this table.
    private String thirdpartyPaymentNo;//支付平台流水号，如支付宝
    private String itpNo;//聚合支付流水号，如众安订单号
    private PaymentStatus status;//付款状态：	1.未付款,2.付款成功,3.付款失败
    private String comments;
    private InternalUser operator;
    private String outTradeNo;//流水号
    private Payment upstreamId;
    private PaymentType paymentType;
    private String mchId;
    private String appId;
    private PurchaseOrderAmend purchaseOrderAmend;
    private PurchaseOrderHistory purchaseOrderHistory;
    private static Map<List<String>, String> DISPLAY_TEXT_MAPPING = null;

    @Transient
    @JsonIgnore
    private Payment previous

    @Transient
    Payment getPrevious() {
        return previous
    }

    void setPrevious(Payment previous) {
        this.previous = previous
    }

    @Override
    void onCreate() {
        this.createTime ?: super.onCreate()
    }

    @ManyToOne
    @JoinColumn(name = "user", foreignKey = @ForeignKey(name = "FK_PAYMENT_REF_USER", foreignKeyDefinition = "FOREIGN KEY (user) REFERENCES user(id)"))
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "purchaseOrder", foreignKey = @ForeignKey(name = "FK_PAYMENT_REF_PURCHASE_ORDER", foreignKeyDefinition = "FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }


    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_PAYMENT_REF_PAYMENT_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES payment_channel(id)"))
    public PaymentChannel getChannel() {
        return channel;
    }

    public void setChannel(PaymentChannel channel) {
        this.channel = channel;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getThirdpartyPaymentNo() {
        return thirdpartyPaymentNo;
    }

    public void setThirdpartyPaymentNo(String thirdpartyPaymentNo) {
        this.thirdpartyPaymentNo = thirdpartyPaymentNo;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getItpNo() {
        return itpNo;
    }

    public void setItpNo(String itpNo) {
        this.itpNo = itpNo;
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey = @ForeignKey(name = "FK_PAYMENT_REF_PAYMENT_STATUS", foreignKeyDefinition = "FOREIGN KEY (status) REFERENCES payment_status(id)"))
    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Column(columnDefinition = "VARCHAR(30)")
    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @ManyToOne
    @JoinColumn(name = "upstream_id", foreignKey = @ForeignKey(name = "payment_ibfk_7", foreignKeyDefinition = "FOREIGN KEY(uupstream_id) REFERENCES payment(id)"))
    public Payment getUpstreamId() {
        return this.upstreamId;
    }

    public void setUpstreamId(Payment upstreamId) {
        this.upstreamId = upstreamId;
    }

    @ManyToOne
    @JoinColumn(name = "payment_type", foreignKey = @ForeignKey(name = "payment_ibfk_8", foreignKeyDefinition = "FOREIGN KEY(payment_type) REFERENCES payment_type(id)"))
    public PaymentType getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }


    @ManyToOne
    @JoinColumn(name = "purchase_order_amend", foreignKey = @ForeignKey(name = "payment_ibfk_9", foreignKeyDefinition = "FOREIGN KEY(purchase_order_amend) REFERENCES purchase_order_amend(id)"))
    public PurchaseOrderAmend getPurchaseOrderAmend() {
        return this.purchaseOrderAmend;
    }

    public void setPurchaseOrderAmend(PurchaseOrderAmend purchaseOrderAmend) {
        this.purchaseOrderAmend = purchaseOrderAmend;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "purchase_order_history", foreignKey = @ForeignKey(name = "payment_ref_order_history", foreignKeyDefinition = "FOREIGN KEY(purchase_order_history) REFERENCES purchase_order_history(id)"))
    public PurchaseOrderHistory getPurchaseOrderHistory() {
        return purchaseOrderHistory;
    }

    public void setPurchaseOrderHistory(PurchaseOrderHistory purchaseOrderHistory) {
        this.purchaseOrderHistory = purchaseOrderHistory;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_PAYMENT_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }


    @ManyToOne
    @JoinColumn(name = "client_type", foreignKey = @ForeignKey(name = "FK_PAYMENT_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (client_type) REFERENCES channel(id)"))
    public Channel getClientType() {
        return clientType;
    }

    public void setClientType(Channel clientType) {
        this.clientType = clientType;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getMchId() {
        return mchId
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getAppId() {
        return appId
    }

    public void setAppId(String appId) {
        this.appId = appId
    }

    public void setMchId(String mchId) {
        this.mchId = mchId
    }

    Boolean reDriving() {
        return DAILY_RESTART_PAY_7 == this.paymentType
    }

    static Payment getDiscountPaymentTemplate(PurchaseOrder order, PaymentChannel paymentChannel) {
        Payment payment = new Payment();
        payment.setChannel(paymentChannel);
        payment.setComments(paymentChannel.getDescription());
        payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
        payment.setPaymentType(PaymentType.Enum.DISCOUNT_5);
        payment.setClientType(order.getSourceChannel());
        payment.purchaseOrder = order;
        payment.user = order.getApplicant();
        return payment;
    }

    public static Payment getPaymentTemplate(PurchaseOrder order) {
        Payment payment = new Payment();

        payment.setChannel(order.getChannel());
        payment.setComments(order.getChannel() == null ? null : order.getChannel().getDescription());
        payment.setCreateTime(Calendar.getInstance().getTime());
        payment.setStatus(PaymentStatus.Enum.NOTPAYMENT_1);
        payment.purchaseOrder = order;
        payment.setClientType(order.getSourceChannel());
        payment.user = order.getApplicant();
        payment.setPaymentType(PaymentType.Enum.INITIALPAYMENT_1);
        return payment;
    }

    public static Payment getPaymentTemplate(PurchaseOrderAmend amend) {
        Payment payment = getPaymentTemplate(amend.getPurchaseOrder());
        payment.setPurchaseOrderAmend(amend);
        payment.setPaymentType(amend.getPaymentType());
        if ([PaymentType.Enum.CHECHEPAY_6, PaymentType.Enum.BAOXIANPAY_8].contains(payment.getPaymentType())) {
            payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
            payment.setChannel(PaymentChannel.Enum.COUPONS_8);
            payment.setComments(PaymentChannel.Enum.COUPONS_8.getDescription());
        }
        return payment;
    }

    static customPayments(payments) {
        payments.findAll {
            [INITIALPAYMENT_1, ADDITIONALPAYMENT_2, PARTIALREFUND_3, FULLREFUND_4].contains(it.paymentType) &&
                it.status != CANCEL_4
        }
    }

    //根据payment的status和paymentType组合前端需要的文案
    public void toDisplayText() {
        if (!DISPLAY_TEXT_MAPPING) {  //由于依赖PaymentStatus和PaymentType初始化结束后才能初始化这部分，所以不能写到static块中
            synchronized (this) {
                if (!DISPLAY_TEXT_MAPPING) {  //加锁后检查
                    DISPLAY_TEXT_MAPPING = [
                            (PAY_TYPES)   : [
                                    (NOTPAYMENT_1)    : '等待付款',
                                    (PAYMENTSUCCESS_2): '已支付',
                                    (PAYMENTFAILED_3) : '支付失败'
                            ],
                            (REFUND_TYPES): [
                                    (NOTPAYMENT_1)    : '退款中',
                                    (PAYMENTSUCCESS_2): '退款成功',
                                    (PAYMENTFAILED_3) : '退款失败'
                            ]
                    ]
                }
            }

        }

        def displayText = DISPLAY_TEXT_MAPPING.find { it.key.contains(this.paymentType) }?.value.find {
            it.key == this.status
        }?.value
        if (displayText) {
            this.status = this.status.clone()  //防止修改系统常量的值(PaymentStatus.Enum中定义的)，所以需要clone一份，否则导致常量和库中加载的数据不一致
            this.status.status = displayText
        }
    }

}
