package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

import javax.persistence.*

/**
 * Created by mahong on 2016/12/23.
 * 按天买车险-提前复驶主表
 */
@Entity
@JsonIgnoreProperties(["dailyInsurance", "restartApplyId", "restartApplyNo", "restartOrderNo", "restartPolicyNo", "payment", "status", "createTime", "updateTime", "description"])
class DailyRestartInsurance extends DescribableEntity {
    private DailyInsurance dailyInsurance; // 停驶id
    private Date beginDate;                // 保单复驶开始日期
    private Date endDate;                  // 保单复驶结束日期
    private String restartApplyId;         // 复驶试算申请id，复驶申请后回填
    private String restartApplyNo;         // 复驶商业险申请单号，复驶申请后回填
    private Date effectiveDate;            // 商业险生效日期
    private Date expireDate;               // 商业险截止日期
    private Double payableAmount;          // 市场价
    private Double discountAmount;         // 折扣价
    private Double paidAmount;             // 实际应付价格
    private Double premium;                // 商业险保费
    private String restartOrderNo;         // 复驶成功新订单号，复驶确认后回填
    private String restartPolicyNo;        // 复驶成功新保单号，复驶支付回调成功后回填
    private Payment payment;               // 支付id
    private DailyInsuranceStatus status;   // 状态
    private int wechatPaymentCalledTimes;  //微信支付调用次数
    private int isSync;   //是否同步到安心标志   1-同步,  0-未同步

    @ManyToOne
    @JoinColumn(name = "daily_insurance", foreignKey = @ForeignKey(name = "FK_RESTART_INSURANCE_REF_DAILY_INSURANCE", foreignKeyDefinition = "FOREIGN KEY (`daily_insurance`) REFERENCES `daily_insurance` (`id`)"))
    DailyInsurance getDailyInsurance() {
        return dailyInsurance
    }

    void setDailyInsurance(DailyInsurance dailyInsurance) {
        this.dailyInsurance = dailyInsurance
    }

    @Column(columnDefinition = "DATE")
    Date getBeginDate() {
        return beginDate
    }

    void setBeginDate(Date beginDate) {
        this.beginDate = beginDate
    }

    @Column(columnDefinition = "DATE")
    Date getEndDate() {
        return endDate
    }

    void setEndDate(Date endDate) {
        this.endDate = endDate
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getRestartApplyId() {
        return restartApplyId
    }

    void setRestartApplyId(String restartApplyId) {
        this.restartApplyId = restartApplyId
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getRestartApplyNo() {
        return restartApplyNo
    }

    void setRestartApplyNo(String restartApplyNo) {
        this.restartApplyNo = restartApplyNo
    }

    @Column(columnDefinition = "DATE")
    Date getEffectiveDate() {
        return effectiveDate
    }

    void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate
    }

    @Column(columnDefinition = "DATE")
    Date getExpireDate() {
        return expireDate
    }

    void setExpireDate(Date expireDate) {
        this.expireDate = expireDate
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getPayableAmount() {
        return payableAmount
    }

    void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getDiscountAmount() {
        return discountAmount
    }

    void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getPaidAmount() {
        return paidAmount
    }

    void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getPremium() {
        return premium
    }

    void setPremium(Double premium) {
        this.premium = premium
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getRestartOrderNo() {
        return restartOrderNo
    }

    void setRestartOrderNo(String restartOrderNo) {
        this.restartOrderNo = restartOrderNo
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getRestartPolicyNo() {
        return restartPolicyNo
    }

    void setRestartPolicyNo(String restartPolicyNo) {
        this.restartPolicyNo = restartPolicyNo
    }

    @ManyToOne
    @JoinColumn(name = "payment", foreignKey = @ForeignKey(name = "FK_RESTART_INSURANCE_REF_PAYMENT", foreignKeyDefinition = "FOREIGN KEY (`payment`) REFERENCES `payment` (`id`)"))
    Payment getPayment() {
        return payment
    }

    void setPayment(Payment payment) {
        this.payment = payment
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey = @ForeignKey(name = "FK_RESTART_INSURANCE_REF_STATUS", foreignKeyDefinition = "FOREIGN KEY (`status`) REFERENCES `daily_insurance_status` (`id`)"))
    DailyInsuranceStatus getStatus() {
        return this.status
    }

    void setStatus(DailyInsuranceStatus status) {
        this.status = status
    }

    @Column
    public int getWechatPaymentCalledTimes() {
        return wechatPaymentCalledTimes;
    }

    public void setWechatPaymentCalledTimes(int wechatPaymentCalledTimes) {
        this.wechatPaymentCalledTimes = wechatPaymentCalledTimes;
    }

    private List<DailyRestartInsuranceDetail> restartInsuranceDetails = new ArrayList<DailyRestartInsuranceDetail>();

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "dailyRestartInsurance", fetch = FetchType.EAGER)
    List<DailyRestartInsuranceDetail> getRestartInsuranceDetails() {
        return restartInsuranceDetails
    }

    void setRestartInsuranceDetails(List<DailyRestartInsuranceDetail> restartInsuranceDetails) {
        this.restartInsuranceDetails = restartInsuranceDetails
    }

    public DailyRestartInsurance oneMoreWechatPaymentCall() {
        this.wechatPaymentCalledTimes++;
        return this;
    }

    public String currentTradeNo(Boolean wechatPay) {
        String currentTradeNo = this.getDailyInsurance().getOrderNo() + "T" + this.getRestartApplyId()
        return wechatPay ? currentTradeNo + Integer.toString(wechatPaymentCalledTimes) : currentTradeNo
    }

    int getIsSync() {
        return isSync
    }

    void setIsSync(int isSync) {
        this.isSync = isSync
    }

}
